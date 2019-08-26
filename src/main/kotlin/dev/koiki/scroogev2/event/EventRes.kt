package dev.koiki.scroogev2.event

import dev.koiki.scroogev2.group.GroupRes
import java.time.LocalDateTime
import java.util.*

data class EventRes(
    val name: String,
    val id: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val groups: List<GroupRes>,
    val transferCurrency: Currency
) {
    constructor(event: Event, groupRess: List<GroupRes>) :
        this(
            name = event.name,
            id = event.id!!,
            createdAt = event.createdAt,
            updatedAt = event.createdAt,
            groups = listOf(),
            transferCurrency = event.transferCurrency
        )
}