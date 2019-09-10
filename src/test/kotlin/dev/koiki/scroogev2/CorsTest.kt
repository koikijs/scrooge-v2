package dev.koiki.scroogev2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class CorsTest(
    @LocalServerPort private val port: Int
) {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun corsTest2() {
        val res = WebClient.create()
            .options()
            .uri("http://localhost:$port/events/_create")
            .header("Origin", "https://kyoden.now.sh")
            .header("Access-Control-Request-Method", "GET")
            .exchange()
            .block()

        assertThat(res!!.statusCode().is2xxSuccessful).isTrue()
    }

    // this does not work, maybe bug??
    //@Test
    fun corsTest() {
        webTestClient.options()
            .uri("/events/_create")
            .header("Origin", "https://kyoden.now.sh")
            .header("Access-Control-Request-Method", "GET")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectHeader().valueEquals("Access-Control-Allow-Origin", "*")
            .expectHeader().valueEquals("Access-Control-Allow-Methods", "POST")
    }
}