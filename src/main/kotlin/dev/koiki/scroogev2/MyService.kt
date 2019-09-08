package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.Event
import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventRepository
import dev.koiki.scroogev2.event.EventRes
import dev.koiki.scroogev2.group.*
import dev.koiki.scroogev2.scrooge.Scrooge
import dev.koiki.scroogev2.scrooge.ScroogeAddReq
import dev.koiki.scroogev2.scrooge.ScroogeRepository
import dev.koiki.scroogev2.scrooge.ScroogeRes
import dev.koiki.scroogev2.transferamount.TransferAmountFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
@FlowPreview
class MyService(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
    private val scroogeRepository: ScroogeRepository,
    private val transferAmountFactory: TransferAmountFactory
) {
    suspend fun readEvent(eventId: String): EventRes {
        val event = eventRepository.findById(eventId)

        val groupsRes: List<GroupRes> = groupRepository.findByEventId(event.id!!)
            .map { group ->
                val scrooges: List<ScroogeRes> = scroogeRepository.findByGroupId(group.id!!)
                    .map { scrooge -> ScroogeRes(scrooge) }
                    .toList()

                val transferAmounts = transferAmountFactory.create(scrooges, group.memberNames)

                GroupRes(group, scrooges, transferAmounts)
            }
            .toList()

        return EventRes(event, groupsRes)
    }

    suspend fun createEvent(req: EventCreateReq): EventRes {
        val sysDateTime = LocalDateTime.now(ZoneOffset.UTC)

        val event: Event = eventRepository.create(Event(
            name = req.name,
            transferCurrency = req.transferCurrency,
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        groupRepository.create(Group(
            name = req.name,
            eventId = event.id!!,
            memberNames = listOf(),
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        return readEvent(event.id)
    }

    suspend fun addGroup(eventId: String, req: GroupAddReq): EventRes {
        val event = eventRepository.findById(eventId)

        val sysDateTime = LocalDateTime.now(ZoneOffset.UTC)

        groupRepository.create(Group(
            name = req.name,
            eventId = event.id!!,
            memberNames = listOf(),
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        return readEvent(event.id)
    }

    suspend fun addScrooge(groupId: String, req: ScroogeAddReq): EventRes {
        val group = groupRepository.findById(groupId)

        if (req.memberName !in group.memberNames)
            throw RuntimeException("ooo")

        scroogeRepository.create(Scrooge(
            groupId = groupId,
            memberName = req.memberName,
            paidAmount = req.paidAmount,
            currency = req.currency,
            forWhat = req.forWhat
        ))

        return readEvent(group.eventId)
    }

    suspend fun updateGroupName(groupId: String, reqBody: GroupNameReq): EventRes {
        val group = groupRepository.findById(groupId)
        groupRepository.updateNameById(group.id!!, reqBody.name)

        return readEvent(group.eventId)
    }

    suspend fun addGroupMemberName(groupId: String, req: GroupMemberNameReq): EventRes {
        val group = groupRepository.findById(groupId)

        groupRepository.addMemberNameById(group.id!!, req.memberName)

        return readEvent(group.eventId)
    }

    suspend fun removeGroupMemberName(groupId: String, req: GroupMemberNameReq): EventRes {
        val group = groupRepository.findById(groupId)

        groupRepository.removeMemberNameById(group.id!!, req.memberName)
        //TODO remove scrooges by groupId and memberName

        return readEvent(group.eventId)
    }

    suspend fun deleteGroup(groupId: String): EventRes {
        val group = groupRepository.findById(groupId)

        scroogeRepository.deleteByGroupId(group.id!!)
        groupRepository.deleteById(group.id)

        return readEvent(group.eventId)
    }

    suspend fun deleteScrooge(scroogeId: String): EventRes {
        val scrooge: Scrooge = scroogeRepository.findById(scroogeId)
        val group: Group = groupRepository.findById(scrooge.groupId)

        scroogeRepository.deleteById(scroogeId)

        return readEvent(group.eventId)
    }
}