package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.EventCreateReq
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromObject
import java.util.*

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestDocsTest(
    private val context: ApplicationContext
) {
    private lateinit var webTestClient: WebTestClient

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
    }

    @Test
    fun createEvent() {
        this.webTestClient
            .post()
            .uri("/events/_create")
            .body(fromObject(EventCreateReq(
                name = "Koiki Camp",
                transferCurrency = Currency.getInstance("JPY")
            )))
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
}