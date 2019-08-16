package dev.koiki.scroogev2.group

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("group")
data class Group(
    @Id val id: String? = null,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)