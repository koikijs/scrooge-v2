package dev.koiki.scroogev2

import kotlinx.coroutines.FlowPreview
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.config.EnableWebFlux

@FlowPreview
@Configuration
class MyRouter(val handler: MyHandler) {

    @Bean
    fun routerFunction(): RouterFunction<ServerResponse> = coRouter {
        "/events".nest {
            POST("/_create") { handler.createEvent(it) }
            "/{eventId}".nest {
                GET("") { handler.readEvent(it) }
                POST("/groups/_add") { handler.addGroup(it) }
            }
        }
        "/groups/{groupId}".nest {
            PATCH("/_updateName") { handler.updateGroupName(it) }
            DELETE("") { handler.deleteGroup(it) }
            PATCH("/_addMemberName") { handler.addGroupMemberName(it) }
            PATCH("/_removeMemberName") { handler.removeGroupMemberName(it) }
            POST("/scrooges") { handler.addScrooge(it) }
        }
        "/scrooges/{scroogeId}".nest {
            DELETE("") { handler.deleteScrooge(it) }
        }
    }
}

@Configuration
class MyCorsConfig : WebFluxConfigurer {

    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedOrigins("https://kyoden.now.sh", "http://localhost:3000")
            .allowedMethods("*")
            .allowCredentials(true)
            .allowedHeaders("Cache-Control", "Content-Language", "Content-Type", "Expires", "Last-Modified", "Pragma", "Location")
            .exposedHeaders("Cache-Control", "Content-Language", "Content-Type", "Expires", "Last-Modified", "Pragma", "Location")
    }
}