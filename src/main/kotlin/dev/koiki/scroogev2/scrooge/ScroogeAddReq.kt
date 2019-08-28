package dev.koiki.scroogev2.scrooge

import org.springframework.data.annotation.Id
import java.math.BigDecimal
import java.util.*

data class ScroogeAddReq(
    val memberName: String,
    val paidAmount: BigDecimal,
    val currency: Currency,
    val forWhat: String
)