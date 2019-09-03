package dev.koiki.scroogev2

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@Component
class MyWebSocketHandler : WebSocketHandler {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val sessionPoolMap = ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>>()

    fun testPublishMessage() {
        log.debug("start!!")
        sessionPoolMap
            .entries
            .forEach {
                it.value.forEach {
                    s -> s.send(Mono.just(s.textMessage("hello!!"))).subscribe()
                }
            }
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        val eventId = session.handshakeInfo.uri.query
        log.debug("session established, sessionId: ${session.id}, eventId: $eventId")
        addSessionToPool(eventId, session)

        return session.receive()
            .doOnNext {
                val payload = it.payloadAsText
                log.debug("message comes, sessionId: ${session.id}, message: $payload")
                session.send(Mono.just(session.textMessage("hey! I am here!, $payload"))).subscribe()
            }
            .doFinally {
                log.debug("terminated, sessionId: ${session.id}")
                removeSessionFromPool(eventId, session)
            }
            .then()
    }

    private fun addSessionToPool(eventId: String, session: WebSocketSession) {
        if (sessionPoolMap[eventId] == null)
            sessionPoolMap[eventId] = CopyOnWriteArraySet()

        sessionPoolMap[eventId]!!.add(session)

        if (log.isDebugEnabled)
            log.debug("sessionPoolMap: $sessionPoolMap")
    }

    private fun removeSessionFromPool(eventId: String, session: WebSocketSession) {
        val sessions = sessionPoolMap[eventId]
        if (sessions != null) {
            sessions.remove(session)
            if (sessions.isEmpty())
                sessionPoolMap.remove(eventId)
        }

        if (log.isDebugEnabled)
            log.debug("sessionPoolMap: $sessionPoolMap")
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