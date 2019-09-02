package dev.koiki.scroogev2.scrooge

import com.mongodb.client.result.DeleteResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ScroogeRepository(
    private val template: ReactiveMongoTemplate
) {
    suspend fun findById(id: String): Scrooge =
        template.query(Scrooge::class.java)
            .matching(Query(Scrooge::id isEqualTo id))
            .awaitOneOrNull()
            ?: throw RuntimeException("")

    @FlowPreview
    suspend fun findByGroupId(groupId: String): Flow<Scrooge> =
        template.query(Scrooge::class.java)
            .matching(Query(Scrooge::groupId isEqualTo groupId))
            .flow()

    suspend fun create(scrooge: Scrooge): Scrooge =
        template.insert(Scrooge::class.java)
            .oneAndAwait(scrooge)

    suspend fun deleteByGroupId(groupId: String): DeleteResult =
        template.remove(Scrooge::class.java)
            .matching(Query(Scrooge::groupId isEqualTo groupId))
            .allAndAwait()
}