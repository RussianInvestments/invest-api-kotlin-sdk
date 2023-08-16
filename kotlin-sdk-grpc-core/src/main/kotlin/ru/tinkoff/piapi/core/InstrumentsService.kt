package ru.tinkoff.piapi.core

import kotlinx.coroutines.runBlocking
import ru.tinkoff.piapi.contract.v1.AssetRequest
import ru.tinkoff.piapi.contract.v1.AssetResponse
import ru.tinkoff.piapi.contract.v1.AssetsRequest
import ru.tinkoff.piapi.contract.v1.AssetsResponse
import ru.tinkoff.piapi.contract.v1.BondResponse
import ru.tinkoff.piapi.contract.v1.BondsResponse
import ru.tinkoff.piapi.contract.v1.Brand
import ru.tinkoff.piapi.contract.v1.CurrenciesResponse
import ru.tinkoff.piapi.contract.v1.CurrencyResponse
import ru.tinkoff.piapi.contract.v1.EditFavoritesRequest
import ru.tinkoff.piapi.contract.v1.EditFavoritesResponse
import ru.tinkoff.piapi.contract.v1.EtfResponse
import ru.tinkoff.piapi.contract.v1.EtfsResponse
import ru.tinkoff.piapi.contract.v1.FilterOptionsRequest
import ru.tinkoff.piapi.contract.v1.FindInstrumentRequest
import ru.tinkoff.piapi.contract.v1.FindInstrumentResponse
import ru.tinkoff.piapi.contract.v1.FutureResponse
import ru.tinkoff.piapi.contract.v1.FuturesResponse
import ru.tinkoff.piapi.contract.v1.GetAccruedInterestsRequest
import ru.tinkoff.piapi.contract.v1.GetAccruedInterestsResponse
import ru.tinkoff.piapi.contract.v1.GetAssetFundamentalsRequest
import ru.tinkoff.piapi.contract.v1.GetAssetFundamentalsResponse
import ru.tinkoff.piapi.contract.v1.GetBondCouponsRequest
import ru.tinkoff.piapi.contract.v1.GetBondCouponsResponse
import ru.tinkoff.piapi.contract.v1.GetBrandRequest
import ru.tinkoff.piapi.contract.v1.GetBrandsRequest
import ru.tinkoff.piapi.contract.v1.GetBrandsResponse
import ru.tinkoff.piapi.contract.v1.GetCountriesRequest
import ru.tinkoff.piapi.contract.v1.GetCountriesResponse
import ru.tinkoff.piapi.contract.v1.GetDividendsRequest
import ru.tinkoff.piapi.contract.v1.GetDividendsResponse
import ru.tinkoff.piapi.contract.v1.GetFavoritesRequest
import ru.tinkoff.piapi.contract.v1.GetFavoritesResponse
import ru.tinkoff.piapi.contract.v1.GetFuturesMarginRequest
import ru.tinkoff.piapi.contract.v1.GetFuturesMarginResponse
import ru.tinkoff.piapi.contract.v1.InstrumentRequest
import ru.tinkoff.piapi.contract.v1.InstrumentResponse
import ru.tinkoff.piapi.contract.v1.InstrumentsRequest
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.OptionResponse
import ru.tinkoff.piapi.contract.v1.OptionsResponse
import ru.tinkoff.piapi.contract.v1.ShareResponse
import ru.tinkoff.piapi.contract.v1.SharesResponse
import ru.tinkoff.piapi.contract.v1.TradingSchedulesRequest
import ru.tinkoff.piapi.contract.v1.TradingSchedulesResponse

data class InstrumentsService(private val coroutineStub: InstrumentsServiceGrpcKt.InstrumentsServiceCoroutineStub) {
    suspend fun tradingSchedules(
        request: TradingSchedulesRequest
    ): TradingSchedulesResponse = coroutineStub.tradingSchedules(request)

    fun tradingSchedulesSync(
        request: TradingSchedulesRequest
    ): TradingSchedulesResponse = runBlocking { tradingSchedules(request) }

    suspend fun bondBy(request: InstrumentRequest): BondResponse = coroutineStub.bondBy(request)

    fun bondBySync(request: InstrumentRequest): BondResponse = runBlocking { bondBy(request) }

    suspend fun bonds(request: InstrumentsRequest): BondsResponse = coroutineStub.bonds(request)

    fun bondsSync(request: InstrumentsRequest): BondsResponse = runBlocking { bonds(request) }

    suspend fun getBondCoupons(
        request: GetBondCouponsRequest
    ): GetBondCouponsResponse = coroutineStub.getBondCoupons(request)

    fun getBondCouponsSync(
        request: GetBondCouponsRequest
    ): GetBondCouponsResponse = runBlocking { getBondCoupons(request) }

    suspend fun currencyBy(request: InstrumentRequest): CurrencyResponse = coroutineStub.currencyBy(request)

    fun currencyBySync(request: InstrumentRequest): CurrencyResponse = runBlocking { currencyBy(request) }

    suspend fun currencies(request: InstrumentsRequest): CurrenciesResponse = coroutineStub.currencies(request)

    fun currenciesSync(request: InstrumentsRequest): CurrenciesResponse = runBlocking { currencies(request) }

    suspend fun etfBy(request: InstrumentRequest): EtfResponse = coroutineStub.etfBy(request)

    fun etfBySync(request: InstrumentRequest): EtfResponse = runBlocking { etfBy(request) }

    suspend fun etfs(request: InstrumentsRequest): EtfsResponse = coroutineStub.etfs(request)

    fun etfsSync(request: InstrumentsRequest): EtfsResponse = runBlocking { etfs(request) }

    suspend fun futureBy(request: InstrumentRequest): FutureResponse = coroutineStub.futureBy(request)

    fun futureBySync(request: InstrumentRequest): FutureResponse = runBlocking { futureBy(request) }

    suspend fun futures(request: InstrumentsRequest): FuturesResponse = coroutineStub.futures(request)

    fun futuresSync(request: InstrumentsRequest): FuturesResponse = runBlocking { futures(request) }

    suspend fun optionBy(request: InstrumentRequest): OptionResponse = coroutineStub.optionBy(request)

    fun optionBySync(request: InstrumentRequest): OptionResponse = runBlocking { optionBy(request) }

    suspend fun optionsBy(request: FilterOptionsRequest): OptionsResponse = coroutineStub.optionsBy(request)

    fun optionsBySync(request: FilterOptionsRequest): OptionsResponse = runBlocking { optionsBy(request) }

    suspend fun shareBy(request: InstrumentRequest): ShareResponse = coroutineStub.shareBy(request)

    fun shareBySync(request: InstrumentRequest): ShareResponse = runBlocking { shareBy(request) }

    suspend fun shares(request: InstrumentsRequest): SharesResponse = coroutineStub.shares(request)

    fun sharesSync(request: InstrumentsRequest): SharesResponse = runBlocking { shares(request) }

    suspend fun getAccruedInterests(
        request: GetAccruedInterestsRequest
    ): GetAccruedInterestsResponse = coroutineStub.getAccruedInterests(request)

    fun getAccruedInterestsSync(
        request: GetAccruedInterestsRequest
    ): GetAccruedInterestsResponse = runBlocking { getAccruedInterests(request) }

    suspend fun getFuturesMargin(
        request: GetFuturesMarginRequest
    ): GetFuturesMarginResponse = coroutineStub.getFuturesMargin(request)

    fun getFuturesMarginSync(
        request: GetFuturesMarginRequest
    ): GetFuturesMarginResponse = runBlocking { getFuturesMargin(request) }

    suspend fun getInstrumentBy(request: InstrumentRequest): InstrumentResponse = coroutineStub.getInstrumentBy(request)

    fun getInstrumentBySync(request: InstrumentRequest): InstrumentResponse = runBlocking { getInstrumentBy(request) }

    suspend fun getDividends(request: GetDividendsRequest): GetDividendsResponse = coroutineStub.getDividends(request)

    fun getDividendsSync(request: GetDividendsRequest): GetDividendsResponse = runBlocking { getDividends(request) }

    suspend fun getAssetBy(request: AssetRequest): AssetResponse = coroutineStub.getAssetBy(request)

    fun getAssetBySync(request: AssetRequest): AssetResponse = runBlocking { getAssetBy(request) }

    suspend fun getAssets(request: AssetsRequest): AssetsResponse = coroutineStub.getAssets(request)

    fun getAssetsSync(request: AssetsRequest): AssetsResponse = runBlocking { getAssets(request) }

    suspend fun getFavorites(request: GetFavoritesRequest): GetFavoritesResponse = coroutineStub.getFavorites(request)

    fun getFavoritesSync(request: GetFavoritesRequest): GetFavoritesResponse = runBlocking { getFavorites(request) }

    suspend fun editFavorites(request: EditFavoritesRequest): EditFavoritesResponse =
        coroutineStub.editFavorites(request)

    fun editFavoritesSync(request: EditFavoritesRequest): EditFavoritesResponse = runBlocking { editFavorites(request) }

    suspend fun getCountries(request: GetCountriesRequest): GetCountriesResponse = coroutineStub.getCountries(request)

    fun getCountriesSync(request: GetCountriesRequest): GetCountriesResponse = runBlocking { getCountries(request) }

    suspend fun findInstrument(
        request: FindInstrumentRequest
    ): FindInstrumentResponse = coroutineStub.findInstrument(request)

    fun findInstrumentSync(
        request: FindInstrumentRequest
    ): FindInstrumentResponse = runBlocking { findInstrument(request) }

    suspend fun getBrands(request: GetBrandsRequest): GetBrandsResponse = coroutineStub.getBrands(request)

    fun getBrandsSync(request: GetBrandsRequest): GetBrandsResponse = runBlocking { getBrands(request) }

    suspend fun getBrandBy(request: GetBrandRequest): Brand = coroutineStub.getBrandBy(request)

    fun getBrandBySync(request: GetBrandRequest): Brand = runBlocking { getBrandBy(request) }
    suspend fun getAssetFundamentals(
        request: GetAssetFundamentalsRequest
    ): GetAssetFundamentalsResponse = coroutineStub.getAssetFundamentals(request)

    fun getAssetFundamentalsSync(
        request: GetAssetFundamentalsRequest
    ): GetAssetFundamentalsResponse = runBlocking { getAssetFundamentals(request) }
}
