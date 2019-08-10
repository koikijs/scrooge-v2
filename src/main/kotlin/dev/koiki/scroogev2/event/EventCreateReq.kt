package dev.koiki.scroogev2.event

import java.util.*

data class EventCreateReq (
    val name: String,
    val transferCurrency: Currency
)
