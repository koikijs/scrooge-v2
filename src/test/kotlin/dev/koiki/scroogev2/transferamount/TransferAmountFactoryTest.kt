package dev.koiki.scroogev2.transferamount

import dev.koiki.scroogev2.scrooge.Scrooge
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

internal class TransferAmountFactoryTest {
    private val transferAmountFactory = TransferAmountFactory()

    @Test
    fun test01() {
        val input = listOf(
            scrooge(
                memberName = "side",
                paidAmount = BigDecimal("9602")
            ),
            scrooge(
                memberName = "side",
                paidAmount = BigDecimal("644")
            ),
            scrooge(
                memberName = "f",
                paidAmount = BigDecimal("48500")
            ),
            scrooge(
                memberName = "sushi",
                paidAmount = BigDecimal("360")
            ),
            scrooge(
                memberName = "nab",
                paidAmount = BigDecimal("9648")
            ),
            scrooge(
                memberName = "ino",
                paidAmount = BigDecimal("6231")
            ),
            scrooge(
                memberName = "ninja",
                paidAmount = BigDecimal("26784")
            ),
            scrooge(
                memberName = "ninja",
                paidAmount = BigDecimal("17667")
            ),
            scrooge(
                memberName = "ninja",
                paidAmount = BigDecimal("1050")
            ),
            scrooge(
                memberName = "ninja",
                paidAmount = BigDecimal("3321")
            ),
            scrooge(
                memberName = "ninja",
                paidAmount = BigDecimal("5260")
            )
        )
        assertThat(transferAmountFactory.create(input, listOf("ninja", "nab", "side", "f", "ino", "sushi")))
            .isEqualTo(listOf(
                TransferAmount(
                    from = "sushi",
                    to = "ninja",
                    amount = BigDecimal("21151")
                ),
                TransferAmount(
                    from = "ino",
                    to = "f",
                    amount = BigDecimal("15280")
                ),
                TransferAmount(
                    from = "nab",
                    to = "f",
                    amount = BigDecimal("11863")
                ),
                TransferAmount(
                    from = "side",
                    to = "ninja",
                    amount = BigDecimal("11265")
                ),
                TransferAmount(
                    from = "f",
                    to = "ninja",
                    amount = BigDecimal("154")
                )
            ))
    }

    @Test
    fun test02() {
        assertThat(transferAmountFactory.create(listOf(), listOf()))
            .isEmpty()
    }

    @Test
    fun test03() {
        val input = listOf(
            scrooge(
                memberName = "side",
                paidAmount = BigDecimal("9602")
            )
        )
        assertThat(transferAmountFactory.create(input, listOf("side")))
            .isEmpty()
    }

    @Test
    fun test04() {
        val input = listOf(
            scrooge(
                memberName = "side",
                paidAmount = BigDecimal("1000")
            )
        )
        assertThat(transferAmountFactory.create(input, listOf("side", "ninja")))
            .isEqualTo(listOf(
                TransferAmount(
                    from = "ninja",
                    to = "side",
                    amount = BigDecimal("500")
                )
            ))
    }

    private fun scrooge(memberName: String, paidAmount: BigDecimal): Scrooge =
        Scrooge(
            memberName = memberName,
            paidAmount = paidAmount,
            forWhat = "",
            currency = Currency.getInstance("JPY"),
            id = ""
        )
}