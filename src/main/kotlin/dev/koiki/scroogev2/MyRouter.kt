package dev.koiki.scroogev2

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MyRouter(val handler: MyHandler) {

    @Bean
    fun routerFunction(): RouterFunction<ServerResponse> = coRouter {
        "/events".nest {
            POST("/_create") { handler.createEvent(it) }
            GET("/{eventId}") { handler.foo() }
            "/{eventId}".nest {
                "/groups".nest {
                    POST("/_add") { handler.addGroup(it) }
                    "/{groupId}".nest {
                        PATCH("/_name") { handler.foo() }
                        DELETE("") { handler.foo() }
                        "/memberNames".nest {
                            POST("/{memberName}") { handler.foo() }
                        }
                        "/scrooges".nest {
                            POST("/_add") { handler.foo() }
                        }
                    }
                }
            }
        }
    }
}
