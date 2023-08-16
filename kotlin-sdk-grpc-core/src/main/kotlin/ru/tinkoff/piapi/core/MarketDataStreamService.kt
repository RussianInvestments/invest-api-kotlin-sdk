package ru.tinkoff.piapi.core

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.tinkoff.piapi.contract.v1.MarketDataRequest
import ru.tinkoff.piapi.contract.v1.MarketDataResponse
import ru.tinkoff.piapi.contract.v1.MarketDataServerSideStreamRequest
import ru.tinkoff.piapi.contract.v1.MarketDataStreamServiceGrpcKt

class MarketDataStreamService(private val coroutineStub: MarketDataStreamServiceGrpcKt.MarketDataStreamServiceCoroutineStub) {
    suspend fun marketDataStream(requests: Flow<MarketDataRequest>, vararg consumers: (it: MarketDataResponse) -> Unit):
            Job = coroutineScope {
        var stub = coroutineStub.marketDataStream(requests).cancellable()
        launch {
            consumers.forEachIndexed { index, consumer ->
                if (index == consumers.size - 1)
                    stub.collect { consumer.invoke(it) }
                else
                    stub = stub.map { consumer.invoke(it); it }
            }
        }
    }

    suspend fun marketDataServerSideStream(
        request: MarketDataServerSideStreamRequest,
        vararg consumers: (it: MarketDataResponse) -> Unit
    ): Job = coroutineScope {
        var stub = coroutineStub.marketDataServerSideStream(request).cancellable()
        launch {
            consumers.forEachIndexed { index, consumer ->
                if (index == consumers.size - 1)
                    stub.collect { consumer.invoke(it) }
                else
                    stub = stub.map { consumer.invoke(it); it }
            }
        }
    }


    fun closeStream(flow: Job) {
        flow.cancel()
    }
}
