package ru.tinkoff.piapi.core

import kotlinx.coroutines.runBlocking
import ru.tinkoff.piapi.contract.v1.CancelOrderRequest
import ru.tinkoff.piapi.contract.v1.CancelOrderResponse
import ru.tinkoff.piapi.contract.v1.GetMaxLotsRequest
import ru.tinkoff.piapi.contract.v1.GetMaxLotsResponse
import ru.tinkoff.piapi.contract.v1.GetOrderStateRequest
import ru.tinkoff.piapi.contract.v1.GetOrdersRequest
import ru.tinkoff.piapi.contract.v1.GetOrdersResponse
import ru.tinkoff.piapi.contract.v1.OrderState
import ru.tinkoff.piapi.contract.v1.OrdersServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.PostOrderRequest
import ru.tinkoff.piapi.contract.v1.PostOrderResponse
import ru.tinkoff.piapi.contract.v1.ReplaceOrderRequest

data class OrdersService(private val coroutineStub: OrdersServiceGrpcKt.OrdersServiceCoroutineStub) {
    suspend fun postOrder(request: PostOrderRequest):
            PostOrderResponse = coroutineStub.postOrder(request)

    fun postOrderSync(request: PostOrderRequest):
            PostOrderResponse = runBlocking { postOrder(request) }

    suspend fun cancelOrder(request: CancelOrderRequest):
            CancelOrderResponse = coroutineStub.cancelOrder(request)

    fun cancelOrderSync(request: CancelOrderRequest):
            CancelOrderResponse = runBlocking { cancelOrder(request) }

    suspend fun getOrderState(request: GetOrderStateRequest):
            OrderState = coroutineStub.getOrderState(request)

    fun getOrderStateSync(request: GetOrderStateRequest):
            OrderState = runBlocking { getOrderState(request) }

    suspend fun getOrders(request: GetOrdersRequest):
            GetOrdersResponse = coroutineStub.getOrders(request)

    fun getOrdersSync(request: GetOrdersRequest):
            GetOrdersResponse = runBlocking { getOrders(request) }

    suspend fun replaceOrder(request: ReplaceOrderRequest):
            PostOrderResponse = coroutineStub.replaceOrder(request)

    fun replaceOrderSync(request: ReplaceOrderRequest):
            PostOrderResponse = runBlocking { replaceOrder(request) }

    suspend fun getMaxLots(request: GetMaxLotsRequest):
            GetMaxLotsResponse = coroutineStub.getMaxLots(request)

    fun getMaxLotsSync(request: GetMaxLotsRequest):
            GetMaxLotsResponse = runBlocking { getMaxLots(request) }
}
