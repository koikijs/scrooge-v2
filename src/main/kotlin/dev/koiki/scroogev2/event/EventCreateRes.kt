package dev.koiki.scroogev2.event

import dev.koiki.scroogev2.group.GroupRes
import java.time.LocalDateTime
import java.util.*

data class EventCreateRes(
    val name: String,
    val id: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val groups: List<GroupRes>,
    val transferCurrency: Currency
)