package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventRes
import dev.koiki.scroogev2.group.GroupAddReq
import dev.koiki.scroogev2.group.GroupMemberNameReq
import dev.koiki.scroogev2.group.GroupNameReq
import dev.koiki.scroogev2.scrooge.ScroogeAddReq
import kotlinx.coroutines.FlowPreview
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@FlowPreview
@Component
class MyHandler(
    private val myService: MyService,
    private val myWebSocketHandler: MyWebSocketHandler
) {
    suspend fun createEvent(request: ServerRequest): ServerResponse {
        val req: EventCreateReq = request.awaitBody()

        val eventRes: EventRes = myService.createEvent(req)

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

        val eventRes: EventRes = myService.addGroup(eventId, req)
        myWebSocketHandler.publishMessage(eventRes)

        return ServerResponse
            .status(CREATED)
            .buildAndAwait()
    }

    suspend fun addScrooge(request: ServerRequest): ServerResponse {
        val groupId = request.pathVariable("groupId")
        val req: ScroogeAddReq = request.awaitBody()

        val eventRes: EventRes = myService.addScrooge(groupId, req)
        myWebSocketHandler.publishMessage(eventRes)

        return ServerResponse
            .status(CREATED)
            .buildAndAwait()
    }

    suspend fun updateGroupName(request: ServerRequest): ServerResponse {
        val groupId = request.pathVariable("groupId")
        val req: GroupNameReq = request.awaitBody()

        val eventRes: EventRes = myService.updateGroupName(groupId, req)
        myWebSocketHandler.publishMessage(eventRes)

        return ServerResponse
            .status(NO_CONTENT)
            .buildAndAwait()
    }

    suspend fun addGroupMemberName(request: ServerRequest): ServerResponse {
        val groupId = request.pathVariable("groupId")
        val req: GroupMemberNameReq = request.awaitBody()

        val eventRes: EventRes = myService.addGroupMemberName(groupId, req)
        myWebSocketHandler.publishMessage(eventRes)

        return ServerResponse
            .status(NO_CONTENT)
            .buildAndAwait()
    }

    suspend fun removeGroupMemberName(request: ServerRequest): ServerResponse {
        val groupId = request.pathVariable("groupId")
        val req: GroupMemberNameReq = request.awaitBody()

        val eventRes: EventRes = myService.removeGroupMemberName(groupId, req)
        myWebSocketHandler.publishMessage(eventRes)

        return ServerResponse
            .status(NO_CONTENT)
            .buildAndAwait()
    }

    suspend fun deleteGroup(request: ServerRequest): ServerResponse {
        val groupId = request.pathVariable("groupId")

        val eventRes: EventRes = myService.deleteGroup(groupId)
        myWebSocketHandler.publishMessage(eventRes)

        return ServerResponse
            .status(NO_CONTENT)
            .buildAndAwait()
    }

    suspend fun deleteScrooge(request: ServerRequest): ServerResponse {
        val scroogeId = request.pathVariable("scroogeId")

        val eventRes: EventRes = myService.deleteScrooge(scroogeId)
        myWebSocketHandler.publishMessage(eventRes)

        return ServerResponse
            .status(NO_CONTENT)
            .buildAndAwait()
    }
}