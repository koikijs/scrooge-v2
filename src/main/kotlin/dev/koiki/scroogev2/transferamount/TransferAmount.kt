package dev.koiki.scroogev2.transferamount

import java.math.BigDecimal

data class TransferAmount(
    val from: String,
    val to: String,
    val amount: BigDecimal
)