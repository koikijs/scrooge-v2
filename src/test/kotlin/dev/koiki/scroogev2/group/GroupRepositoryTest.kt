package dev.koiki.scroogev2.group

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.RuntimeException
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class GroupRepositoryTest {
    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Test
    fun addMemberNameById() = runBlocking {
        val a = groupRepository.create(Group(
            eventId = "xxx",
            name = "testA",
            memberNames = listOf("ninja"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))

        val b = groupRepository.create(Group(
            eventId = "xxx",
            name = "testB",
            memberNames = listOf("nabnab"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))

        groupRepository.addMemberNameById(id = a.id!!, memberName = "duke")

        val resultA = groupRepository.findById(a.id!!)
        val resultB = groupRepository.findById(b.id!!)

        assertAll(
            { assertThat(resultA.memberNames).isEqualTo(listOf("ninja", "duke")) },
            { assertThat(resultB.memberNames).isEqualTo(listOf("nabnab")) }
        )
    }

    @Test
    fun deleteMemberNameById() = runBlocking {
        val a = groupRepository.create(Group(
            eventId = "xxx",
            name = "testA",
            memberNames = listOf("ninja", "duke"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))

        val b = groupRepository.create(Group(
            eventId = "xxx",
            name = "testB",
            memberNames = listOf("nabnab", "ninja"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))

        groupRepository.deleteMemberNameById(id = a.id!!, memberName = "ninja")

        val resultA = groupRepository.findById(a.id!!)
        val resultB = groupRepository.findById(b.id!!)

        assertAll(
            { assertThat(resultA.memberNames).isEqualTo(listOf("duke")) },
            { assertThat(resultB.memberNames).isEqualTo(listOf("nabnab", "ninja")) }
        )
    }

    @Test
    fun updateNameById() = runBlocking {
        val a = groupRepository.create(Group(
            eventId = "xxx",
            name = "testA",
            memberNames = listOf("ninja", "duke"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))

        val b = groupRepository.create(Group(
            eventId = "xxx",
            name = "testB",
            memberNames = listOf("nabnab", "ninja"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))

        groupRepository.updateNameById(id = a.id!!, name = "testC")

        val resultA = groupRepository.findById(a.id!!)
        val resultB = groupRepository.findById(b.id!!)

        assertAll(
            { assertThat(resultA.name).isEqualTo("testC") },
            { assertThat(resultB.name).isEqualTo("testB") }
        )
    }

    @Test
    fun deleteById() = runBlocking {
        val a = groupRepository.create(Group(
            eventId = "xxx",
            name = "testA",
            memberNames = listOf("ninja", "duke"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))

        val b = groupRepository.create(Group(
            eventId = "xxx",
            name = "testB",
            memberNames = listOf("nabnab", "ninja"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))

        val result = groupRepository.deleteById(id = a.id!!)

        assertAll(
            {
                assertThat(result.deletedCount).isEqualTo(1)
            },
            {
                assertThatThrownBy { runBlocking { groupRepository.findById(a.id!!) } }
                    .isInstanceOf(RuntimeException::class.java)
            }
        )
    }
}