package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventCreateRes
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.CREATED
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Component
class MyHandler {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun createEvent(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(EventCreateReq::class.java)
            .doOnSuccess { log.info("$it") }
            .flatMap {
                ServerResponse
                    .status(CREATED)
                    .body(fromObject(EventCreateRes(
                        name = "Koiki Camp",
                        id = "5a226c2d7c245e14f33fc5a8",
                        createdAt = LocalDateTime.parse("2017-12-02T16:52:45.52"),
                        updatedAt = LocalDateTime.parse("2017-12-02T16:52:45.52"),
                        transferCurrency = Currency.getInstance("JPY")
                    )))
            }
    }

    fun foo(): Mono<ServerResponse> = ServerResponse.ok().body(fromObject(mapOf("msg" to "hello")))
}