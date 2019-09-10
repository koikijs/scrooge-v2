package dev.koiki.scroogev2

import kotlinx.coroutines.FlowPreview
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

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
class CorsConfig {

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val headers = listOf(
            "Cache-Control", "Content-Language", "Content-Type",
            "Expires", "Last-Modified", "Pragma", "Location"
        )

        val corsConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("https://kyoden.now.sh", "http://localhost:3000")
            maxAge = 8000L
            addAllowedMethod("*")
            allowedHeaders = headers
            exposedHeaders = headers
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfig)
        }

        return CorsWebFilter(source)
    }
}
