package ru.ttech.piapi.example

import ru.tinkoff.piapi.contract.v1.GetAccountsRequest
import ru.ttech.piapi.core.InvestApi

class UserServiceAsyncExample {

    suspend fun exampleUserInfo() {
        //создаем channel со свойствами по умолчанию
        val channel = InvestApi.defaultChannel(
            token = System.getProperty("token"),
            target = System.getProperty("target")
        )
        //создаем объект API
        val investApi = InvestApi.createApi(channel)
        val usersService = investApi.usersServiceAsync

        // запрашиваем все аккаунты
        val accounts = usersService.getAccounts(GetAccountsRequest.getDefaultInstance())

        // выводим полученный результат в output при получении
        accounts.accountsList.forEach { println("$it") }
        channel.shutdown()

    }

}