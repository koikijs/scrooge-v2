package dev.koiki.scroogev2.scrooge

import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.util.*

@Document("scrooge")
data class Scrooge(
    val id: String? = null,
    val groupId: String,
    val memberName: String,
    val paidAmount: BigDecimal,
    val currency: Currency,
    val forWhat: String
)