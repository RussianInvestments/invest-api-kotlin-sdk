package ru.tinkoff.piapi.core

import kotlinx.coroutines.runBlocking
import ru.tinkoff.piapi.contract.v1.CancelOrderRequest
import ru.tinkoff.piapi.contract.v1.CancelOrderResponse
import ru.tinkoff.piapi.contract.v1.CloseSandboxAccountRequest
import ru.tinkoff.piapi.contract.v1.CloseSandboxAccountResponse
import ru.tinkoff.piapi.contract.v1.GetAccountsRequest
import ru.tinkoff.piapi.contract.v1.GetAccountsResponse
import ru.tinkoff.piapi.contract.v1.GetOperationsByCursorRequest
import ru.tinkoff.piapi.contract.v1.GetOperationsByCursorResponse
import ru.tinkoff.piapi.contract.v1.GetOrderStateRequest
import ru.tinkoff.piapi.contract.v1.GetOrdersRequest
import ru.tinkoff.piapi.contract.v1.GetOrdersResponse
import ru.tinkoff.piapi.contract.v1.OpenSandboxAccountRequest
import ru.tinkoff.piapi.contract.v1.OpenSandboxAccountResponse
import ru.tinkoff.piapi.contract.v1.OperationsRequest
import ru.tinkoff.piapi.contract.v1.OperationsResponse
import ru.tinkoff.piapi.contract.v1.OrderState
import ru.tinkoff.piapi.contract.v1.PortfolioRequest
import ru.tinkoff.piapi.contract.v1.PortfolioResponse
import ru.tinkoff.piapi.contract.v1.PositionsRequest
import ru.tinkoff.piapi.contract.v1.PositionsResponse
import ru.tinkoff.piapi.contract.v1.PostOrderRequest
import ru.tinkoff.piapi.contract.v1.PostOrderResponse
import ru.tinkoff.piapi.contract.v1.ReplaceOrderRequest
import ru.tinkoff.piapi.contract.v1.SandboxPayInRequest
import ru.tinkoff.piapi.contract.v1.SandboxPayInResponse
import ru.tinkoff.piapi.contract.v1.SandboxServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.WithdrawLimitsRequest
import ru.tinkoff.piapi.contract.v1.WithdrawLimitsResponse

class SandboxService(private val coroutineStub: SandboxServiceGrpcKt.SandboxServiceCoroutineStub) {
    suspend fun openSandboxAccount(
        request: OpenSandboxAccountRequest
    ): OpenSandboxAccountResponse = coroutineStub.openSandboxAccount(request)

    fun openSandboxAccountSync(
        request: OpenSandboxAccountRequest
    ): OpenSandboxAccountResponse = runBlocking { openSandboxAccount(request) }

    suspend fun getSandboxAccounts(
        request: GetAccountsRequest
    ): GetAccountsResponse = coroutineStub.getSandboxAccounts(request)

    fun getSandboxAccountsSync(
        request: GetAccountsRequest
    ): GetAccountsResponse = runBlocking { getSandboxAccounts(request) }

    suspend fun closeSandboxAccount(
        request: CloseSandboxAccountRequest
    ): CloseSandboxAccountResponse = coroutineStub.closeSandboxAccount(request)

    fun closeSandboxAccountSync(
        request: CloseSandboxAccountRequest
    ): CloseSandboxAccountResponse = runBlocking { closeSandboxAccount(request) }

    suspend fun postSandboxOrder(request: PostOrderRequest):
            PostOrderResponse = coroutineStub.postSandboxOrder(request)

    fun postSandboxOrderSync(request: PostOrderRequest):
            PostOrderResponse = runBlocking { postSandboxOrder(request) }

    suspend fun replaceSandboxOrder(
        request: ReplaceOrderRequest
    ): PostOrderResponse = coroutineStub.replaceSandboxOrder(request)

    fun replaceSandboxOrderSync(
        request: ReplaceOrderRequest
    ): PostOrderResponse = runBlocking { replaceSandboxOrder(request) }

    suspend fun getSandboxOrders(request: GetOrdersRequest):
            GetOrdersResponse = coroutineStub.getSandboxOrders(request)

    fun getSandboxOrdersSync(request: GetOrdersRequest):
            GetOrdersResponse = runBlocking { getSandboxOrders(request) }

    suspend fun cancelSandboxOrder(
        request: CancelOrderRequest
    ): CancelOrderResponse = coroutineStub.cancelSandboxOrder(request)

    fun cancelSandboxOrderSync(
        request: CancelOrderRequest
    ): CancelOrderResponse = runBlocking { cancelSandboxOrder(request) }

    suspend fun getSandboxOrderState(
        request: GetOrderStateRequest
    ): OrderState = coroutineStub.getSandboxOrderState(request)

    fun getSandboxOrderStateSync(
        request: GetOrderStateRequest
    ): OrderState = runBlocking { getSandboxOrderState(request) }

    suspend fun getSandboxPositions(
        request: PositionsRequest
    ): PositionsResponse = coroutineStub.getSandboxPositions(request)

    fun getSandboxPositionsSync(
        request: PositionsRequest
    ): PositionsResponse = runBlocking { getSandboxPositions(request) }

    suspend fun getSandboxOperations(
        request: OperationsRequest
    ): OperationsResponse = coroutineStub.getSandboxOperations(request)

    fun getSandboxOperationsSync(
        request: OperationsRequest
    ): OperationsResponse = runBlocking { getSandboxOperations(request) }

    suspend fun getSandboxOperationsByCursor(
        request: GetOperationsByCursorRequest
    ): GetOperationsByCursorResponse = coroutineStub.getSandboxOperationsByCursor(request)

    fun getSandboxOperationsByCursorSync(
        request: GetOperationsByCursorRequest
    ): GetOperationsByCursorResponse = runBlocking { getSandboxOperationsByCursor(request) }

    suspend fun getSandboxPortfolio(
        request: PortfolioRequest
    ): PortfolioResponse = coroutineStub.getSandboxPortfolio(request)

    fun getSandboxPortfolioSync(
        request: PortfolioRequest
    ): PortfolioResponse = runBlocking { getSandboxPortfolio(request) }

    suspend fun sandboxPayIn(request: SandboxPayInRequest):
            SandboxPayInResponse = coroutineStub.sandboxPayIn(request)

    fun sandboxPayInSync(request: SandboxPayInRequest):
            SandboxPayInResponse = runBlocking { sandboxPayIn(request) }

    suspend fun getSandboxWithdrawLimits(
        request: WithdrawLimitsRequest
    ): WithdrawLimitsResponse = coroutineStub.getSandboxWithdrawLimits(request)

    fun getSandboxWithdrawLimitsSync(
        request: WithdrawLimitsRequest
    ): WithdrawLimitsResponse = runBlocking { getSandboxWithdrawLimits(request) }
}
