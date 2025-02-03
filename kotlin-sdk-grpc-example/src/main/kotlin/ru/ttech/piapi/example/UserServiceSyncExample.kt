package ru.ttech.piapi.example

import ru.tinkoff.piapi.contract.v1.Account
import ru.tinkoff.piapi.contract.v1.GetAccountsRequest
import ru.ttech.piapi.core.InvestApi

class UserServiceSyncExample {

    fun exampleUserInfo(): List<Account> {
        //создаем channel со свойствами по умолчанию
        val channel = InvestApi.defaultChannel(
            token = System.getProperty("token"),
            target = System.getProperty("target")
        )
        //создаем объект API
        val investApi = InvestApi.createApi(channel)
        val usersService = investApi.usersServiceSync

        // запрашиваем все аккаунты
        val accounts = usersService.getAccounts(GetAccountsRequest.getDefaultInstance())
        channel.shutdown()
        //Возвращаем список аккаунтов и одновременно выводим в консоль
        return accounts.accountsList.also { println(it) }
    }

    fun exampleUnauthenticatedAccess() {
        //создаем channel с некорректным токеном
        val channel = InvestApi.defaultChannel(
            token = "invalid_token",
            target = System.getProperty("target")
        )

        //создаем объект API
        val investApi = InvestApi.createApi(channel)
        val usersService = investApi.usersServiceSync
        // запрашиваем аккаунты для получения ошибки
        try {
            usersService.getAccounts(GetAccountsRequest.getDefaultInstance())
        } finally {
            channel.shutdown()
        }
    }

}