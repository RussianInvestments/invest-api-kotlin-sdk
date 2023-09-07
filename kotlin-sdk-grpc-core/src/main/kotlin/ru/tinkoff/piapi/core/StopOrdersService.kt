package ru.tinkoff.piapi.core

import kotlinx.coroutines.runBlocking
import ru.tinkoff.piapi.contract.v1.CancelStopOrderRequest
import ru.tinkoff.piapi.contract.v1.CancelStopOrderResponse
import ru.tinkoff.piapi.contract.v1.GetStopOrdersRequest
import ru.tinkoff.piapi.contract.v1.GetStopOrdersResponse
import ru.tinkoff.piapi.contract.v1.PostStopOrderRequest
import ru.tinkoff.piapi.contract.v1.PostStopOrderResponse
import ru.tinkoff.piapi.contract.v1.StopOrdersServiceGrpcKt

data class StopOrdersService(private val coroutineStub: StopOrdersServiceGrpcKt.StopOrdersServiceCoroutineStub) {
    suspend fun postStopOrder(request: PostStopOrderRequest):
            PostStopOrderResponse = coroutineStub.postStopOrder(request)

    fun postStopOrderSync(request: PostStopOrderRequest):
            PostStopOrderResponse = runBlocking { postStopOrder(request) }

    suspend fun getStopOrders(request: GetStopOrdersRequest):
            GetStopOrdersResponse = coroutineStub.getStopOrders(request)

    fun getStopOrdersSync(request: GetStopOrdersRequest):
            GetStopOrdersResponse = runBlocking { getStopOrders(request) }

    suspend fun cancelStopOrder(
        request: CancelStopOrderRequest
    ): CancelStopOrderResponse = coroutineStub.cancelStopOrder(request)

    fun cancelStopOrderSync(
        request: CancelStopOrderRequest
    ): CancelStopOrderResponse = runBlocking { cancelStopOrder(request) }
}
