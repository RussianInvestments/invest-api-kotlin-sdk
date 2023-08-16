package ru.tinkoff.piapi.core

import kotlinx.coroutines.runBlocking
import ru.tinkoff.piapi.contract.v1.BrokerReportRequest
import ru.tinkoff.piapi.contract.v1.BrokerReportResponse
import ru.tinkoff.piapi.contract.v1.GetDividendsForeignIssuerRequest
import ru.tinkoff.piapi.contract.v1.GetDividendsForeignIssuerResponse
import ru.tinkoff.piapi.contract.v1.GetOperationsByCursorRequest
import ru.tinkoff.piapi.contract.v1.GetOperationsByCursorResponse
import ru.tinkoff.piapi.contract.v1.OperationsRequest
import ru.tinkoff.piapi.contract.v1.OperationsResponse
import ru.tinkoff.piapi.contract.v1.OperationsServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.PortfolioRequest
import ru.tinkoff.piapi.contract.v1.PortfolioResponse
import ru.tinkoff.piapi.contract.v1.PositionsRequest
import ru.tinkoff.piapi.contract.v1.PositionsResponse
import ru.tinkoff.piapi.contract.v1.WithdrawLimitsRequest
import ru.tinkoff.piapi.contract.v1.WithdrawLimitsResponse

data class OperationsService(private val coroutineStub: OperationsServiceGrpcKt.OperationsServiceCoroutineStub) {

    suspend fun getOperations(request: OperationsRequest): OperationsResponse = coroutineStub.getOperations(request)

    fun getOperationsSync(request: OperationsRequest): OperationsResponse = runBlocking { getOperations(request) }

    suspend fun getPortfolio(request: PortfolioRequest): PortfolioResponse = coroutineStub.getPortfolio(request)

    fun getPortfolioSync(request: PortfolioRequest): PortfolioResponse = runBlocking { getPortfolio(request) }

    suspend fun getPositions(request: PositionsRequest): PositionsResponse = coroutineStub.getPositions(request)

    fun getPositionsSync(request: PositionsRequest): PositionsResponse = runBlocking { getPositions(request) }

    suspend fun getWithdrawLimits(
        request: WithdrawLimitsRequest
    ): WithdrawLimitsResponse = coroutineStub.getWithdrawLimits(request)

    fun getWithdrawLimitsSync(
        request: WithdrawLimitsRequest
    ): WithdrawLimitsResponse = runBlocking { getWithdrawLimits(request) }

    suspend fun getBrokerReport(
        request: BrokerReportRequest
    ): BrokerReportResponse = coroutineStub.getBrokerReport(request)

    fun getBrokerReportSync(
        request: BrokerReportRequest
    ): BrokerReportResponse = runBlocking { getBrokerReport(request) }

    suspend fun getDividendsForeignIssuer(
        request: GetDividendsForeignIssuerRequest
    ): GetDividendsForeignIssuerResponse = coroutineStub.getDividendsForeignIssuer(request)

    fun getDividendsForeignIssuerSync(
        request: GetDividendsForeignIssuerRequest
    ): GetDividendsForeignIssuerResponse = runBlocking { getDividendsForeignIssuer(request) }

    suspend fun getOperationsByCursor(
        request: GetOperationsByCursorRequest
    ): GetOperationsByCursorResponse = coroutineStub.getOperationsByCursor(request)

    fun getOperationsByCursorSync(
        request: GetOperationsByCursorRequest
    ): GetOperationsByCursorResponse = runBlocking { getOperationsByCursor(request) }
}
