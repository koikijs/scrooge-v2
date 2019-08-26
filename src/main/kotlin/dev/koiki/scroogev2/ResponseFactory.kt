package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.Event
import dev.koiki.scroogev2.event.EventRes
import dev.koiki.scroogev2.group.Group
import dev.koiki.scroogev2.group.GroupRes
import dev.koiki.scroogev2.scrooge.Scrooge
import org.springframework.stereotype.Component

@Component
class ResponseFactory {
    fun makeEvent(event: Event, groupRess: List<GroupRes>): EventRes =
        EventRes(
            name = event.name,
            id = event.id!!,
            createdAt = event.createdAt,
            updatedAt = event.updatedAt,
            transferCurrency = event.transferCurrency,
            groups = groupRess
        )

    fun makeGroup(group: Group, scrooges: List<Scrooge>): GroupRes =
        GroupRes(
            id = group.id!!,
            name = group.name,
            scrooges = listOf(),
            memberNames = group.memberNames,
            createdAt = group.createdAt,
            updatedAt = group.updatedAt
        )

}