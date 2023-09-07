package ru.tinkoff.piapi.core

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.tinkoff.piapi.contract.v1.OperationsStreamServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.PortfolioStreamRequest
import ru.tinkoff.piapi.contract.v1.PortfolioStreamResponse
import ru.tinkoff.piapi.contract.v1.PositionsStreamRequest
import ru.tinkoff.piapi.contract.v1.PositionsStreamResponse

data class OperationsStreamService(private val coroutineStub: OperationsStreamServiceGrpcKt.OperationsStreamServiceCoroutineStub) {
    suspend fun portfolioStream(
        request: PortfolioStreamRequest,
        vararg consumers: (it: PortfolioStreamResponse) -> Unit
    ):
            Job = coroutineScope {
        var stub = coroutineStub.portfolioStream(request).cancellable()
        launch {
            consumers.forEachIndexed { index, consumer ->
                if (index == consumers.size - 1)
                    stub.collect { consumer.invoke(it) }
                else
                    stub = stub.map { consumer.invoke(it); it }
            }
        }
    }

    suspend fun positionsStream(
        request: PositionsStreamRequest,
        vararg consumers: (it: PositionsStreamResponse) -> Unit
    ):
            Job = coroutineScope {
        var stub = coroutineStub.positionsStream(request).cancellable()
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
