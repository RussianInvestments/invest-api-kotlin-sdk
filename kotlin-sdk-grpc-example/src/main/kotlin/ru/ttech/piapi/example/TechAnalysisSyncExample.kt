package ru.ttech.piapi.example

import ru.tinkoff.piapi.contract.v1.GetTechAnalysisRequest
import ru.tinkoff.piapi.contract.v1.GetTechAnalysisResponse
import ru.tinkoff.piapi.contract.v1.Quotation
import ru.ttech.piapi.core.InvestApi
import ru.ttech.piapi.core.utils.toTimestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

class TechAnalysisSyncExample {

    companion object {
        // UID акции Т-Технологии на Московской бирже
        private const val SHARE_UID = "87db07bc-0e02-4e29-90bb-05e8ef791d7b"

        // Период одной свечи
        private val INDICATOR_INTERVAL = GetTechAnalysisRequest.IndicatorInterval.INDICATOR_INTERVAL_FIFTEEN_MINUTES

        // Тип цены для расчета индикатора
        private val TYPE_OF_PRICE = GetTechAnalysisRequest.TypeOfPrice.TYPE_OF_PRICE_CLOSE

        // Время начала периода берем на час раньше текущего
        private val timeFrom = Instant.now().minus(1, ChronoUnit.HOURS).toTimestamp()

        // Время окончания периода берем как текущее
        private val timeTo = Instant.now().toTimestamp()
    }

    fun exampleTechAnalysisRSI(): List<GetTechAnalysisResponse.TechAnalysisItem> {
        // Собираем запрос к API
        val request = GetTechAnalysisRequest.newBuilder()
            .setIndicatorType(GetTechAnalysisRequest.IndicatorType.INDICATOR_TYPE_RSI) // RSI индикатор
            .setInstrumentUid(SHARE_UID) // UID акции
            .setFrom(timeFrom) // Начало периода расчета
            .setTo(timeTo) // Конец периода расчета
            .setInterval(INDICATOR_INTERVAL) // Период свечи
            .setTypeOfPrice(TYPE_OF_PRICE) // Тип цены
            .setLength(14) // Период RSI
            .build()
        return executeRequestSync(request)
    }

    fun exampleTechAnalysisSMA(): List<GetTechAnalysisResponse.TechAnalysisItem> {
        // Собираем запрос к API
        val request = GetTechAnalysisRequest.newBuilder()
            .setIndicatorType(GetTechAnalysisRequest.IndicatorType.INDICATOR_TYPE_SMA) // SMA индикатор
            .setInstrumentUid(SHARE_UID) // UID акции
            .setFrom(timeFrom) // Начало периода расчета
            .setTo(timeTo) // Конец периода расчета
            .setInterval(INDICATOR_INTERVAL) // Период свечи
            .setTypeOfPrice(TYPE_OF_PRICE) // Тип цены
            .setLength(9) // Период SMA
            .build()
        return executeRequestSync(request)
    }

    fun exampleTechAnalysisEMA(): List<GetTechAnalysisResponse.TechAnalysisItem> {
        // Собираем запрос к API
        val request = GetTechAnalysisRequest.newBuilder()
            .setIndicatorType(GetTechAnalysisRequest.IndicatorType.INDICATOR_TYPE_EMA) // EMA индикатор
            .setInstrumentUid(SHARE_UID) // UID акции
            .setFrom(timeFrom) // Начало периода расчета
            .setTo(timeTo) // Конец периода расчета
            .setInterval(INDICATOR_INTERVAL) // Период свечи
            .setTypeOfPrice(TYPE_OF_PRICE) // Тип цены
            .setLength(9) // Период EMA
            .build()
        return executeRequestSync(request)
    }

    fun exampleTechAnalysisBB(): List<GetTechAnalysisResponse.TechAnalysisItem> {
        // Собираем запрос к API
        val request = GetTechAnalysisRequest.newBuilder()
            .setIndicatorType(GetTechAnalysisRequest.IndicatorType.INDICATOR_TYPE_EMA) // Bollinger Bands индикатор
            .setInstrumentUid(SHARE_UID) // UID акции
            .setFrom(timeFrom) // Начало периода расчета
            .setTo(timeTo) // Конец периода расчета
            .setInterval(INDICATOR_INTERVAL) // Период свечи
            .setTypeOfPrice(TYPE_OF_PRICE) // Тип цены
            .setLength(20) // Период индикатора
            .setDeviation(
                GetTechAnalysisRequest.Deviation.newBuilder()
                    .setDeviationMultiplier(Quotation.newBuilder().setUnits(2).setNano(0).build())
                    .build() // Стандартное отклонение
            )
            .build()
        return executeRequestSync(request)
    }

    fun exampleTechAnalysisMACD(): List<GetTechAnalysisResponse.TechAnalysisItem> {
        // Собираем запрос к API
        val request = GetTechAnalysisRequest.newBuilder()
            .setIndicatorType(GetTechAnalysisRequest.IndicatorType.INDICATOR_TYPE_MACD) // MACD индикатор
            .setInstrumentUid(SHARE_UID) // UID акции
            .setFrom(timeFrom) // Начало периода расчета
            .setTo(timeTo) // Конец периода расчета
            .setInterval(INDICATOR_INTERVAL) // Период свечи
            .setTypeOfPrice(TYPE_OF_PRICE) // Тип цены
            .setSmoothing(
                GetTechAnalysisRequest.Smoothing.newBuilder()
                    .setFastLength(12) // Короткий период сглаживания для первой EMA
                    .setSlowLength(26) // Длинный период сглаживания для второй EMA
                    .setSignalSmoothing(9) // Период сглаживания для третьей EMA
                    .build()
            )
            .build()
        return executeRequestSync(request)
    }

    private fun executeRequestSync(request: GetTechAnalysisRequest): List<GetTechAnalysisResponse.TechAnalysisItem> {
        //создаем channel со свойствами по умолчанию
        val channel = InvestApi.defaultChannel(
            token = System.getProperty("token"),
            target = System.getProperty("target")
        )
        //создаем объект API
        val investApi = InvestApi.createApi(channel)
        val marketDataService = investApi.marketDataServiceSync

        // Запрашиваем значения индикатора
        val response = marketDataService.getTechAnalysis(request)
        channel.shutdown()
        // Возвращаем список значений индикатора
        return response.technicalIndicatorsList.also { println(it) }
    }
}