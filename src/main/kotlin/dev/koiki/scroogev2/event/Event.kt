package dev.koiki.scroogev2.event

import dev.koiki.scroogev2.group.Group
import java.time.LocalDateTime
import java.util.*

data class Event(
    val id: String,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val groups: List<Group>,
    val transferCurrency: Currency
)