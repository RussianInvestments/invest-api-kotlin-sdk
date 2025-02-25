package ru.ttech.piapi.core.utils

import com.google.protobuf.Timestamp
import ru.tinkoff.piapi.contract.v1.MoneyValue
import ru.tinkoff.piapi.contract.v1.Quotation
import ru.tinkoff.piapi.contract.v1.moneyValue
import ru.tinkoff.piapi.contract.v1.quotation
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Converts [Timestamp] to [Instant]
 * @return [Instant] representation of [Timestamp]
 */
fun Timestamp.toInstant(): Instant = Instant.ofEpochSecond(this.seconds, this.nanos.toLong())
fun Instant.toTimestamp(): Timestamp = Timestamp.newBuilder().setSeconds(this.epochSecond).setNanos(this.nano).build()
fun Timestamp.toOffsetDateTime(zone: ZoneOffset): OffsetDateTime = this.toInstant().atOffset(zone)
fun MoneyValue.toBigDecimal(): BigDecimal = valueOfQuotation(this.units, this.nano)
fun MoneyValue.toQuotation(): Quotation = Quotation.newBuilder().setUnits(units).setNano(nano).build()
fun BigDecimal.toQuotation(): Quotation = quotation {
    units = getUnits()
    nano = getNano()
}
fun BigDecimal.toMoneyValue(curr: String? = null): MoneyValue = moneyValue {
    units = getUnits()
    nano = getNano()
    currency = curr.toLowerCase()
}
fun Quotation.toMoneyValue(curr: String? = null): MoneyValue =
    MoneyValue.newBuilder().setUnits(units).setNano(nano).setCurrency(curr.toLowerCase()).build()
fun Quotation.toBigDecimal(): BigDecimal = valueOfQuotation(this.units, this.nano)

internal fun BigDecimal.getUnits(): Long = this.toLong()
internal fun BigDecimal.getNano(): Int = this.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(1000000000L)).toInt()
internal fun valueOfQuotation(units: Long, nanos: Int) = BigDecimal.valueOf(units) + BigDecimal.valueOf(nanos.toLong(), 9)
internal fun String?.toLowerCase(): String = this?.lowercase()?:""