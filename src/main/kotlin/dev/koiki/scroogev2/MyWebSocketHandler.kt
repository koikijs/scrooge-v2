package dev.koiki.scroogev2

import com.fasterxml.jackson.databind.ObjectMapper
import dev.koiki.scroogev2.event.EventRes
import kotlinx.coroutines.FlowPreview
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@Component
class MyWebSocketHandler(
    private val myService: MyService,
    private val mapper: ObjectMapper
) : WebSocketHandler {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val sessionPoolMap = ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>>()

    @FlowPreview
    suspend fun publishMessage(eventId: String) {
        val eventRes: EventRes = myService.readEvent(eventId)
        val eventResJsonString: String = mapper.writeValueAsString(eventRes)

        log.debug("start sending a message")
        sessionPoolMap[eventId]?.forEach {
            it.send(Mono.just(it.textMessage(eventResJsonString))).subscribe()
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
    fun webSocketHandlerMapping(): HandlerMapping =
        SimpleUrlHandlerMapping().also {
            it.order = 1
            it.urlMap = mapOf("/events" to webSocketHandler)
        }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter =
        WebSocketHandlerAdapter(webSocketService())

    @Bean
    fun webSocketService(): WebSocketService =
        HandshakeWebSocketService(ReactorNettyRequestUpgradeStrategy())
}