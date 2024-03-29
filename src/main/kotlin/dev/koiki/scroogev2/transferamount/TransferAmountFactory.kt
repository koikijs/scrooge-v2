package dev.koiki.scroogev2.transferamount

import dev.koiki.scroogev2.scrooge.Scrooge
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors

@Component
class TransferAmountFactory {
    fun create(scrooges: List<Scrooge>, memberNames: List<String>): List<TransferAmount> {
        if (scrooges.isEmpty())
            return listOf()

        val membersWhoDoNotPayMoney: List<String> = memberNames
            .filter { memberName ->
                scrooges.find { it.memberName != memberName } != null
            }
            .toList()

        val dummyScrooges: List<Scrooge> = membersWhoDoNotPayMoney
            .map {
                Scrooge(
                    id = "",
                    memberName = it,
                    paidAmount = BigDecimal.ZERO,
                    currency = Currency.getInstance("JPY"),
                    forWhat = ""
                )
            }

        val totalScrooges = scrooges.plus(dummyScrooges)

        val totalAmount: BigDecimal = totalScrooges
            .map { it.paidAmount }
            .reduce { acc, paidAmount -> acc + paidAmount }

        val paidAmountPerMember: Map<String, BigDecimal> = totalScrooges
            .stream()
            .collect(
                Collectors.groupingBy(
                    { it.memberName },
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        Scrooge::paidAmount,
                        BigDecimal::add)
                )
            )

        val avgAmount: BigDecimal = totalAmount / paidAmountPerMember.size.toBigDecimal()

        val payableAmountsPerMember: Map<String, BigDecimal> = paidAmountPerMember
            .entries
            .stream()
            .collect(Collectors.toMap(
                { it.key },
                { it.value.subtract(avgAmount) }
            ))

        val transferAmounts = mutableListOf<TransferAmount>()
        calculateRecursively(payableAmountsPerMember.toMutableMap(), transferAmounts)

        return transferAmounts
    }

    private fun calculateRecursively(
        payableAmountsPerMember: MutableMap<String, BigDecimal>,
        transferAmounts: MutableList<TransferAmount>) {
        if (payableAmountsPerMember.size > 1) {
            val from = payableAmountsPerMember
                .entries
                .stream()
                .min { a, b -> a.value.subtract(b.value).compareTo(BigDecimal.ZERO) }
                .get()
                .key

            val to = payableAmountsPerMember
                .entries
                .stream()
                .min { a, b -> b.value.subtract(a.value).compareTo(BigDecimal.ZERO) }
                .get()
                .key

            transferAmounts.add(
                TransferAmount(
                    from = from,
                    to = to,
                    amount = payableAmountsPerMember[from]!!.multiply(BigDecimal.valueOf(-1))
                )
            )

            payableAmountsPerMember[to] = payableAmountsPerMember[to]!!.add(payableAmountsPerMember[from])
            payableAmountsPerMember.remove(from)

            calculateRecursively(payableAmountsPerMember, transferAmounts)
        }
    }
}