package ru.tinvest.piapi.example

import io.grpc.ManagedChannel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.tinkoff.piapi.contract.v1.*
import ru.tinvest.piapi.core.*
import ru.tinvest.piapi.core.utils.toBigDecimal
import ru.tinvest.piapi.core.utils.toQuotation
import java.math.BigDecimal
import java.util.*

@Suppress("DuplicatedCode")
class OrdersServiceAsyncExample {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(OrdersServiceSyncExample::class.java)

        // UID акции Банк ВТБ на Московской бирже
        private const val SHARE_UID = "8e2b0325-0292-4654-8a18-4f63ed3b0e09"
    }

    private fun createInvestApi(): Pair<InvestApi, ManagedChannel> {
        //создаем channel со свойствами по умолчанию
        val channel = InvestApi.defaultChannel(
            token = System.getProperty("token"),
            target = System.getProperty("target-main")
        )
        //создаем объект API
        val investApi = InvestApi.createApi(channel)
        return Pair(investApi, channel)
    }

    suspend fun exampleLimitOrder() {
        val (investApi, channel) = createInvestApi()
        val instrumentsService = investApi.instrumentsServiceAsync
        val marketService = investApi.marketDataServiceAsync
        val userService = investApi.usersServiceAsync
        val ordersService = investApi.ordersServiceAsync
        val ordersStreamService = investApi.ordersStreamServiceAsync

        // берем первый счет из списка
        val account = getBrokerAccount(userService)
        val priceStep = getInstrument(instrumentsService).minPriceIncrement.toBigDecimal()
        val lastPrice = getLastPrice(marketService).price.toBigDecimal()
        logger.info("Шаг цены: $priceStep")
        logger.info("Последняя цена: $lastPrice")

        // цена на 10 шагов ниже текущей
        val price = lastPrice.minus(priceStep.multiply(BigDecimal.TEN))

        // собираем запрос на подписку ордеров пользователя
        val ordersRequest = OrderStateStreamRequest.newBuilder()
            .addAccounts(account.id) // id счета
            .build()

        val orderId = UUID.randomUUID().toString() // сгенерированный ключ идемпотентности ордера
        // собираем запрос для создания ордера асинхронно
        val postOrderRequest = PostOrderAsyncRequest.newBuilder()
            .setAccountId(account.id) // id счета
            .setInstrumentId(SHARE_UID) // uid инструмента
            .setDirection(OrderDirection.ORDER_DIRECTION_BUY) // направление ордера
            .setQuantity(1) // количество инструмента
            .setOrderType(OrderType.ORDER_TYPE_LIMIT) // тип ордера
            .setPrice(price.toQuotation()) // лимитная цена
            .setOrderId(orderId) // ключ идемпотентности
            .build()

        runBlocking {
            val ordersJob = async {
                ordersStreamService.orderStateStream(
                    ordersRequest,
                    {
                        if (it.hasSubscription() && it.subscription.status == ResultSubscriptionStatus.RESULT_SUBSCRIPTION_STATUS_OK) {
                            logger.info("Успешная подписка на обновления ордеров")
                            // создаём ордер, при успешном подключении к стриму
                            runBlocking { ordersService.postOrderAsync(postOrderRequest) }
                        }
                    },
                    {
                        if (it.hasOrderState() && it.orderState.orderRequestId.equals(orderId) &&
                            it.orderState.lotsLeft > 0) {
                            logger.info("Ордер: $it")
                            // отменяем созданный ордер
                            val status = it.orderState.executionReportStatus
                            if (status == OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_NEW ||
                                status == OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_FILL ||
                                status == OrderExecutionReportStatus.EXECUTION_REPORT_STATUS_PARTIALLYFILL) {
                                logger.info("Отменяем ордер")
                                runBlocking {
                                    val response = ordersService.cancelOrder(
                                        CancelOrderRequest.newBuilder()
                                            .setAccountId(account.id) // id счета
                                            .setOrderId(it.orderState.orderId) // id ордера для отмены
                                            .build()
                                    )
                                    logger.info("Ответ: $response")
                                }
                            }
                        }
                    }
                )
            }

            delay(3000)
            ordersJob.cancel()
        }
        channel.shutdown()
    }

    suspend fun exampleMarketOrder() {
        val (investApi, channel) = createInvestApi()
        val userService = investApi.usersServiceAsync
        val ordersService = investApi.ordersServiceAsync
        val ordersStreamService = investApi.ordersStreamServiceAsync

        // берем первый счет из списка
        val account = getBrokerAccount(userService)

        // собираем запрос на подписку ордеров пользователя
        val ordersRequest = OrderStateStreamRequest.newBuilder()
            .addAccounts(account.id) // id счета
            .build()

        // собираем запрос на подписку ордеров пользователя
        val tradesRequest = TradesStreamRequest.newBuilder()
            .addAccounts(account.id) // id счета
            .build()

        runBlocking {
            // подписываемся на стрим ордеров
            val ordersJob = async {
                ordersStreamService.orderStateStream(
                    ordersRequest,
                    { if (it.hasOrderState()) logger.info("Order state update: $it") }
                )
            }
            // подписываемся на стрим сделок
            val tradesJob = async {
                ordersStreamService.tradesStream(
                    tradesRequest,
                    { if (it.hasOrderTrades()) logger.info("New trade: $it") }
                )
            }
            delay(1000)

            // выставляем best-price ордер
            val postOrder = ordersService.postOrderAsync(
                PostOrderAsyncRequest.newBuilder()
                    .setAccountId(account.id) // id счета
                    .setInstrumentId(SHARE_UID) // uid инструмента
                    .setDirection(OrderDirection.ORDER_DIRECTION_BUY) // навправление ордера
                    .setQuantity(1) // количество инструмента
                    .setOrderType(OrderType.ORDER_TYPE_BESTPRICE) // тип ордера
                    .setOrderId(UUID.randomUUID().toString()) // ключ идемпотентности
                    .build()
            )
            logger.info("id ордера: ${postOrder.tradeIntentId}")
            delay(1000)

            // закрываем позицию по рынку
            ordersService.postOrderAsync(
                PostOrderAsyncRequest.newBuilder()
                    .setAccountId(account.id) // id счета
                    .setInstrumentId(SHARE_UID) // uid инструмента
                    .setDirection(OrderDirection.ORDER_DIRECTION_SELL) // навправление ордера
                    .setQuantity(1) // количество инструмента
                    .setOrderType(OrderType.ORDER_TYPE_BESTPRICE) // тип ордера
                    .setOrderId(UUID.randomUUID().toString()) // ключ идемпотентности
                    .build()
            )
            delay(1000)
            ordersJob.cancel()
            tradesJob.cancel()
        }
        channel.shutdown()
    }

    suspend fun exampleGetOrders() {
        val (investApi, channel) = createInvestApi()
        val userService = investApi.usersServiceAsync
        val ordersService = investApi.ordersServiceAsync

        // получаем брокерский аккаунт
        val account = getBrokerAccount(userService)

        // получаем список активных ордеров
        val activeOrders = ordersService.getOrders(
            GetOrdersRequest.newBuilder()
                .setAccountId(account.id)
                .build()
        )

        logger.info("Активных ордеров: ${activeOrders.ordersCount}")
        activeOrders.ordersList.forEach { logger.info("$it") }
        channel.shutdown()
    }

    suspend fun exampleGetMaxLots() {
        val (investApi, channel) = createInvestApi()
        val ordersService = investApi.ordersServiceAsync
        val userService = investApi.usersServiceAsync
        val marketService = investApi.marketDataServiceAsync

        // берем первый счет из списка
        val account = getBrokerAccount(userService)
        val lastPrice = getLastPrice(marketService)
        logger.info("Цена инструмента: ${lastPrice.price.toBigDecimal()}")

        // получаем количество доступных лотов для покупки/продажи
        val maxLotsResponse = ordersService.getMaxLots(
            GetMaxLotsRequest.newBuilder()
                .setAccountId(account.id) // id счета
                .setInstrumentId(SHARE_UID) // uid инструмента
                .setPrice(lastPrice.price) // цена инструмента для расчета
                .build()
        )

        logger.info("Лимиты на покупку")
        logger.info("Денежных средств доступно: ${maxLotsResponse.buyLimits.buyMoneyAmount.toBigDecimal()}")
        logger.info("Доступно лотов для покупки по указанной цене: ${maxLotsResponse.buyLimits.buyMaxLots}")
        logger.info("Доступно лотов для покупки по рыночной цене: ${maxLotsResponse.buyLimits.buyMaxMarketLots}")
        logger.info("Лимиты на продажу")
        logger.info("Доступно лотов для продажи: ${maxLotsResponse.sellLimits.sellMaxLots}")
        channel.shutdown()
    }

    suspend fun exampleGetOrderPrice() {
        val (investApi, channel) = createInvestApi()
        val ordersService = investApi.ordersServiceAsync
        val userService = investApi.usersServiceAsync
        val marketService = investApi.marketDataServiceAsync

        // получаем брокерский аккаунт
        val account = getBrokerAccount(userService)
        val lastPrice = getLastPrice(marketService)
        logger.info("Цена инструмента: ${lastPrice.price.toBigDecimal()}")

        val orderPrice = ordersService.getOrderPrice(
            GetOrderPriceRequest.newBuilder()
                .setAccountId(account.id) // id аккаунта
                .setInstrumentId(SHARE_UID) // uid инструмента
                .setPrice(lastPrice.price) // лимитная цена за лот
                .setQuantity(1) // количество инструмента
                .setDirection(OrderDirection.ORDER_DIRECTION_BUY) // направление операции
                .build()
        )

        logger.info("Итоговая стоимость заявки: ${orderPrice.totalOrderAmount.toBigDecimal()}")
        logger.info("Стоимость заявки без комиссий: ${orderPrice.initialOrderAmount.toBigDecimal()}")
        logger.info("Запрошено лотов: ${orderPrice.lotsRequested}")
        logger.info("Общая комиссия: ${orderPrice.executedCommission.toBigDecimal()}")
        logger.info("Сервисная комиссия: ${orderPrice.serviceCommission.toBigDecimal()}")
        logger.info("Комиссия за проведение сделки: ${orderPrice.dealCommission.toBigDecimal()}")
        channel.shutdown()
    }

    private suspend fun getBrokerAccount(userService: UsersServiceAsync): Account {
        // получаем все счета пользователя
        val accountsResponse = userService.getAccounts(GetAccountsRequest.getDefaultInstance())
        // отдаем из списка брокерский аккаунт
        return accountsResponse.accountsList
            .first { it.type.equals(AccountType.ACCOUNT_TYPE_TINKOFF) }
            .also { logger.info("Аккаунт: $it") }
    }

    private suspend fun getLastPrice(marketService: MarketDataServiceAsync): LastPrice {
        // получаем цену последней сделки по инструменту
        val lastPriceResponse = marketService.getLastPrices(
            GetLastPricesRequest.newBuilder()
                .addInstrumentId(SHARE_UID) // uid инструмента
                .setLastPriceType(LastPriceType.LAST_PRICE_DEALER) // источник цены -- брокер
                .build()
        )
        return lastPriceResponse.lastPricesList.first()
    }

    private suspend fun getInstrument(instrumentsService: InstrumentsServiceAsync): Instrument {
        // получение инструмента
        val instrumentResponse = instrumentsService.getInstrumentBy(
            InstrumentRequest.newBuilder()
                .setIdType(InstrumentIdType.INSTRUMENT_ID_TYPE_UID) // устанавливаем тип id как UID
                .setId(SHARE_UID) // uid инструмента
                .build()
        )
        return instrumentResponse.instrument
    }
}