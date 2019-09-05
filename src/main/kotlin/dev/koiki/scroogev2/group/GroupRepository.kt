package dev.koiki.scroogev2.group

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component

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

    suspend fun create(group: Group): Group =
        template.insert(Group::class.java)
            .oneAndAwait(group)

    suspend fun addMemberNameById(id: String, memberName: String): UpdateResult =
        template.update(Group::class.java)
            .matching(Query(Group::id isEqualTo id))
            .apply(Update().push("memberNames").value(memberName))
            .firstAndAwait()

    suspend fun removeMemberNameById(id: String, memberName: String): UpdateResult =
        template.update(Group::class.java)
            .matching(Query(Group::id isEqualTo id))
            .apply(Update().pull("memberNames", memberName))
            .firstAndAwait()

    suspend fun updateNameById(id: String, name: String): UpdateResult =
        template.update(Group::class.java)
            .matching(Query(Group::id isEqualTo id))
            .apply(Update().set("name", name))
            .firstAndAwait()

    suspend fun deleteById(id: String): DeleteResult =
        template.remove(Group::class.java)
            .matching(Query(Group::id isEqualTo id))
            .allAndAwait()
}