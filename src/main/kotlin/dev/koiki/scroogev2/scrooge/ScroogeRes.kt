package dev.koiki.scroogev2.scrooge

import java.math.BigDecimal
import java.util.*

data class ScroogeRes(
    val memberName: String,
    val paidAmount: BigDecimal,
    val currency: Currency,
    val forWhat: String
)