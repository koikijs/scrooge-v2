package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.Event
import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.group.Group
import dev.koiki.scroogev2.group.GroupAddReq
import dev.koiki.scroogev2.group.GroupMemberNameReq
import dev.koiki.scroogev2.group.GroupNameReq
import dev.koiki.scroogev2.scrooge.Scrooge
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
import org.springframework.web.reactive.function.BodyInserters.fromObject
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * This will simplify assertions.
 * https://github.com/joel-costigliola/assertj-core/issues/1002
 */
//TODO add readEvent test to confirm transferAmounts[]
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

        val res: Event = webTestClient.post()
            .uri("/events/_create")
            .body(fromObject(requestBody))
            .exchange()
            .expectStatus().isCreated
            .expectBody<Event>()
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
            .body(fromObject(requestBody))
            .exchange()
            .expectStatus().isCreated
            .expectBody().isEmpty

        val event: Event = fetchEvent(testId.eventId)

        assertThat(event.groups).hasSize(2)
        assertThat(event.groups[1].name).isEqualTo("ADD")
    }

    @Test
    fun `add member to group and add scrooge to group`() {
        val groupMemberNameReq = GroupMemberNameReq(
            memberName = "ninja"
        )
        webTestClient.patch()
            .uri("/groups/${testId.groupId}/_addMemberName")
            .body(fromObject(groupMemberNameReq))
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty


        val scroogeAddReq = ScroogeAddReq(
            memberName = "ninja",
            paidAmount = BigDecimal.TEN,
            currency = Currency.getInstance("JPY"),
            forWhat = "camp"
        )

        webTestClient.post()
            .uri("/groups/${testId.groupId}/scrooges")
            .body(fromObject(scroogeAddReq))
            .exchange()
            .expectStatus().isCreated
            .expectBody().isEmpty

        // confirm addition of scrooge
        var event = fetchEvent(testId.eventId)
        assertThat(event.groups[0].scrooges[0])
            .isEqualToIgnoringGivenFields(Scrooge(
                id = "xxx",
                memberName = "ninja",
                paidAmount = BigDecimal.TEN,
                currency = Currency.getInstance("JPY"),
                forWhat = "camp"
            ), "id")

        webTestClient.delete()
            .uri("/scrooges/${event.groups[0].scrooges[0].id}")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty

        // confirm removal of scrooge
        event = fetchEvent(testId.eventId)
        assertThat(event.groups[0].scrooges).isEmpty()
    }

    @Test
    fun `add member to group and remove member from group`() {
        webTestClient.patch()
            .uri("/groups/${testId.groupId}/_addMemberName")
            .body(fromObject(GroupMemberNameReq(
                memberName = "ninja"
            )))
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty

        webTestClient.patch()
            .uri("/groups/${testId.groupId}/_addMemberName")
            .body(fromObject(GroupMemberNameReq(
                memberName = "nabnab"
            )))
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty

        var event = fetchEvent(testId.eventId)
        assertThat(event.groups[0].memberNames).isEqualTo(listOf("ninja", "nabnab"))

        webTestClient.patch()
            .uri("/groups/${testId.groupId}/_removeMemberName")
            .body(fromObject(GroupMemberNameReq(
                memberName = "ninja"
            )))
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty

        event = fetchEvent(testId.eventId)
        assertThat(event.groups[0].memberNames).isEqualTo(listOf("nabnab"))
    }

    @Test
    fun `update group name`() {
        val requestBody = GroupNameReq(
            name = "Name is updated"
        )

        webTestClient.patch()
            .uri("/groups/${testId.groupId}/_updateName")
            .body(fromObject(requestBody))
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty

        val event = fetchEvent(testId.eventId)

        assertThat(event.groups[0].name).isEqualTo("Name is updated")
    }

    @Test
    fun `create an event`() {
        val requestBody = EventCreateReq(
            name = "Camp 2019",
            transferCurrency = Currency.getInstance("JPY")
        )

        val expectedBody = Event(
            id = "xxx",
            name = "Camp 2019",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            groups = listOf(
                Group(
                    id = "xxx",
                    name = "Camp 2019",
                    memberNames = listOf(),
                    scrooges = listOf(),
                    transferAmounts = listOf(),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
                )
            ),
            transferCurrency = Currency.getInstance("JPY")
        )

        webTestClient.post()
            .uri("/events/_create")
            .body(fromObject(requestBody))
            .exchange()
            .expectStatus().isCreated
            .expectBody<Event>()
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

    @Test
    fun `delete a group`() {
        webTestClient.delete()
            .uri("/groups/${testId.groupId}")
            .exchange()
            .expectStatus().isNoContent

        val event = fetchEvent(testId.eventId)

        assertThat(event.groups).isEmpty()
    }

    fun fetchEvent(eventId: String): Event =
        webTestClient.get()
            .uri("/events/$eventId")
            .exchange()
            .expectStatus().isOk
            .expectBody<Event>()
            .returnResult()
            .responseBody
            ?: throw RuntimeException("response body is null")

    data class TestId(
        val eventId: String,
        val groupId: String
    )
}