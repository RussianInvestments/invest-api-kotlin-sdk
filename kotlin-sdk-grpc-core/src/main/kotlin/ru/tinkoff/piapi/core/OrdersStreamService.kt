package ru.tinkoff.piapi.core

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.tinkoff.piapi.contract.v1.OrdersStreamServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.TradesStreamRequest
import ru.tinkoff.piapi.contract.v1.TradesStreamResponse

class OrdersStreamService(private val coroutineStub: OrdersStreamServiceGrpcKt.OrdersStreamServiceCoroutineStub) {
    suspend fun tradesStream(request: TradesStreamRequest, vararg consumers: (it: TradesStreamResponse) -> Unit):
            Job = coroutineScope {
        var stub = coroutineStub.tradesStream(request).cancellable()
        launch {
            consumers.forEachIndexed { index, consumer ->
                if (index == consumers.size - 1)
                    stub.collect { consumer.invoke(it) }
                else
                    stub = stub.map { consumer.invoke(it); it }
            }
        }
    }
}
