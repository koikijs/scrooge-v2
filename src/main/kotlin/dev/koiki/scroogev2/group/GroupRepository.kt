package dev.koiki.scroogev2.group

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.awaitOne
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.core.query
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GroupRepository(
    private val template: ReactiveMongoTemplate
) {
    suspend fun read(id: String): Group =
        template.query(Group::class.java)
            .matching(Query(Group::id isEqualTo id))
            .awaitOne()

    suspend fun create(group: Group): Group = template.insert(Group::class.java).oneAndAwait(group)
}