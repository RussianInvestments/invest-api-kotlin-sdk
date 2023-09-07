package ru.tinkoff.piapi.core

import kotlinx.coroutines.runBlocking
import ru.tinkoff.piapi.contract.v1.GetAccountsRequest
import ru.tinkoff.piapi.contract.v1.GetAccountsResponse
import ru.tinkoff.piapi.contract.v1.GetInfoRequest
import ru.tinkoff.piapi.contract.v1.GetInfoResponse
import ru.tinkoff.piapi.contract.v1.GetMarginAttributesRequest
import ru.tinkoff.piapi.contract.v1.GetMarginAttributesResponse
import ru.tinkoff.piapi.contract.v1.GetUserTariffRequest
import ru.tinkoff.piapi.contract.v1.GetUserTariffResponse
import ru.tinkoff.piapi.contract.v1.UsersServiceGrpcKt

data class UsersService(private val coroutineStub: UsersServiceGrpcKt.UsersServiceCoroutineStub) {

    suspend fun getAccounts(request: GetAccountsRequest):
            GetAccountsResponse = coroutineStub.getAccounts(request)

    fun getAccountsSync(request: GetAccountsRequest):
            GetAccountsResponse = runBlocking { getAccounts(request) }

    suspend fun getMarginAttributes(request: GetMarginAttributesRequest):
            GetMarginAttributesResponse = coroutineStub.getMarginAttributes(request)

    fun getMarginAttributesSync(request: GetMarginAttributesRequest):
            GetMarginAttributesResponse = runBlocking { getMarginAttributes(request) }

    suspend fun getUserTariff(request: GetUserTariffRequest):
            GetUserTariffResponse = coroutineStub.getUserTariff(request)

    fun getUserTariffSync(request: GetUserTariffRequest):
            GetUserTariffResponse = runBlocking { getUserTariff(request) }

    suspend fun getInfo(request: GetInfoRequest):
            GetInfoResponse = coroutineStub.getInfo(request)

    fun getInfoSync(request: GetInfoRequest):
            GetInfoResponse = runBlocking { getInfo(request) }
}
