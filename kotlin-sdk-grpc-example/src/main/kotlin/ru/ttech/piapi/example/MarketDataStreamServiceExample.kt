package ru.ttech.piapi.example

import io.grpc.ManagedChannel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.tinkoff.piapi.contract.v1.CandleInstrument
import ru.tinkoff.piapi.contract.v1.MarketDataRequest
import ru.tinkoff.piapi.contract.v1.MarketDataServerSideStreamRequest
import ru.tinkoff.piapi.contract.v1.OrderBookInstrument
import ru.tinkoff.piapi.contract.v1.SubscribeCandlesRequest
import ru.tinkoff.piapi.contract.v1.SubscribeOrderBookRequest
import ru.tinkoff.piapi.contract.v1.SubscriptionAction
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval
import ru.ttech.piapi.core.InvestApi
import java.util.stream.Collectors

class MarketDataStreamServiceExample {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MarketDataStreamServiceExample::class.java)
    }

    fun marketDataServerSideStream(channel: ManagedChannel) {
        val investApi = InvestApi.createApi(channel)
        val marketDataStreamService = investApi.marketDataStreamServiceAsync
        val orderBookInstruments = System.getProperty("instrumentIds").split(",")
            .stream().map { OrderBookInstrument.newBuilder().setInstrumentId(it).setDepth(1).build() }
            .collect(Collectors.toList())
        val subscribeOrderBookRequest = SubscribeOrderBookRequest.newBuilder()
            .setSubscriptionAction(SubscriptionAction.SUBSCRIPTION_ACTION_SUBSCRIBE)
            .addAllInstruments(orderBookInstruments)
        // Создается запрос на подписку OrderBook инструментов
        val request = MarketDataServerSideStreamRequest.newBuilder().setSubscribeOrderBookRequest(
            subscribeOrderBookRequest
        ).build()
        runBlocking {
            //выполняем запрос
            val job = async {
                marketDataStreamService.marketDataServerSideStream(
                    request,
                    // Две callback функции (количество не ограничено), для обработки сообщений.
                    // Первая выводит в лог сообщение о получении сообщения типа SubscribeOrderBookResponse
                    // Вторая функция выводит в лог полученное сообщение вне зависимости от типа
                    { if (it.hasSubscribeOrderBookResponse()) logger.info("Got SubscribeOrderBookResponse.") },
                    { message -> logger.info("Here it is the message: $message") })
            }
            //через 3 секунды закрываем соединение
            delay(3000)
            job.cancel()
        }

    }

    fun biDirectionalStreamExample(channel: ManagedChannel) {
            val investApi = InvestApi.createApi(channel)
            val marketDataStreamService = investApi.marketDataStreamServiceAsync

            // Примечание: replay = 1 - число сообщений, которые должны быть повторно отправлены серверу при установке соединения.
            // При значении 0 возможна потеря ранее отправленных сообщений
            val requests = MutableSharedFlow<MarketDataRequest>(replay = 1)
            runBlocking {
                val job = async {
                    marketDataStreamService.marketDataStream(
                        requests,
                        // Две callback функции (количество не ограничено), для обработки сообщений.
                        // Первая выводит в лог сообщение о получении сообщения типа SubscribeOrderBookResponse
                        // Вторая функция выводит в лог полученное сообщение вне зависимости от типа
                        { if (it.hasSubscribeOrderBookResponse()) logger.info("Got SubscribeOrderBookResponse.") },
                        { message -> logger.info("Here it is the message: $message") })
                }
                // подготовка запроса на подписку на OrderBook для инструментов
                val orderBookInstruments = System.getProperty("instrumentIds").split(",")
                    .stream().map { OrderBookInstrument.newBuilder().setInstrumentId(it).setDepth(1).build() }
                    .collect(Collectors.toList())
                var subscribeOrderBookRequest = SubscribeOrderBookRequest.newBuilder()
                    .setSubscriptionAction(SubscriptionAction.SUBSCRIPTION_ACTION_SUBSCRIBE)
                    .addAllInstruments(orderBookInstruments)
                var request = MarketDataRequest.newBuilder().setSubscribeOrderBookRequest(
                    subscribeOrderBookRequest
                ).build()
                // отправка запроса
                requests.emit(request)

                // в течение 1 секунды получаем сообщения
                delay(1000)

                // подготовка запроса на подписку на свечи
                val candleInstruments = System.getProperty("instrumentIds").split(",")
                    .stream().map {
                        CandleInstrument.newBuilder()
                            .setInstrumentId(it)
                            .setInterval(SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE)
                            .build()
                    }
                    .collect(Collectors.toList())
                val subscribeCandleRequest = SubscribeCandlesRequest.newBuilder()
                    .setSubscriptionAction(SubscriptionAction.SUBSCRIPTION_ACTION_SUBSCRIBE)
                    .addAllInstruments(candleInstruments)
                request = MarketDataRequest.newBuilder().setSubscribeCandlesRequest(subscribeCandleRequest).build()
                // отправка запроса
                requests.emit(request)

                // в течение 1 секунды получаем сообщения как OrderBook, так и Candles
                delay(1000)

                // подготовка запроса отписки от OrderBook
                subscribeOrderBookRequest = SubscribeOrderBookRequest.newBuilder()
                    .setSubscriptionAction(SubscriptionAction.SUBSCRIPTION_ACTION_UNSUBSCRIBE)
                    .addAllInstruments(orderBookInstruments)
                request = MarketDataRequest.newBuilder().setSubscribeOrderBookRequest(
                    subscribeOrderBookRequest
                ).build()
                // отправка запроса
                requests.emit(request)

                //через 3 секунды закрываем соединение
                delay(3000)
                job.cancel()
            }

    }
}