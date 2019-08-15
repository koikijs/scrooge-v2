package dev.koiki.scroogev2.group

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.awaitOne
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.core.query
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Mono

class GroupRepository(
    private val template: ReactiveMongoTemplate
) {
    suspend fun foo() {
        val query = Query()
        template.query(Group::class.java).matching(query).awaitOne()
    }

    suspend fun hoge() {
        //template.insert(Group::class.java).oneAndAwait(Group())
    }
}