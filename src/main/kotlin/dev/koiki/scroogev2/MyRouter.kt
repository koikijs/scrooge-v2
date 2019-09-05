package dev.koiki.scroogev2

import kotlinx.coroutines.FlowPreview
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
            POST("/scrooges/_add") { handler.addScrooge(it) }
        }
        "/scrooges/{scroogeId}".nest {
            DELETE("") { handler.deleteScrooge(it) }
        }
    }
}
