package ru.tinvest.piapi.example

import io.grpc.ManagedChannel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import ru.tinkoff.piapi.contract.v1.GetAccountsRequest
import ru.tinkoff.piapi.contract.v1.PortfolioStreamRequest
import ru.tinkoff.piapi.contract.v1.PositionsStreamRequest
import ru.tinvest.piapi.core.InvestApi

class OperationStreamServiceExample {

    fun examplePortfolioStream(channel: ManagedChannel) {
        val investApi = InvestApi.createApi(channel)
        val operationStreamService = investApi.operationsStreamServiceAsync
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())

        // собираем запрос на подписку изменений портфолио
        val request = PortfolioStreamRequest.newBuilder()
            .addAllAccounts(accounts.accountsList.map { account -> account.id }) // добавялем id всех счетов
            .build()

        runBlocking {
            val job = async {
                operationStreamService.portfolioStream(
                    request,
                    // Две callback функции (количество не ограничено), для обработки сообщений.
                    // Первая выводит в консоль сообщение о получении сообщения типа PortfolioStreamResponse
                    // Вторая функция выводит в консоль полученное сообщение вне зависимости от типа
                    { if (it.hasSubscriptions()) println("Состояние подписки обновилось: ${it.subscriptions}") },
                    { message -> println("Получено обновление: $message") }
                )
            }
            //через 3 секунды закрываем соединение
            delay(3000)
            job.cancel()
        }
    }

    fun examplePositionsStream(channel: ManagedChannel) {
        val investApi = InvestApi.createApi(channel)
        val operationStreamService = investApi.operationsStreamServiceAsync
        val accounts = investApi.usersServiceSync.getAccounts(GetAccountsRequest.getDefaultInstance())

        // собираем запрос на подписку информации о позициях
        val request = PositionsStreamRequest.newBuilder()
            .addAllAccounts(accounts.accountsList.map { account -> account.id }) // добавялем id всех счетов
            .build()

        runBlocking {
            val job = async {
                operationStreamService.positionsStream(
                    request,
                    // Две callback функции (количество не ограничено), для обработки сообщений.
                    // Первая выводит в консоль сообщение о получении сообщения типа PositionsStreamResponse
                    // Вторая функция выводит в консоль полученное сообщение вне зависимости от типа
                    { if (it.hasSubscriptions()) println("Состояние подписки обновилось: ${it.subscriptions}") },
                    { message -> println("Получено обновление: $message") }
                )
            }
            //через 3 секунды закрываем соединение
            delay(3000)
            job.cancel()
        }
    }
}