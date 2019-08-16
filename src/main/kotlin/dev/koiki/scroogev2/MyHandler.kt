package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventCreateRes
import dev.koiki.scroogev2.group.Group
import dev.koiki.scroogev2.group.GroupRepository
import dev.koiki.scroogev2.group.GroupRes
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.CREATED
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.LocalDateTime
import java.util.*

@Component
class MyHandler(
    private val groupRepository: GroupRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun createEvent(request: ServerRequest): ServerResponse {
        val req: EventCreateReq = request.awaitBody()

        log.info("request body: $req")

        val createRes = groupRepository.create(Group(
            name = "Test",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))
        log.info("create result: $createRes")

        val readRes = groupRepository.read(createRes.id!!)
        log.info("read result: $readRes")

        return ServerResponse
            .status(CREATED)
            .bodyAndAwait(EventCreateRes(
                name = "Koiki Camp",
                id = "5a226c2d7c245e14f33fc5a8",
                createdAt = LocalDateTime.parse("2017-12-02T16:52:45.52"),
                updatedAt = LocalDateTime.parse("2017-12-02T16:52:45.52"),
                transferCurrency = Currency.getInstance("JPY"),
                groups = listOf(
                    GroupRes(
                        id = "5a226c2d7c245e14f33fc5a8",
                        name = "Default",
                        scrooges = listOf(),
                        memberNames = listOf(),
                        createdAt = LocalDateTime.parse("2017-12-02T16:52:45.52"),
                        updatedAt = LocalDateTime.parse("2017-12-02T16:52:45.52")
                    )
                )
            ))
    }

    suspend fun foo(): ServerResponse =
        ServerResponse.ok()
            .bodyAndAwait(mapOf("msg" to "hello"))
}