package ru.tinvest.piapi.example

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class OrdersServiceExampleTest {

    private val syncExample = OrdersServiceSyncExample()
    private val asyncExample = OrdersServiceAsyncExample()

    @Test
    @DisplayName("Sync request to LimitOrder")
    fun testLimitOrderSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleLimitOrder()
        }
    }

    @Test
    @DisplayName("Async request to LimitOrder")
    fun testLimitOrderAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleLimitOrder()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to MarketOrder")
    fun testMarketOrderSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleMarketOrder()
        }
    }

    @Test
    @DisplayName("Async request to MarketOrder")
    fun testMarketOrderAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleMarketOrder()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to GetOrders")
    fun testGetOrdersSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetOrders()
        }
    }

    @Test
    @DisplayName("Async request to GetOrders")
    fun testGetOrdersAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetOrders()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to GetMaxLots")
    fun testGetMaxLotsSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetMaxLots()
        }
    }

    @Test
    @DisplayName("Async request to GetMaxLots")
    fun testGetMaxLotsAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetMaxLots()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to GetOrderPrice")
    fun testGetOrderPriceSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetOrderPrice()
        }
    }

    @Test
    @DisplayName("Async request to GetOrderPrice")
    fun testGetOrderPriceAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetOrderPrice()
                }.await()
            }
        }
    }
}