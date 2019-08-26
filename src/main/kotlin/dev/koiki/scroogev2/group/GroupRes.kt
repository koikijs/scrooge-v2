package dev.koiki.scroogev2.group

import dev.koiki.scroogev2.scrooge.ScroogeRes
import java.time.LocalDateTime

data class GroupRes(
    val id: String,
    val name: String,
    val memberNames: List<String>,
    val scrooges: List<ScroogeRes>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    constructor(group: Group, scrooges: List<ScroogeRes>) :
        this(
            id = group.id!!,
            name = group.name,
            scrooges = scrooges,
            memberNames = group.memberNames,
            createdAt = group.createdAt,
            updatedAt = group.updatedAt
        )
}