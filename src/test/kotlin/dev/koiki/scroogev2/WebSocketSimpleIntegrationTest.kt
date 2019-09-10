package dev.koiki.scroogev2

import com.fasterxml.jackson.databind.ObjectMapper
import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.Event
import dev.koiki.scroogev2.group.GroupAddReq
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.ReplayProcessor
import java.lang.Thread.sleep
import java.net.URI
import java.util.*
import kotlin.concurrent.thread

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WebSocketSimpleIntegrationTest(
    @LocalServerPort private val port: Int
) {
    @Autowired
    private lateinit var webTestClient: WebTestClient
    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun webSocketTest() = runBlocking {
        /**
         * (1) create an event
         */
        val res: Event = webTestClient.post()
            .uri("/events/_create")
            .body(EventCreateReq(
                name = "test",
                transferCurrency = Currency.getInstance("JPY")
            ))
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody<Event>()
            .returnResult()
            .responseBody ?: throw RuntimeException("response body is null")

        /**
         * (2) add a group to the event AFTER 2 sec at another thread!!
         */
        thread {
            sleep(2_000)
            webTestClient.post()
                .uri("/events/${res.id}/groups/_add")
                .body(GroupAddReq(
                    name = "ADD"
                ))
                .exchange()
                .expectStatus().is2xxSuccessful
        }

        /**
         * (3) wait for webSocket messages, and it will come after 2 sec
         */
        val retrieveCnt = 1
        val output: ReplayProcessor<String> = ReplayProcessor.create(retrieveCnt)
        ReactorNettyWebSocketClient().execute(URI("ws://localhost:$port/events?${res.id}")) {
            it.receive()
                .map { msg -> msg.payloadAsText }
                .take(retrieveCnt.toLong())
                .subscribeWith(output)
                .then()
        }.subscribe()

        val receivedMessages: List<String> = output.collectList().awaitSingle()

        assertAll(
            {
                assertThat(receivedMessages).hasSize(1)
            },
            {
                assertThatCode {
                    mapper.readValue(receivedMessages[0], Event::class.java)
                }.doesNotThrowAnyException()
            }
        )

        return@runBlocking
    }
}