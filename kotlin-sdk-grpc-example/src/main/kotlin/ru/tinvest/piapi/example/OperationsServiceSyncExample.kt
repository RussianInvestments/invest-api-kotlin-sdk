package ru.tinvest.piapi.example

import io.grpc.ManagedChannel
import ru.tinkoff.piapi.contract.v1.*
import ru.tinvest.piapi.core.InvestApi
import ru.tinvest.piapi.core.utils.toBigDecimal
import ru.tinvest.piapi.core.utils.toTimestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

@Suppress("DuplicatedCode")
class OperationsServiceSyncExample {

    companion object {
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

    fun exampleGetBrokerReport() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceSync

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
                println("Task id: ${response.generateBrokerReportResponse.taskId}")

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
                    println(
                        "Отчет по задаче ${response.generateBrokerReportResponse.taskId} " +
                                "содержит ${report.getBrokerReportResponse.itemsCount} элементов"
                    )
                }
            }

            if (response.hasGetBrokerReportResponse()) {
                println("Отчет содержит ${response.getBrokerReportResponse.itemsCount} элементов")
            }
        }
        channel.shutdown()
    }

    fun exampleGetPortfolio() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceSync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение портфеля по счету
            val response = operationsService.getPortfolio(
                PortfolioRequest.newBuilder()
                    .setAccountId(it.id)  // идентификатор счета клиента
                    .build()
            )

            println("Общая стоимость облигаций в портфеле ${response.totalAmountBonds}")
            println("Общая стоимость фондов в портфеле ${response.totalAmountEtf}")
            println("Общая стоимость валют в портфеле ${response.totalAmountCurrencies}")
            println("Общая стоимость фьючерсов в портфеле ${response.totalAmountFutures}")
            println("Общая стоимость акций в портфеле ${response.totalAmountShares}")
            println("Текущая доходность портфеля ${response.expectedYield}")
            println("В портфеле ${response.positionsCount} позиций")
            response.positionsList.take(5).forEach { position ->
                println(
                    "Позиция с figi: ${position.figi}, количество инструмента: ${position.quantity.toBigDecimal()}, " +
                            "текущая цена инструмента: ${position.currentPrice.toBigDecimal()}, " +
                            "текущая расчитанная доходность: ${position.expectedYield.toBigDecimal()}"
                )
            }
        }
        channel.shutdown()
    }

    fun exampleGetOperations() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceSync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение операций по счёту
            val response = operationsService.getOperations(
                OperationsRequest.newBuilder()
                    .setAccountId(it.id)  // идентификатор счета клиента
                    .setFrom(timeFrom) // начало периода
                    .setTo(timeTo) // окончание периода
                    .setState(OperationState.OPERATION_STATE_UNSPECIFIED) // фильтр по статусу операции
                    .setFigi("BBG0013HGFT4") // figi Доллара США
                    .build()
            )

            response.operationsList.take(5).forEach { operation ->
                println(
                    "Операция с id: ${operation.id}, дата: ${operation.state}, статус: ${operation.state}," +
                            " платёж: ${operation.payment}, figi: ${operation.figi}"
                )
            }
        }
        channel.shutdown()
    }

    fun exampleGetPositions() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceSync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение позиций по счёту
            val response = operationsService.getPositions(
                PositionsRequest.newBuilder()
                    .setAccountId(it.id)  // идентификатор счета клиента
                    .build()
            )

            println("Список валютных позиций портфеля")
            response.moneyList.forEach { money ->
                println("Валюта: ${money.currency}, количество: ${money.toBigDecimal()}")
            }
            println("Список заблокированных валютных позиций портфеля")
            response.blockedList.forEach { money ->
                println("Валюта: ${money.currency}, количество: ${money.toBigDecimal()}")
            }
            println("Список ценно-бумажных позиций портфеля")
            response.securitiesList.forEach { security ->
                println("figi: ${security.figi}, текущий баланс: ${security.balance}, заблокировано: ${security.blocked}")
            }
            println("Список фьючерсов портфеля")
            response.futuresList.forEach { future ->
                println("figi: ${future.figi}, текущий баланс: ${future.balance}, заблокировано: ${future.blocked}")
            }
        }
        channel.shutdown()
    }

    fun exampleGetWithdrawLimits() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceSync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            // запрос на получение доступного остатка для вывода средств по счету
            val response = operationsService.getWithdrawLimits(
                WithdrawLimitsRequest.newBuilder()
                    .setAccountId(it.id)  // идентификатор счета клиента
                    .build()
            )

            println("Доступный для вывода остаток для счета $it")
            println("Массив валютных позиций")
            response.moneyList.forEach { money ->
                println("Валюта ${money.currency}, количество: ${money.toBigDecimal()}")
            }
            println("Массив заблокированных валютных позиций портфеля")
            response.moneyList.forEach { money ->
                println("Валюта ${money.currency}, количество: ${money.toBigDecimal()}")
            }
            println("Заблокировано под гарантийное обеспечение фьючерсов")
            response.blockedGuaranteeList.forEach { money ->
                println("Валюта ${money.currency}, количество: ${money.toBigDecimal()}")
            }
        }
        channel.shutdown()
    }

    fun exampleGetDividendsForeignIssuer() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceSync

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
                println("Task id: ${response.generateDivForeignIssuerReportResponse.taskId}")

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
                    println("Отчет содержит в себе ${response.divForeignIssuerReport.itemsCount} позиций")
                }
            } else if (response.hasDivForeignIssuerReport()) {
                println("Отчет содержит в себе ${response.divForeignIssuerReport.itemsCount} позиций")
            }
        }
        channel.shutdown()
    }

    fun exampleGetOperationsByCursor() {
        val (investApi, channel) = createInvestApi()
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())
        val operationsService = investApi.operationsServiceSync

        // Получаем список аккаунтов и берём первый из списка
        accounts.accountsList.take(1).forEach {
            println("Получение операций с помощью курсора без фильтрации")
            // запрос на получение операций по счёту с пагинацией
            val response = operationsService.getOperationsByCursor(
                GetOperationsByCursorRequest.newBuilder()
                    .setAccountId(it.id)  // идентификатор счёта клиента
                    .setFrom(timeFrom) // начало периода
                    .setTo(timeTo) // окончание периода
                    .build()
            )
            response.itemsList.take(5).forEach { operation ->
                println("Операция с id: ${operation.id}, дата: ${operation.date}, статус: ${operation.state}, " +
                        "платёж: ${operation.payment.toBigDecimal()}, figi: ${operation.figi}")
            }

            println("Получение операций с помощью курсора с фильтрацией")
            val responseWithFilters = operationsService.getOperationsByCursor(
                GetOperationsByCursorRequest.newBuilder()
                    .setAccountId(it.id) // идентификатор счёта клиента
                    .setInstrumentId("BBG0013HGFT4") // figi Доллара США
                    .setFrom(timeFrom) // начало периода
                    .setTo(timeTo) // окончание периода
                    .setLimit(200) // лимит количества операций
                    .addOperationTypes(OperationType.OPERATION_TYPE_TAX_CORRECTION) // типы операций
                    .addOperationTypes(OperationType.OPERATION_TYPE_BUY_MARGIN)
                    .setState(OperationState.OPERATION_STATE_CANCELED) // статус запрашиваемых операций
                    .setWithoutCommissions(true) // флаг возврата комиссии
                    .setWithoutTrades(true) // флаг получения ответа без массива сделок
                    .setWithoutOvernights(true) // флаг показа овернайт операций
                    .build()
            )
            responseWithFilters.itemsList.take(5).forEach { operation ->
                println("Операция с id: ${operation.id}, дата: ${operation.date}, статус: ${operation.state}, " +
                        "платёж: ${operation.payment.toBigDecimal()}, figi: ${operation.figi}")
            }
        }

        channel.shutdown()
    }
}