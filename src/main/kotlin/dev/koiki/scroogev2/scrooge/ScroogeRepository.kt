package dev.koiki.scroogev2.scrooge

import com.mongodb.client.result.DeleteResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

@Component
class ScroogeRepository(
    private val template: ReactiveMongoTemplate
) {
    suspend fun findById(id: String): ScroogeDoc =
        template.query(ScroogeDoc::class.java)
            .matching(Query(ScroogeDoc::id isEqualTo id))
            .awaitOneOrNull()
            ?: throw RuntimeException("")

    @FlowPreview
    suspend fun findByGroupId(groupId: String): Flow<ScroogeDoc> =
        template.query(ScroogeDoc::class.java)
            .matching(Query(ScroogeDoc::groupId isEqualTo groupId))
            .flow()

    suspend fun create(scroogeDoc: ScroogeDoc): ScroogeDoc =
        template.insert(ScroogeDoc::class.java)
            .oneAndAwait(scroogeDoc)

    suspend fun deleteByGroupId(groupId: String): DeleteResult =
        template.remove(ScroogeDoc::class.java)
            .matching(Query(ScroogeDoc::groupId isEqualTo groupId))
            .allAndAwait()

    suspend fun deleteById(id: String): DeleteResult =
        template.remove(ScroogeDoc::class.java)
            .matching(Query(ScroogeDoc::id isEqualTo id))
            .allAndAwait()
}