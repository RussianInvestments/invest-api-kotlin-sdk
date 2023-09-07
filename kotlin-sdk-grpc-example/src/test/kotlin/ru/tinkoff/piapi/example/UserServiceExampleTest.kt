package ru.tinkoff.piapi.example

import io.grpc.StatusException
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.tinkoff.piapi.contract.v1.GetAccountsRequest
import ru.tinkoff.piapi.core.InvestApi

class UserServiceExampleTest {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserServiceExampleTest::class.java)
    }

    @Test
    @DisplayName("Асинхронное обращение к user service")
    fun testUserInfo() {
        //создаем channel со свойствами по умолчанию
        val channel = InvestApi.defaultChannel(
            token = System.getProperty("token"),
            target = System.getProperty("target")
        )
        //создаем объект API
        val investApi = InvestApi.createApi(channel)
        val usersService = investApi.usersService
        Assertions.assertDoesNotThrow {
            // запрашиваем и выводим все аккаунты в консоль
            runBlocking {
                val accounts = async { usersService.getAccounts(GetAccountsRequest.getDefaultInstance()) }
                accounts.await().accountsList.forEach { logger.info("$it") }
            }
        }
        channel.shutdown()
    }

    @Test
    @DisplayName("Синхронное обращение к user service")
    fun testUserInfoSync() {
        //создаем channel со свойствами по умолчанию
        val channel = InvestApi.defaultChannel(
            token = System.getProperty("token"),
            target = System.getProperty("target")
        )
        //создаем объект API
        val investApi = InvestApi.createApi(channel)
        val usersService = investApi.usersService
        Assertions.assertDoesNotThrow {
            // запрашиваем все аккаунты
            val accounts = usersService.getAccountsSync(GetAccountsRequest.getDefaultInstance())
            // выводим полученный результат в output
            accounts.accountsList.forEach { logger.info("$it") }
        }
        channel.shutdown()
    }

    @Test
    @DisplayName("Получение ошибки unauthenticated")
    fun testAuthenticated() {
        //создаем channel с некорректным токеном
        val channel = InvestApi.defaultChannel(
            token = "invalid_token",
            target = System.getProperty("target")
        )
        //создаем объект API
        val investApi = InvestApi.createApi(channel)
        val usersService = investApi.usersService
        // запрашиваем аккаунты для получения ошибки
        Assertions.assertThrows(StatusException::class.java)
        {
            usersService.getAccountsSync(GetAccountsRequest.getDefaultInstance())
        }
        channel.shutdown()
    }

}