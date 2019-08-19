package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.Event
import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventCreateRes
import dev.koiki.scroogev2.event.EventRepository
import dev.koiki.scroogev2.group.Group
import dev.koiki.scroogev2.group.GroupRepository
import dev.koiki.scroogev2.group.GroupRes
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.CREATED
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.*

@Component
class MyHandler(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun createEvent(request: ServerRequest): ServerResponse {
        val req: EventCreateReq = request.awaitBody()
        val sysDateTime = LocalDateTime.now(UTC)

        val eventRes = eventRepository.create(Event(
            name = req.name,
            transferCurrency = req.transferCurrency,
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        val groupRes = groupRepository.create(Group(
            name = "Test",
            eventId = eventRes.id!!,
            memberNames = listOf(),
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ))

        val responseBody = EventCreateRes(
            name = eventRes.name,
            id = eventRes.id,
            createdAt = eventRes.createdAt,
            updatedAt = eventRes.updatedAt,
            transferCurrency = eventRes.transferCurrency,
            groups = listOf(
                GroupRes(
                    id = groupRes.id!!,
                    name = groupRes.name,
                    scrooges = listOf(),
                    memberNames = groupRes.memberNames,
                    createdAt = groupRes.createdAt,
                    updatedAt = groupRes.updatedAt
                )
            )
        )

        return ServerResponse
            .status(CREATED)
            .bodyAndAwait(responseBody)
    }

    suspend fun foo(): ServerResponse =
        ServerResponse.ok()
            .bodyAndAwait(mapOf("msg" to "hello"))
}