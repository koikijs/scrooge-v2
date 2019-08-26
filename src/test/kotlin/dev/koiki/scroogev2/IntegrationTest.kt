package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventRes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class IntegrationTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun `create an event`() {
        val requestBody = EventCreateReq(
            name = "test",
            transferCurrency = Currency.getInstance("JPY")
        )

        webTestClient.post()
            .uri("/events/_create")
            .body(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<EventRes>()
            .consumeWith {
                assertThat(it.responseBody).isNotNull
            }
    }
}