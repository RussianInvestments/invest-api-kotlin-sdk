package ru.tinvest.piapi.example

import io.grpc.ManagedChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.tinkoff.piapi.contract.v1.*
import ru.tinvest.piapi.core.InvestApi
import ru.tinvest.piapi.core.OperationsServiceAsync
import ru.tinvest.piapi.core.utils.toBigDecimal
import ru.tinvest.piapi.core.utils.toTimestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

@Suppress("DuplicatedCode")
class OperationsServiceAsyncExample {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(OperationsServiceAsyncExample::class.java)

        // начало периода
        private val timeFrom = Instant.now().minus(1, ChronoUnit.DAYS).toTimestamp()

        // окончание периода
        private val timeTo = Instant.now().toTimestamp()
    }

    private fun createInvestApi(): Pair<InvestApi, ManagedChannel> {
        val channel = InvestApi.defaultChannel(
            token = System.getProperty("token"),
            target = System.getProperty("target")
        )
        //создаем объект API
        val investApi = InvestApi.createApi(channel)
        return Pair(investApi, channel)
    }

    suspend fun exampleGetBrokerReport() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceAsync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceAsync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение брокерского отчёта
            val response = operationsService.getBrokerReport(
                BrokerReportRequest.newBuilder()
                    .setGenerateBrokerReportRequest(
                        GenerateBrokerReportRequest.newBuilder()
                            .setAccountId(it.id) // идентификатор счета клиента
                            .setFrom(timeFrom) // начало периода
                            .setTo(timeTo) // окончание периода
                            .build()
                    )
                    .build()
            )
            if (response.hasGenerateBrokerReportResponse()) {
                logger.info("Task id: ${response.generateBrokerReportResponse.taskId}")

                // Также можно запросить готовый отчет по его id
                val report = operationsService.getBrokerReport(
                    BrokerReportRequest.newBuilder()
                        .setGetBrokerReportRequest(
                            GetBrokerReportRequest.newBuilder()
                                .setTaskId(response.generateBrokerReportResponse.taskId) // идентификатор задачи формирования отчета
                                .setPage(0) // номер страницы отчета
                                .build()
                        )
                        .build()
                )

                if (report.hasGetBrokerReportResponse()) {
                    logger.info(
                        "Отчет по задаче ${response.generateBrokerReportResponse.taskId} " +
                                "содержит ${report.getBrokerReportResponse.itemsCount} элементов"
                    )
                }
            }

            if (response.hasGetBrokerReportResponse()) {
                logger.info("Отчет содержит ${response.getBrokerReportResponse.itemsCount} элементов")
            }
        }
        channel.shutdown()
    }

    suspend fun exampleGetPortfolio() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceAsync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceAsync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение портфеля по счету
            val response = operationsService.getPortfolio(
                PortfolioRequest.newBuilder()
                    .setAccountId(it.id)  // идентификатор счета клиента
                    .build()
            )

            logger.info("Общая стоимость облигаций в портфеле ${response.totalAmountBonds}")
            logger.info("Общая стоимость фондов в портфеле ${response.totalAmountEtf}")
            logger.info("Общая стоимость валют в портфеле ${response.totalAmountCurrencies}")
            logger.info("Общая стоимость фьючерсов в портфеле ${response.totalAmountFutures}")
            logger.info("Общая стоимость акций в портфеле ${response.totalAmountShares}")
            logger.info("Текущая доходность портфеля ${response.expectedYield}")
            logger.info("В портфеле ${response.positionsCount} позиций. Первые 5 позиций:")
            response.positionsList.take(5).forEach { position ->
                logger.info(
                    "Позиция с figi: ${position.figi}, количество инструмента: ${position.quantity.toBigDecimal()}, " +
                            "текущая цена инструмента: ${position.currentPrice.toBigDecimal()}, " +
                            "текущая расчитанная доходность: ${position.expectedYield.toBigDecimal()}"
                )
            }
        }
        channel.shutdown()
    }

    suspend fun exampleGetOperations() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceAsync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceAsync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение операций по счёту
            val response = operationsService.getOperations(
                OperationsRequest.newBuilder()
                    .setAccountId(it.id)  // идентификатор счета клиента
                    .setFrom(timeFrom) // начало периода
                    .setTo(timeTo) // окончание периода
                    .setState(OperationState.OPERATION_STATE_CANCELED) // фильтр по статусу операции
                    .setFigi("BBG0013HGFT4") // figi Доллара США
                    .build()
            )

            logger.info("Первые 5 операций по счету ${it.id}:")
            response.operationsList.take(5).forEach { operation ->
                logger.info(
                    "Операция с id: ${operation.id}, дата: ${operation.state}, статус: ${operation.state}," +
                            " платёж: ${operation.payment}, figi: ${operation.figi}"
                )
            }
        }
        channel.shutdown()
    }

    suspend fun exampleGetPositions() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceAsync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceAsync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение позиций по счёту
            val response = operationsService.getPositions(
                PositionsRequest.newBuilder()
                    .setAccountId(it.id)  // идентификатор счета клиента
                    .build()
            )

            logger.info("Список валютных позиций портфеля")
            response.moneyList.forEach { money ->
                logger.info("Валюта: ${money.currency}, количество: ${money.toBigDecimal()}")
            }
            logger.info("Список заблокированных валютных позиций портфеля")
            response.blockedList.forEach { money ->
                logger.info("Валюта: ${money.currency}, количество: ${money.toBigDecimal()}")
            }
            logger.info("Список ценно-бумажных позиций портфеля")
            response.securitiesList.forEach { security ->
                logger.info("figi: ${security.figi}, текущий баланс: ${security.balance}, заблокировано: ${security.blocked}")
            }
            logger.info("Список фьючерсов портфеля")
            response.futuresList.forEach { future ->
                logger.info("figi: ${future.figi}, текущий баланс: ${future.balance}, заблокировано: ${future.blocked}")
            }
        }
        channel.shutdown()
    }

    suspend fun exampleGetWithdrawLimits() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceAsync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceAsync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение доступного остатка для вывода средств по счету
            val response = operationsService.getWithdrawLimits(
                WithdrawLimitsRequest.newBuilder()
                    .setAccountId(it.id)  // идентификатор счета клиента
                    .build()
            )

            logger.info("Доступный для вывода остаток для счета $it")
            logger.info("Массив валютных позиций")
            response.moneyList.forEach { money ->
                logger.info("Валюта ${money.currency}, количество: ${money.toBigDecimal()}")
            }
            logger.info("Массив заблокированных валютных позиций портфеля")
            response.moneyList.forEach { money ->
                logger.info("Валюта ${money.currency}, количество: ${money.toBigDecimal()}")
            }
            logger.info("Заблокировано под гарантийное обеспечение фьючерсов")
            response.blockedGuaranteeList.forEach { money ->
                logger.info("Валюта ${money.currency}, количество: ${money.toBigDecimal()}")
            }
        }
        channel.shutdown()
    }

    suspend fun exampleGetDividendsForeignIssuer() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceAsync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceAsync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение отчета «Справка о доходах за пределами РФ» по счету
            val response = operationsService.getDividendsForeignIssuer(
                GetDividendsForeignIssuerRequest.newBuilder()
                    .setGenerateDivForeignIssuerReport(
                        GenerateDividendsForeignIssuerReportRequest.newBuilder()
                            .setAccountId(it.id)  // идентификатор счета клиента
                            .setFrom(timeFrom) // начало периода
                            .setTo(timeTo) // окончание периода
                            .build()
                    )
                    .build()
            )

            if (response.hasGenerateDivForeignIssuerReportResponse()) {
                logger.info("Task id: ${response.generateDivForeignIssuerReportResponse.taskId}")

                // Также можно запросить готовый отчет по его id
                val report = operationsService.getDividendsForeignIssuer(
                    GetDividendsForeignIssuerRequest.newBuilder()
                        .setGetDivForeignIssuerReport(
                            GetDividendsForeignIssuerReportRequest.newBuilder()
                                .setTaskId(response.generateDivForeignIssuerReportResponse.taskId) // идентификатор задачи на получение отчета по счету
                                .build()
                        )
                        .build()
                )

                if (report.hasDivForeignIssuerReport()) {
                    logger.info("Отчет содержит в себе ${response.divForeignIssuerReport.itemsCount} позиций")
                }
            } else if (response.hasDivForeignIssuerReport()) {
                logger.info("Отчет содержит в себе ${response.divForeignIssuerReport.itemsCount} позиций")
            }
        }
        channel.shutdown()
    }

    suspend fun exampleGetOperationsByCursor() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceAsync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            logger.info("Получение последних 5 операций с помощью курсора без фильтрации")
            val totalOperations = getTotalOperationsForAccount(operationsService, it.id)
            totalOperations.takeLast(5).forEach { operation ->
                logger.info(
                    "Операция с id: ${operation.id}, дата: ${operation.date}, статус: ${operation.state}, " +
                            "платёж: ${operation.payment.toBigDecimal()}, figi: ${operation.figi}"
                )
            }

            logger.info("Получение последних 5 операций с помощью курсора с фильтрацией")
            val totalOperationsWithFilter = getTotalOperationsForAccountWithFilter(operationsService, it.id)
            totalOperationsWithFilter.takeLast(5).forEach { operation ->
                logger.info(
                    "Операция с id: ${operation.id}, дата: ${operation.date}, статус: ${operation.state}, " +
                            "платёж: ${operation.payment.toBigDecimal()}, figi: ${operation.figi}"
                )
            }
        }
        channel.shutdown()
    }

    private suspend fun getTotalOperationsForAccount(
        operationsService: OperationsServiceAsync,
        accountId: String
    ): List<OperationItem> {
        var cursor = "" // Начальное значение курсора
        val totalOperations = mutableListOf<OperationItem>()
        // в цикле получаем постранично все операции
        while (true) {
            // запрос на получение операций по счёту с пагинацией
            val response = operationsService.getOperationsByCursor(
                GetOperationsByCursorRequest.newBuilder()
                    .setAccountId(accountId)  // идентификатор счёта клиента
                    .setCursor(cursor) // курсор для указания текущей страницы
                    .setFrom(timeFrom) // начало периода
                    .setTo(timeTo) // окончание периода
                    .build()
            )
            totalOperations.addAll(response.itemsList)
            cursor = response.nextCursor
            if (!response.hasNext) {
                break
            }
        }
        return totalOperations
    }

    private suspend fun getTotalOperationsForAccountWithFilter(
        operationsService: OperationsServiceAsync,
        accountId: String
    ): List<OperationItem> {
        var cursor = "" // Начальное значение курсора
        val totalOperations = mutableListOf<OperationItem>()
        // в цикле получаем постранично все операции, удовлетворяющие фильтру
        while (true) {
            // запрос на получение операций по счёту с пагинацией и фильтрацией
            val response = operationsService.getOperationsByCursor(
                GetOperationsByCursorRequest.newBuilder()
                    .setAccountId(accountId) // идентификатор счёта клиента
                    .setInstrumentId("BBG0013HGFT4") // figi Доллара США
                    .setFrom(timeFrom) // начало периода
                    .setTo(timeTo) // окончание периода
                    .setCursor(cursor) // курсор для указания текущей страницы
                    .setLimit(200) // лимит количества операций
                    .addOperationTypes(OperationType.OPERATION_TYPE_TAX_CORRECTION) // типы операций
                    .addOperationTypes(OperationType.OPERATION_TYPE_BUY_MARGIN)
                    .setState(OperationState.OPERATION_STATE_CANCELED) // статус запрашиваемых операций
                    .setWithoutCommissions(true) // флаг возврата комиссии
                    .setWithoutTrades(true) // флаг получения ответа без массива сделок
                    .setWithoutOvernights(true) // флаг показа овернайт операций
                    .build()
            )
            totalOperations.addAll(response.itemsList)
            cursor = response.nextCursor
            if (!response.hasNext) {
                break
            }
        }
        return totalOperations
    }
}