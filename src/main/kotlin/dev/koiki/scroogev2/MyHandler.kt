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
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.intellij.lang.annotations.Flow
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

@FlowPreview
@Component
class MyHandler(
    private val groupRepository: GroupRepository,
    private val scroogeRepository: ScroogeRepository,
    private val myService: MyService,
    private val myWebSocketHandler: MyWebSocketHandler
) {
    suspend fun createEvent(request: ServerRequest): ServerResponse {
        val req: EventCreateReq = request.awaitBody()

        val event: Event = myService.createEvent(req)
        val eventRes: EventRes = myService.readEvent(event.id!!)

        return ServerResponse
            .status(CREATED)
            .bodyAndAwait(eventRes)
    }

    @FlowPreview
    suspend fun readEvent(request: ServerRequest): ServerResponse {
        val eventId = request.pathVariable("eventId")

        val eventRes = myService.readEvent(eventId)

        return ServerResponse
            .status(OK)
            .bodyAndAwait(eventRes)
    }

    suspend fun addGroup(request: ServerRequest): ServerResponse {
        val eventId = request.pathVariable("eventId")
        val req: GroupAddReq = request.awaitBody()

        myService.addGroup(eventId, req)
        myWebSocketHandler.publishMessage(eventId)

        return ServerResponse
            .status(CREATED)
            .buildAndAwait()
    }

    suspend fun addScrooge(request: ServerRequest): ServerResponse {
        val groupId = request.pathVariable("groupId")
        val req: ScroogeAddReq = request.awaitBody()

        val group: Group = myService.addScrooge(groupId, req)
        myWebSocketHandler.publishMessage(group.eventId)

        return ServerResponse
            .status(CREATED)
            .buildAndAwait()
    }

    suspend fun updateGroupName(request: ServerRequest): ServerResponse {
        val groupId = request.pathVariable("groupId")
        val reqBody: GroupNameReq = request.awaitBody()

        val group = groupRepository.findById(groupId)

        groupRepository.updateNameById(group.id!!, reqBody.name)

        return ServerResponse
            .status(NO_CONTENT)
            .buildAndAwait()
    }

    suspend fun addGroupMemberName(request: ServerRequest): ServerResponse {
        val groupId = request.pathVariable("groupId")
        val reqBody: GroupMemberNameReq = request.awaitBody()

        val group = groupRepository.findById(groupId)

        groupRepository.addMemberNameById(group.id!!, reqBody.memberName)

        return ServerResponse
            .status(NO_CONTENT)
            .buildAndAwait()
    }

    suspend fun deleteGroup(request: ServerRequest): ServerResponse {
        val groupId = request.pathVariable("groupId")

        val group = groupRepository.findById(groupId)

        scroogeRepository.deleteByGroupId(group.id!!)
        groupRepository.deleteById(group.id)

        return ServerResponse
            .status(NO_CONTENT)
            .buildAndAwait()
    }
}