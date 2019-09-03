package dev.koiki.scroogev2

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import java.util.HashMap
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Component
class MyWebSocketHandler : WebSocketHandler {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(session: WebSocketSession): Mono<Void> {
        log.info("session established, sessionId: ${session.id}")

        return session.receive()
            .doOnNext {
                log.info("message comes, sessionId: ${session.id}, message: ${it.payloadAsText}")
            }
            .doFinally {
                log.info("terminated, sessionId: ${session.id}")
            }
            .then()
    }
}

@Configuration
class WebSocketConfig(
    private val webSocketHandler: WebSocketHandler
) {
    @Bean
    fun webSocketHandlerMapping(): HandlerMapping {
        val map = HashMap<String, WebSocketHandler>()
        map["/event-emitter"] = webSocketHandler

        val handlerMapping = SimpleUrlHandlerMapping()
        handlerMapping.order = 1
        handlerMapping.urlMap = map
        return handlerMapping
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter(webSocketService())
    }

    @Bean
    fun webSocketService(): WebSocketService {
        return HandshakeWebSocketService(ReactorNettyRequestUpgradeStrategy())
    }
}