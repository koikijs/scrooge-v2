package dev.koiki.scroogev2.scrooge

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class ScroogeRepositoryTest {
    @Autowired
    lateinit var scroogeRepository: ScroogeRepository

    @Test
    @FlowPreview
    fun deleteById() = runBlocking {
        scroogeRepository.create(Scrooge(
            groupId = "groupA",
            memberName = "ninja",
            paidAmount = BigDecimal.TEN,
            currency = Currency.getInstance("JPY"),
            forWhat = "bla bla"
        ))

        scroogeRepository.create(Scrooge(
            groupId = "groupA",
            memberName = "nab",
            paidAmount = BigDecimal.ONE,
            currency = Currency.getInstance("JPY"),
            forWhat = "bla bla"
        ))

        scroogeRepository.create(Scrooge(
            groupId = "groupB",
            memberName = "ninja",
            paidAmount = BigDecimal.TEN,
            currency = Currency.getInstance("JPY"),
            forWhat = "bla bla"
        ))

        val deleteResult = scroogeRepository.deleteByGroupId("groupA")
        val fetchResult = scroogeRepository.findByGroupId("groupA").toList()

        assertAll(
            {
                assertThat(deleteResult.deletedCount).isEqualTo(2)
            },
            {
                assertThat(fetchResult).isEmpty()
            }
        )
    }
}