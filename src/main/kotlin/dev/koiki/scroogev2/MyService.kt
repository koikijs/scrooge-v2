package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.Event
import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventRepository
import dev.koiki.scroogev2.event.EventRes
import dev.koiki.scroogev2.group.Group
import dev.koiki.scroogev2.group.GroupAddReq
import dev.koiki.scroogev2.group.GroupRepository
import dev.koiki.scroogev2.group.GroupRes
import dev.koiki.scroogev2.scrooge.Scrooge
import dev.koiki.scroogev2.scrooge.ScroogeAddReq
import dev.koiki.scroogev2.scrooge.ScroogeRepository
import dev.koiki.scroogev2.scrooge.ScroogeRes
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
@FlowPreview
class MyService(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
    private val scroogeRepository: ScroogeRepository
) {
    suspend fun readEvent(eventId: String): EventRes {
        val event = eventRepository.findById(eventId)

        val groupsRes: List<GroupRes> = groupRepository.findByEventId(event.id!!)
            .map { group ->
                val scrooges: List<ScroogeRes> = scroogeRepository.findByGroupId(group.id!!)
                    .map { scrooge -> ScroogeRes(scrooge) }
                    .toList()

                GroupRes(group, scrooges)
            }
            .toList()

        return EventRes(event, groupsRes)
    }

    suspend fun createEvent(req: EventCreateReq): Event {
        val sysDateTime = LocalDateTime.now(ZoneOffset.UTC)

        val event: Event = eventRepository.create(Event(
            name = req.name,
            transferCurrency = req.transferCurrency,
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        groupRepository.create(Group(
            name = "Test",
            eventId = event.id!!,
            memberNames = listOf(),
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        return event
    }

    suspend fun addGroup(eventId: String, req: GroupAddReq) {
        val event = eventRepository.findById(eventId)

        val sysDateTime = LocalDateTime.now(ZoneOffset.UTC)

        groupRepository.create(Group(
            name = req.name,
            eventId = event.id!!,
            memberNames = listOf(),
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))
    }

    suspend fun addScrooge(groupId: String, req: ScroogeAddReq): Group {
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

        return group
    }
}