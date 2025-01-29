package ru.tinvest.piapi.example

import io.grpc.ManagedChannel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.tinkoff.piapi.contract.v1.*
import ru.tinvest.piapi.core.InstrumentsServiceSync
import ru.tinvest.piapi.core.InvestApi
import ru.tinvest.piapi.core.MarketDataServiceSync
import ru.tinvest.piapi.core.UsersServiceSync
import ru.tinvest.piapi.core.utils.toBigDecimal
import ru.tinvest.piapi.core.utils.toQuotation
import java.math.BigDecimal
import java.util.UUID

@Suppress("DuplicatedCode")
class OrdersServiceSyncExample {

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

    fun exampleLimitOrder() {
        val (investApi, channel) = createInvestApi()
        val instrumentsService = investApi.instrumentsServiceSync
        val marketService = investApi.marketDataServiceSync
        val userService = investApi.usersServiceSync
        val ordersService = investApi.ordersServiceSync

        // берем первый счет из списка
        val account = getBrokerAccount(userService)
        val priceStep = getInstrument(instrumentsService).minPriceIncrement.toBigDecimal()
        val lastPrice = getLastPrice(marketService).price.toBigDecimal()
        logger.info("Шаг цены: $priceStep")
        logger.info("Последняя цена: $lastPrice")

        // цена на 10 шагов ниже текущей
        val price = lastPrice.minus(priceStep.multiply(BigDecimal.TEN))

        // выставляем лимитный ордер
        val postOrder = ordersService.postOrder(
            PostOrderRequest.newBuilder()
                .setAccountId(account.id) // id счета
                .setInstrumentId(SHARE_UID) // uid инструмента
                .setDirection(OrderDirection.ORDER_DIRECTION_BUY) // навправление ордера
                .setQuantity(1) // количество инструмента
                .setOrderType(OrderType.ORDER_TYPE_LIMIT) // тип ордера
                .setPrice(price.toQuotation()) // лимитная цена
                .build()
        )
        logger.info("id ордера: ${postOrder.orderId}")

        // получаем ордер
        val orderState = ordersService.getOrderState(
            GetOrderStateRequest.newBuilder()
                .setAccountId(account.id) // id аккаунта
                .setOrderId(postOrder.orderId) // id ордера
                .build()
        )
        logger.info("Ордер: $orderState")

        val newPrice = price.minus(priceStep.multiply(BigDecimal.TEN))
        val updatedOrderState = ordersService.replaceOrder(
            ReplaceOrderRequest.newBuilder()
                .setIdempotencyKey(UUID.randomUUID().toString()) // ключ идемпотентности
                .setAccountId(account.id) // id аккаунта
                .setOrderId(postOrder.orderId) // id ордера
                .setQuantity(2) // количество инструмента
                .setPrice(newPrice.toQuotation()) // лимитная цена
                .build()
        )
        logger.info("Обновленный ордер: $updatedOrderState")

        // отменяем ордер
        ordersService.cancelOrder(
            CancelOrderRequest.newBuilder()
                .setAccountId(account.id) // id счета
                .setOrderId(updatedOrderState.orderId) // id ордера для отмены
                .build()
        )
        channel.shutdown()
    }

    fun exampleMarketOrder() {
        val (investApi, channel) = createInvestApi()
        val userService = investApi.usersServiceSync
        val ordersService = investApi.ordersServiceSync
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
            val postOrder = ordersService.postOrder(
                PostOrderRequest.newBuilder()
                    .setAccountId(account.id) // id счета
                    .setInstrumentId(SHARE_UID) // uid инструмента
                    .setDirection(OrderDirection.ORDER_DIRECTION_BUY) // навправление ордера
                    .setQuantity(1) // количество инструмента
                    .setOrderType(OrderType.ORDER_TYPE_BESTPRICE) // тип ордера
                    .build()
            )
            logger.info("id ордера: ${postOrder.orderId}")
            delay(1000)

            // закрываем позицию по рынку
            ordersService.postOrder(
                PostOrderRequest.newBuilder()
                    .setAccountId(account.id) // id счета
                    .setInstrumentId(SHARE_UID) // uid инструмента
                    .setDirection(OrderDirection.ORDER_DIRECTION_SELL) // навправление ордера
                    .setQuantity(1) // количество инструмента
                    .setOrderType(OrderType.ORDER_TYPE_BESTPRICE) // тип ордера
                    .build()
            )
            delay(1000)
            ordersJob.cancel()
            tradesJob.cancel()
        }
        channel.shutdown()
    }

    fun exampleGetOrders() {
        val (investApi, channel) = createInvestApi()
        val userService = investApi.usersServiceSync
        val ordersService = investApi.ordersServiceSync

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

    fun exampleGetMaxLots() {
        val (investApi, channel) = createInvestApi()
        val ordersService = investApi.ordersServiceSync
        val userService = investApi.usersServiceSync
        val marketService = investApi.marketDataServiceSync

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

    fun exampleGetOrderPrice() {
        val (investApi, channel) = createInvestApi()
        val ordersService = investApi.ordersServiceSync
        val userService = investApi.usersServiceSync
        val marketService = investApi.marketDataServiceSync

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

    private fun getBrokerAccount(userService: UsersServiceSync): Account {
        // получаем все счета пользователя
        val accountsResponse = userService.getAccounts(GetAccountsRequest.getDefaultInstance())
        // отдаем из списка брокерский аккаунт
        return accountsResponse.accountsList
            .first { it.type.equals(AccountType.ACCOUNT_TYPE_TINKOFF) }
            .also { logger.info("Аккаунт: $it")}
    }

    private fun getLastPrice(marketService: MarketDataServiceSync): LastPrice {
        // получаем цену последней сделки по инструменту
        val lastPriceResponse = marketService.getLastPrices(
            GetLastPricesRequest.newBuilder()
                .addInstrumentId(SHARE_UID) // uid инструмента
                .setLastPriceType(LastPriceType.LAST_PRICE_DEALER) // источник цены -- брокер
                .build()
        )
        return lastPriceResponse.lastPricesList.first()
    }

    private fun getInstrument(instrumentsService: InstrumentsServiceSync): Instrument {
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