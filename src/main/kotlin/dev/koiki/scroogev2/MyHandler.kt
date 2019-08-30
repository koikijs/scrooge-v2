package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.Event
import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventRes
import dev.koiki.scroogev2.event.EventRepository
import dev.koiki.scroogev2.group.*
import dev.koiki.scroogev2.scrooge.Scrooge
import dev.koiki.scroogev2.scrooge.ScroogeAddReq
import dev.koiki.scroogev2.scrooge.ScroogeRepository
import dev.koiki.scroogev2.scrooge.ScroogeRes
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

@Component
class MyHandler(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
    private val scroogeRepository: ScroogeRepository,
    private val responseFactory: ResponseFactory
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

        val responseBody = EventRes(
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

    @FlowPreview
    suspend fun readEvent(request: ServerRequest): ServerResponse {
        val eventId = request.pathVariable("eventId")

        val event = eventRepository.findById(eventId)

        val groupRess: List<GroupRes> = groupRepository.findByEventId(event.id!!)
            .map { group ->
                val scrooges: List<ScroogeRes> = scroogeRepository.findByGroupId(group.id!!)
                    .map { scrooge -> ScroogeRes(scrooge) }
                    .toList()

                GroupRes(group, scrooges)
            }
            .toList()

        val eventRes = EventRes(event, groupRess)

        return ServerResponse
            .status(OK)
            .bodyAndAwait(eventRes)
    }

    suspend fun addGroup(request: ServerRequest): ServerResponse {
        val eventId = request.pathVariable("eventId")
        val req: GroupAddReq = request.awaitBody()

        val event = eventRepository.findById(eventId)

        val sysDateTime = LocalDateTime.now(UTC)
        val res = groupRepository.create(Group(
            name = req.name,
            eventId = event.id!!,
            memberNames = listOf(),
            createdAt = sysDateTime,
            updatedAt = sysDateTime
        ));

        return ServerResponse
            .status(CREATED)
            .bodyAndAwait(mapOf("groupId" to res.id!!))
    }

    suspend fun addScrooge(request: ServerRequest): ServerResponse {
        val eventId = request.pathVariable("eventId")
        val groupId = request.pathVariable("groupId")
        val reqBody: ScroogeAddReq = request.awaitBody()

        val group = groupRepository.findById(groupId)

        if (group.eventId != eventId)
            throw RuntimeException("eee")
        if (reqBody.memberName !in group.memberNames)
            throw RuntimeException("ooo")

        val scrooge = scroogeRepository.create(Scrooge(
            groupId = groupId,
            memberName = reqBody.memberName,
            paidAmount = reqBody.paidAmount,
            currency = reqBody.currency,
            forWhat = reqBody.forWhat
        ))

        return ServerResponse
            .status(CREATED)
            .bodyAndAwait(mapOf("scroogeId" to scrooge.id))
    }

    suspend fun updateGroupName(request: ServerRequest): ServerResponse {
        val eventId = request.pathVariable("eventId")
        val groupId = request.pathVariable("groupId")
        val reqBody: GroupNameReq = request.awaitBody()

        val group = groupRepository.findById(groupId)

        if (group.eventId != eventId)
            throw RuntimeException("eee")

        groupRepository.updateNameById(groupId, reqBody.name)

        return ServerResponse
            .status(NO_CONTENT)
            .buildAndAwait()
    }

    suspend fun foo(): ServerResponse =
        ServerResponse.ok()
            .bodyAndAwait(mapOf("msg" to "hello"))
}