package dev.koiki.scroogev2.event

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document("event")
data class Event(
    val id: String? = null,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val transferCurrency: Currency
)