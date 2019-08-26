package dev.koiki.scroogev2.group

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GroupRepository(
    private val template: ReactiveMongoTemplate
) {
    suspend fun findById(id: String): Group =
        template.query(Group::class.java)
            .matching(Query(Group::id isEqualTo id))
            .awaitOneOrNull()
            ?: throw RuntimeException("")

    @FlowPreview
    suspend fun findByEventId(eventId: String): Flow<Group> =
        template.query(Group::class.java)
            .matching(Query(Group::eventId isEqualTo eventId))
            .flow()

    suspend fun create(group: Group): Group = template.insert(Group::class.java).oneAndAwait(group)
}