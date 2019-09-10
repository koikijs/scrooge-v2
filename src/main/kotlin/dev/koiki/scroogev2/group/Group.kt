package dev.koiki.scroogev2.group

import dev.koiki.scroogev2.scrooge.Scrooge
import dev.koiki.scroogev2.transferamount.TransferAmount
import java.time.LocalDateTime

data class Group(
    val id: String,
    val name: String,
    val memberNames: List<String>,
    val scrooges: List<Scrooge>,
    val transferAmounts: List<TransferAmount>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)