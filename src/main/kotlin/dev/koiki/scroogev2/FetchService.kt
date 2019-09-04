package dev.koiki.scroogev2

import dev.koiki.scroogev2.event.EventRepository
import dev.koiki.scroogev2.event.EventRes
import dev.koiki.scroogev2.group.GroupRepository
import dev.koiki.scroogev2.group.GroupRes
import dev.koiki.scroogev2.scrooge.ScroogeRepository
import dev.koiki.scroogev2.scrooge.ScroogeRes
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component

@Component
class FetchService(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository,
    private val scroogeRepository: ScroogeRepository
) {
    @FlowPreview
    suspend fun readEvent(eventId: String): EventRes {
        val event = eventRepository.findById(eventId)

        val groupRess: List<GroupRes> = groupRepository.findByEventId(event.id!!)
            .map { group ->
                val scrooges: List<ScroogeRes> = scroogeRepository.findByGroupId(group.id!!)
                    .map { scrooge -> ScroogeRes(scrooge) }
                    .toList()

                GroupRes(group, scrooges)
            }
            .toList()

        return EventRes(event, groupRess)
    }
}