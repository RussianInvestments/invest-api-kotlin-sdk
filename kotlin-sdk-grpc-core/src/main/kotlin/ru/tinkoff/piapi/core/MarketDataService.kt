package ru.tinkoff.piapi.core

import kotlinx.coroutines.runBlocking
import ru.tinkoff.piapi.contract.v1.GetCandlesRequest
import ru.tinkoff.piapi.contract.v1.GetCandlesResponse
import ru.tinkoff.piapi.contract.v1.GetClosePricesRequest
import ru.tinkoff.piapi.contract.v1.GetClosePricesResponse
import ru.tinkoff.piapi.contract.v1.GetLastPricesRequest
import ru.tinkoff.piapi.contract.v1.GetLastPricesResponse
import ru.tinkoff.piapi.contract.v1.GetLastTradesRequest
import ru.tinkoff.piapi.contract.v1.GetLastTradesResponse
import ru.tinkoff.piapi.contract.v1.GetOrderBookRequest
import ru.tinkoff.piapi.contract.v1.GetOrderBookResponse
import ru.tinkoff.piapi.contract.v1.GetTradingStatusRequest
import ru.tinkoff.piapi.contract.v1.GetTradingStatusResponse
import ru.tinkoff.piapi.contract.v1.GetTradingStatusesRequest
import ru.tinkoff.piapi.contract.v1.GetTradingStatusesResponse
import ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpcKt

class MarketDataService(private val coroutineStub: MarketDataServiceGrpcKt.MarketDataServiceCoroutineStub) {
    suspend fun getCandles(request: GetCandlesRequest):
            GetCandlesResponse = coroutineStub.getCandles(request)

    fun getCandlesSync(request: GetCandlesRequest):
            GetCandlesResponse = runBlocking { getCandles(request) }

    suspend fun getLastPrices(request: GetLastPricesRequest):
            GetLastPricesResponse = coroutineStub.getLastPrices(request)

    fun getLastPricesSync(request: GetLastPricesRequest):
            GetLastPricesResponse = runBlocking { getLastPrices(request) }

    suspend fun getOrderBook(request: GetOrderBookRequest):
            GetOrderBookResponse = coroutineStub.getOrderBook(request)

    fun getOrderBookSync(request: GetOrderBookRequest):
            GetOrderBookResponse = runBlocking { getOrderBook(request) }

    suspend fun getTradingStatus(
        request: GetTradingStatusRequest
    ): GetTradingStatusResponse = coroutineStub.getTradingStatus(request)

    fun getTradingStatusSync(
        request: GetTradingStatusRequest
    ): GetTradingStatusResponse = runBlocking { getTradingStatus(request) }

    suspend fun getTradingStatuses(
        request: GetTradingStatusesRequest
    ): GetTradingStatusesResponse = coroutineStub.getTradingStatuses(request)

    fun getTradingStatusesSync(
        request: GetTradingStatusesRequest
    ): GetTradingStatusesResponse = runBlocking { getTradingStatuses(request) }

    suspend fun getLastTrades(request: GetLastTradesRequest):
            GetLastTradesResponse = coroutineStub.getLastTrades(request)

    fun getLastTradesSync(request: GetLastTradesRequest):
            GetLastTradesResponse = runBlocking { getLastTrades(request) }

    suspend fun getClosePrices(
        request: GetClosePricesRequest
    ): GetClosePricesResponse = coroutineStub.getClosePrices(request)

    fun getClosePricesSync(
        request: GetClosePricesRequest
    ): GetClosePricesResponse = runBlocking { getClosePrices(request) }
}
