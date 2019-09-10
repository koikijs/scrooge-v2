package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.Event
import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventDoc
import dev.koiki.scroogev2.event.EventRepository
import dev.koiki.scroogev2.group.*
import dev.koiki.scroogev2.scrooge.Scrooge
import dev.koiki.scroogev2.scrooge.ScroogeAddReq
import dev.koiki.scroogev2.scrooge.ScroogeDoc
import dev.koiki.scroogev2.scrooge.ScroogeRepository
import dev.koiki.scroogev2.transferamount.TransferAmountFactory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
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
    suspend fun readEvent(eventId: String): Event {
        val eventDoc: EventDoc = eventRepository.findById(eventId)

        val groups: List<Group> = groupRepository.findByEventId(eventDoc.id!!)
            .map { groupDoc ->
                val scrooges: List<Scrooge> = scroogeRepository.findByGroupId(groupDoc.id!!)
                    .map { scroogeDoc ->
                        Scrooge(
                            id = scroogeDoc.id!!,
                            memberName = scroogeDoc.memberName,
                            paidAmount = scroogeDoc.paidAmount,
                            currency = scroogeDoc.currency,
                            forWhat = scroogeDoc.forWhat
                        )
                    }
                    .toList()

                val transferAmounts = transferAmountFactory.create(scrooges, groupDoc.memberNames)

                Group(
                    id = groupDoc.id,
                    name = groupDoc.name,
                    scrooges = scrooges,
                    memberNames = groupDoc.memberNames,
                    transferAmounts = transferAmounts,
                    createdAt = groupDoc.createdAt,
                    updatedAt = groupDoc.updatedAt
                )
            }
            .toList()

        return Event(
            name = eventDoc.name,
            id = eventDoc.id,
            createdAt = eventDoc.createdAt,
            updatedAt = eventDoc.createdAt,
            groups = groups,
            transferCurrency = eventDoc.transferCurrency
        )
    }

    suspend fun createEvent(req: EventCreateReq): Event {
        val sysDateTime = LocalDateTime.now(ZoneOffset.UTC)

        val eventDoc: EventDoc = eventRepository.create(EventDoc(
            name = req.name,
            transferCurrency = req.transferCurrency,
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        groupRepository.create(GroupDoc(
            name = req.name,
            eventId = eventDoc.id!!,
            memberNames = listOf(),
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        return readEvent(eventDoc.id)
    }

    suspend fun addGroup(eventId: String, req: GroupAddReq): Event {
        val eventDoc: EventDoc = eventRepository.findById(eventId)

        val sysDateTime = LocalDateTime.now(ZoneOffset.UTC)

        groupRepository.create(GroupDoc(
            name = req.name,
            eventId = eventDoc.id!!,
            memberNames = listOf(),
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        return readEvent(eventDoc.id)
    }

    suspend fun addScrooge(groupId: String, req: ScroogeAddReq): Event {
        val groupDoc: GroupDoc = groupRepository.findById(groupId)

        if (req.memberName !in groupDoc.memberNames)
            throw RuntimeException("ooo")

        scroogeRepository.create(ScroogeDoc(
            groupId = groupId,
            memberName = req.memberName,
            paidAmount = req.paidAmount,
            currency = req.currency,
            forWhat = req.forWhat
        ))

        return readEvent(groupDoc.eventId)
    }

    suspend fun updateGroupName(groupId: String, reqBody: GroupNameReq): Event {
        val groupDoc: GroupDoc = groupRepository.findById(groupId)
        groupRepository.updateNameById(groupDoc.id!!, reqBody.name)

        return readEvent(groupDoc.eventId)
    }

    suspend fun addGroupMemberName(groupId: String, req: GroupMemberNameReq): Event {
        val groupDoc: GroupDoc = groupRepository.findById(groupId)

        groupRepository.addMemberNameById(groupDoc.id!!, req.memberName)

        return readEvent(groupDoc.eventId)
    }

    suspend fun removeGroupMemberName(groupId: String, req: GroupMemberNameReq): Event {
        val groupDoc: GroupDoc = groupRepository.findById(groupId)

        groupRepository.removeMemberNameById(groupDoc.id!!, req.memberName)
        //TODO remove scrooges by groupId and memberName

        return readEvent(groupDoc.eventId)
    }

    suspend fun deleteGroup(groupId: String): Event {
        val groupDoc: GroupDoc = groupRepository.findById(groupId)

        scroogeRepository.deleteByGroupId(groupDoc.id!!)
        groupRepository.deleteById(groupDoc.id)

        return readEvent(groupDoc.eventId)
    }

    suspend fun deleteScrooge(scroogeId: String): Event {
        val scroogeDoc: ScroogeDoc = scroogeRepository.findById(scroogeId)
        val groupDoc: GroupDoc = groupRepository.findById(scroogeDoc.groupId)

        scroogeRepository.deleteById(scroogeId)

        return readEvent(groupDoc.eventId)
    }
}