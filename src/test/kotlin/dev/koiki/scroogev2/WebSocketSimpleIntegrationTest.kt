package dev.koiki.scroogev2

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.StandardWebSocketClient
import reactor.core.publisher.Mono
import reactor.core.publisher.ReplayProcessor
import java.lang.Thread.sleep
import java.net.URI
import java.time.Duration
import kotlin.concurrent.thread

/**
https://www.google.com/search?ei=NEVuXZODJ4n8wQO1k6X4Cg&q=reactive+websockethandler+integration+test&oq=reactive+websockethandler+integration+test&gs_l=psy-ab.3..33i21.14168.18137..18240...0.0..0.110.1569.16j2......0....1..gws-wiz.......0i7i30i19j0i19j0i30j0i5i30j33i160.VXP9UZNxe_4&ved=0ahUKEwiT7fm7vbTkAhUJfnAKHbVJCa8Q4dUDCAo&uact=5
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WebSocketSimpleIntegrationTest(
    @LocalServerPort private val port: Int
) {
    @Autowired
    lateinit var webTestClient: WebTestClient
    private val webSocketClient = ReactorNettyWebSocketClient()

    @Test
    fun test() = runBlocking {
        val eventId = "123"

        val output: ReplayProcessor<String> = ReplayProcessor.create(2)

        val uri = URI("ws://localhost:$port/event-emitter?$eventId")

        thread {
            println("async start")
            sleep(2000)
            println("delay end")
            webTestClient.get().uri("/test").exchange().expectStatus().isNoContent
            webTestClient.get().uri("/test").exchange().expectStatus().isNoContent
        }

        val mono = webSocketClient.execute(uri) {
            it.receive()
                .map {
                    println("message received, $it")
                    it.payloadAsText
                }
                .take(2)
                .subscribeWith(output)
                .then()
        }.block()

        println("here i am222")
        println("here i am")

        val result = output.collectList().block()!!
        assertThat(result[0]).isEqualTo("hello!!")
        assertThat(result[1]).isEqualTo("hello!!")

        println("ohh")
    }
}