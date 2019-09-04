package dev.koiki.scroogev2.group

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("group")
data class Group(
    val id: String? = null,
    val eventId: String,
    val name: String,
    val memberNames: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)