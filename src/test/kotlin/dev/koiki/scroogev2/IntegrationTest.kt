package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.EventRes
import dev.koiki.scroogev2.group.GroupAddReq
import dev.koiki.scroogev2.group.GroupRes
import dev.koiki.scroogev2.scrooge.ScroogeAddReq
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * This will simplify assertions.
 * https://github.com/joel-costigliola/assertj-core/issues/1002
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class IntegrationTest {
    @Autowired
    lateinit var webTestClient: WebTestClient
    lateinit var testId: TestId

    @BeforeEach
    fun `before each`() {
        val requestBody = EventCreateReq(
            name = "test",
            transferCurrency = Currency.getInstance("JPY")
        )

        val res: EventRes = webTestClient.post()
            .uri("/events/_create")
            .body(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<EventRes>()
            .returnResult()
            .responseBody ?: throw RuntimeException("response body is null")

        testId = TestId(
            eventId = res.id,
            groupId = res.groups[0].id
        )
    }

    @Test
    fun `add a group`() {
        val requestBody = GroupAddReq(
            name = "ADD"
        )

        webTestClient.post()
            .uri("/events/${testId.eventId}/groups/_add")
            .body(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<Map<String, Any>>()
            .consumeWith {
                assertThat(it.responseBody!!["groupId"]).isNotNull
            }

        val event: EventRes = fetchEvent(testId.eventId)

        assertThat(event.groups).hasSize(2)
        assertThat(event.groups[1].name).isEqualTo("ADD")
    }

    @Test
    fun `add a scrooge`() {
        val requestBody = ScroogeAddReq(
            memberName = "ninja",
            paidAmount = BigDecimal.TEN,
            currency = Currency.getInstance("JPY"),
            forWhat = "camp"
        )

        webTestClient.post()
            .uri("/events/${testId.eventId}/groups/${testId.groupId}/scrooges/_add")
            .body(requestBody)
            .exchange()
            .expectStatus().isCreated
            .expectBody<Map<String, Any>>()
            .consumeWith {
                assertThat(it.responseBody!!["scroogeId"]).isNotNull
            }

        // TODO
    }

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

    fun fetchEvent(eventId: String): EventRes =
        webTestClient.get()
            .uri("/events/$eventId")
            .exchange()
            .expectStatus().isOk
            .expectBody<EventRes>()
            .returnResult()
            .responseBody
            ?: throw RuntimeException("response body is null")

    data class TestId(
        val eventId: String,
        val groupId: String
    )
}