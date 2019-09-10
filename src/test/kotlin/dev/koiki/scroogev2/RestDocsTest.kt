package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.EventCreateReq
import dev.koiki.scroogev2.event.Event
import dev.koiki.scroogev2.group.GroupAddReq
import dev.koiki.scroogev2.group.GroupMemberNameReq
import dev.koiki.scroogev2.group.GroupNameReq
import dev.koiki.scroogev2.scrooge.ScroogeAddReq
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.math.BigDecimal
import java.util.*

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestDocsTest(
    private val context: ApplicationContext
) {
    private lateinit var webTestClient: WebTestClient
    private lateinit var testId: TestId

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        this.webTestClient = WebTestClient
            .bindToApplicationContext(context)
            .configureClient()
            .filter(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint())
            )
            .build()

        val event = this.webTestClient
            .post()
            .uri("/events/_create")
            .body(EventCreateReq(
                name = "Koiki Camp",
                transferCurrency = Currency.getInstance("JPY")
            ))
            .exchange()
            .expectBody<Event>()
            .returnResult()
            .responseBody ?: throw RuntimeException("response body is null")

        this.webTestClient
            .patch()
            .uri("/groups/${event.groups[0].id}/_addMemberName")
            .body(GroupMemberNameReq(
                memberName = "Ninja"
            ))
            .exchange()
            .expectStatus().is2xxSuccessful

        this.webTestClient
            .patch()
            .uri("/groups/${event.groups[0].id}/_addMemberName")
            .body(GroupMemberNameReq(
                memberName = "Nabnab"
            ))
            .exchange()
            .expectStatus().is2xxSuccessful

        this.webTestClient
            .post()
            .uri("/groups/${event.groups[0].id}/scrooges")
            .body(ScroogeAddReq(
                memberName = "Ninja",
                paidAmount = BigDecimal("1100"),
                currency = Currency.getInstance("JPY"),
                forWhat = "rent-a-car"
            ))
            .exchange()
            .expectStatus().is2xxSuccessful

        val event2 = this.webTestClient
            .get()
            .uri("/events/${event.id}")
            .exchange()
            .expectBody<Event>()
            .returnResult()
            .responseBody ?: throw RuntimeException("response body is null")

        this.testId = TestId(
            eventId = event2.id,
            groupId = event2.groups[0].id,
            scroogeId = event2.groups[0].scrooges[0].id
        )
    }

    @Test
    fun createEvent() {
        this.webTestClient
            .post()
            .uri("/events/_create")
            .body(EventCreateReq(
                name = "Koiki Camp",
                transferCurrency = Currency.getInstance("JPY")
            ))
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .consumeWith(document("event-create",
                requestFields(
                    fieldWithPath("name")
                        .description("Not Null, event name"),
                    fieldWithPath("transferCurrency")
                        .description("Not Null, currency code of ISO 4217")
                )
            ))
    }

    @Test
    fun readEvent() {
        this.webTestClient
            .get()
            .uri("/events/${testId.eventId}")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .consumeWith(document("event-read"))
    }

    @Test
    fun addGroup() {
        this.webTestClient
            .post()
            .uri("/events/${testId.eventId}/groups/_add")
            .body(GroupAddReq(
                name = "Scuba diving"
            ))
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .consumeWith(document("group-add",
                requestFields(
                    fieldWithPath("name")
                        .description("Not Null, group name")
                    )
            ))
    }

    @Test
    fun updateGroupName() {
        this.webTestClient
            .patch()
            .uri("/groups/${testId.groupId}/_updateName")
            .body(GroupNameReq(
                name = "BBQ"
            ))
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .consumeWith(document("group-name-update",
                requestFields(
                    fieldWithPath("name")
                        .description("Not Null, group name")
                )
            ))
    }

    @Test
    fun deleteGroup() {
        this.webTestClient
            .delete()
            .uri("/groups/${testId.groupId}")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .consumeWith(document("group-delete"))
    }

    @Test
    fun addMemberName() {
        this.webTestClient
            .patch()
            .uri("/groups/${testId.groupId}/_addMemberName")
            .body(GroupMemberNameReq(
                memberName = "Ninja"
            ))
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .consumeWith(document("group-memberName-add",
                requestFields(
                    fieldWithPath("memberName")
                        .description("Not Null, member name")
                )
            ))
    }

    @Test
    fun removeMemberName() {
        this.webTestClient
            .patch()
            .uri("/groups/${testId.groupId}/_removeMemberName")
            .body(GroupMemberNameReq(
                memberName = "Ninja"
            ))
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .consumeWith(document("group-memberName-remove",
                requestFields(
                    fieldWithPath("memberName")
                        .description("Not Null, member name")
                )
            ))
    }

    @Test
    fun addScrooge() {
        this.webTestClient
            .post()
            .uri("/groups/${testId.groupId}/scrooges")
            .body(ScroogeAddReq(
                memberName = "Ninja",
                paidAmount = BigDecimal("11000"),
                currency = Currency.getInstance("JPY"),
                forWhat = "parking"
            ))
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .consumeWith(document("scrooge-add",
                requestFields(
                    fieldWithPath("memberName")
                        .description("Not Null, member name"),
                    fieldWithPath("paidAmount")
                        .description("Not Null, paid amount by member"),
                    fieldWithPath("currency")
                        .description("Not Null, currency code of ISO 4217"),
                    fieldWithPath("forWhat")
                        .description("Not Null, memo")
                )
            ))
    }

    @Test
    fun deleteScrooge() {
        this.webTestClient
            .delete()
            .uri("/scrooges/${testId.scroogeId}")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .consumeWith(document("scrooge-delete"))
    }

    data class TestId(
        val eventId: String,
        val groupId: String,
        val scroogeId: String
    )
}