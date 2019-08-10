package dev.koiki.scroogev2.event

import java.time.LocalDateTime
import java.util.*

data class EventCreateRes(
    val name: String,
    val id: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val transferCurrency: Currency
)