package dev.koiki.scroogev2.group

import dev.koiki.scroogev2.scrooge.ScroogeRes
import dev.koiki.scroogev2.transferamount.TransferAmount
import java.time.LocalDateTime

data class GroupRes(
    val id: String,
    val name: String,
    val memberNames: List<String>,
    val scrooges: List<ScroogeRes>,
    val transferAmounts: List<TransferAmount>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    constructor(group: Group, scrooges: List<ScroogeRes>, transferAmounts: List<TransferAmount>) :
        this(
            id = group.id!!,
            name = group.name,
            scrooges = scrooges,
            memberNames = group.memberNames,
            transferAmounts = transferAmounts,
            createdAt = group.createdAt,
            updatedAt = group.updatedAt
        )
}