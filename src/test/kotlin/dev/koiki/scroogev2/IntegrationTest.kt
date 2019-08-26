package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventRes
import dev.koiki.scroogev2.group.GroupRes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.LocalDateTime
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

        val expectedBody = EventRes(
            id = "xxx",
            name = "test",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            groups = listOf(
                GroupRes(
                    id = "xxx",
                    name = "Test",
                    memberNames = listOf(),
                    scrooges = listOf(),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            ),
            transferCurrency = Currency.getInstance("JPY")
        )

        webTestClient.post()
            .uri("/events/_create")
            .body(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<EventRes>()
            .consumeWith {
                assertThat(it.responseBody)
                    .isEqualToIgnoringGivenFields(expectedBody,
                        "id", "createdAt", "updatedAt", "groups")

                assertThat(it.responseBody!!.groups).hasSize(1)

                assertThat(it.responseBody!!.groups[0])
                    .isEqualToIgnoringGivenFields(expectedBody.groups[0],
                        "id", "createdAt", "updatedAt")
            }
    }
}