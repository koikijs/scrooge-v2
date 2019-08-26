package dev.koiki.scroogev2.event

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.awaitOneOrNull
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class EventRepository(
    private val template: ReactiveMongoTemplate
) {
    suspend fun create(event: Event): Event = template.insert(Event::class.java).oneAndAwait(event)

    suspend fun findById(id: String): Event =
        template.query(Event::class.java)
            .matching(Query(Event::id isEqualTo id))
            .awaitOneOrNull()
            ?: throw RuntimeException("")
}