package ru.tinvest.piapi.example

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class OperationsServiceExampleTest {

    private val syncExample = OperationsServiceSyncExample()
    private val asyncExample = OperationsServiceAsyncExample()

    @Test
    @DisplayName("Sync request to GetBrokerReport")
    fun testGetBrokerReportSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetBrokerReport()
        }
    }

    @Test
    @DisplayName("Async request to GetBrokerReport")
    fun testGetBrokerReportAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetBrokerReport()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to GetPortfolio")
    fun testGetPortfolioSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetPortfolio()
        }
    }

    @Test
    @DisplayName("Async request to GetPortfolio")
    fun testGetPortfolioAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetPortfolio()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to GetOperations")
    fun testGetOperationsSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetOperations()
        }
    }

    @Test
    @DisplayName("Async request to GetOperations")
    fun testGetOperationsAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetOperations()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to GetPositions")
    fun testGetPositionsSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetPositions()
        }
    }

    @Test
    @DisplayName("Async request to GetPositions")
    fun testGetPositionsAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetPositions()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to GetWithdrawLimits")
    fun testGetWithdrawLimitsSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetWithdrawLimits()
        }
    }

    @Test
    @DisplayName("Async request to GetWithdrawLimits")
    fun testGetWithdrawLimitsAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetWithdrawLimits()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to GetDividendsForeignIssuer")
    fun testGetDividendsForeignIssuerSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetDividendsForeignIssuer()
        }
    }

    @Test
    @DisplayName("Async request to GetDividendsForeignIssuer")
    fun testGetDividendsForeignIssuerAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetDividendsForeignIssuer()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to GetOperationsByCursor")
    fun testGetOperationsByCursorSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleGetOperationsByCursor()
        }
    }

    @Test
    @DisplayName("Async request to GetOperationsByCursor")
    fun testGetOperationsByCursorAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleGetOperationsByCursor()
                }.await()
            }
        }
    }
}
