package ru.ttech.piapi.core.utils

import com.google.protobuf.Timestamp
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import ru.tinkoff.piapi.contract.v1.moneyValue
import ru.tinkoff.piapi.contract.v1.quotation
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class UtilsKtTest {

    @ParameterizedTest
    @CsvSource(
        value = [
            "335,0",
            "219,37",
            "0,5"
        ]
    )
    fun testTimestampToInstant(second: Long, nano: Int) {
        Assertions.assertEquals(
            Instant.ofEpochSecond(second, nano.toLong()),
            Timestamp.newBuilder().setSeconds(second).setNanos(nano).build().toInstant()
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "335,0,3,1970-01-01T03:05:35+03:00",
            "335,0,0,1970-01-01T00:05:35Z",
            "0,500000000,0,1970-01-01T00:00:00.500000Z"
        ]
    )
    fun testTimestampToOffsetDateTime(second: Long, nano: Int, zoneOffset: Int, expected: OffsetDateTime) {
        Assertions.assertEquals(
            expected,
            Timestamp.newBuilder().setSeconds(second).setNanos(nano).build()
                .toOffsetDateTime(ZoneOffset.ofHours(zoneOffset))
        )
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1,200000000,1.200000000",
            "5,0,5.000000000",
            "0,123456789,0.123456789"
        ]
    )
    fun testMoneyValueToBigDecimal(unit: Long, nanos: Int, expected: BigDecimal) {
        Assertions.assertEquals(expected, moneyValue {
            units = unit
            nano = nanos
            currency = "cur"
        }.toBigDecimal())
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1,200000000",
            "5,0",
            "0,123456789"
        ]
    )
    fun testMoneyValueToQuotation(unit: Long, nanos: Int) {
        val expected = quotation {
            units = unit
            nano = nanos
        }
        Assertions.assertEquals(expected, moneyValue {
            units = unit
            nano = nanos
            currency = "cur"
        }.toQuotation())
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1.2,1,200000000",
            "5,5,0",
            "0.123456789,0,123456789"
        ]
    )
    fun testBigDecimalToQuotation(source: BigDecimal, expectedUnits: Long, expectedNanos: Int) {
        val expected = quotation {
            units = expectedUnits
            nano = expectedNanos
        }
        Assertions.assertEquals(expected, source.toQuotation())
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1.2,1,200000000",
            "5,5,0",
            "0.123456789,0,123456789"
        ]
    )
    fun testBigDecimalToMoneyValue(source: BigDecimal, expectedUnits: Long, expectedNanos: Int) {
        val expected = moneyValue {
            units = expectedUnits
            nano = expectedNanos
            currency = "cur"
        }
        Assertions.assertEquals(expected, source.toMoneyValue("CUR"))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1,200000000",
            "5,0",
            "0,123456789"
        ]
    )
    fun testQuotationToMoneyValue(unit: Long, nanos: Int) {
        val expected = moneyValue {
            units = unit
            nano = nanos
            currency = "cur"
        }
        val source = quotation {
            units = unit
            nano = nanos
        }
        Assertions.assertEquals(expected, source.toMoneyValue("CUR"))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1,200000000,1.200000000",
            "5,0,5.000000000",
            "0,123456789,0.123456789"
        ]
    )
    fun testQuotationToBigDecimal(unit: Long, nanos: Int, expected: BigDecimal) {
        val source = quotation {
            units = unit
            nano = nanos
        }
        Assertions.assertEquals(expected, source.toBigDecimal())
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "3.000000045,3",
            "5.000000000,5",
            "90.200000000,90",
            "5.123456789,5"
        ]
    )
    fun testGetUnits(source: BigDecimal, expected: Long) {
        Assertions.assertEquals(expected, source.getUnits())
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "3.000000045,45",
            "5.000000000,0",
            "90.200000000,200000000",
            "5.123456789,123456789"
        ]
    )
    fun testGetNano(source: BigDecimal, expected: Int) {
        Assertions.assertEquals(expected, source.getNano())
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "3,45,3.000000045",
            "5,0,5.000000000",
            "90,200000000,90.200000000",
            "5,123456789,5.123456789"
        ]
    )
    fun testValueOfQuotation(unit: Long, nano: Int, expected: BigDecimal) {
        Assertions.assertEquals(expected, valueOfQuotation(unit, nano))
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "RUB,rub",
            "VaL,val",
            "rub,rub",
            ",''"
        ]
    )
    fun testToLowerCase(source: String?, expected: String) {
        Assertions.assertEquals(expected, source.toLowerCase())
    }
}