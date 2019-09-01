package dev.koiki.scroogev2

import kotlinx.coroutines.FlowPreview
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MyRouter(val handler: MyHandler) {

    @Bean
    @FlowPreview
    fun routerFunction(): RouterFunction<ServerResponse> = coRouter {
        "/events".nest {
            POST("/_create") { handler.createEvent(it) }
            "/{eventId}".nest {
                GET("") { handler.readEvent(it) }
                "/groups".nest {
                    POST("/_add") { handler.addGroup(it) }
                    "/{groupId}".nest {
                        PATCH("/_updateName") { handler.updateGroupName(it) }
                        DELETE("") { handler.foo() }
                        PATCH("/_addMemberName") { handler.addGroupMemberName(it) }
                        "/scrooges".nest {
                            POST("/_add") { handler.addScrooge(it) }
                        }
                    }
                }
            }
        }
    }
}
