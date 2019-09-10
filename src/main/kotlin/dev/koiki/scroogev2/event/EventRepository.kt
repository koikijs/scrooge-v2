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
    suspend fun create(eventDoc: EventDoc): EventDoc = template.insert(EventDoc::class.java).oneAndAwait(eventDoc)

    suspend fun findById(id: String): EventDoc =
        template.query(EventDoc::class.java)
            .matching(Query(EventDoc::id isEqualTo id))
            .awaitOneOrNull()
            ?: throw RuntimeException("")
}