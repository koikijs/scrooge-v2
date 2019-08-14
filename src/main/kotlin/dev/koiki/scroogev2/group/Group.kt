package dev.koiki.scroogev2.group

import java.time.LocalDateTime

data class Group(
    val id: String,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)