package ru.ttech.piapi.example

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*

class TechAnalysisExampleTest {

    private val syncExample = TechAnalysisSyncExample()
    private val asyncExample = TechAnalysisAsyncExample()

    @Test
    @DisplayName("Sync request to RSI indicator")
    fun testTechAnalysisRSISync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleTechAnalysisRSI()
        }
    }

    @Test
    @DisplayName("Async request to RSI indicator")
    fun testTechAnalysisRSIAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleTechAnalysisRSI()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to EMA indicator")
    fun testTechAnalysisEMASync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleTechAnalysisEMA()
        }
    }

    @Test
    @DisplayName("Async request to EMA indicator")
    fun testTechAnalysisEMAAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleTechAnalysisEMA()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to SMA indicator")
    fun testTechAnalysisSMASync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleTechAnalysisSMA()
        }
    }

    @Test
    @DisplayName("Async request to SMA indicator")
    fun testTechAnalysisSMAAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleTechAnalysisSMA()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to MACD indicator")
    fun testTechAnalysisMACDSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleTechAnalysisMACD()
        }
    }

    @Test
    @DisplayName("Async request to MACD indicator")
    fun testTechAnalysisMACDAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleTechAnalysisMACD()
                }.await()
            }
        }
    }

    @Test
    @DisplayName("Sync request to Bollinger Bands indicator")
    fun testTechAnalysisBBSync() {
        Assertions.assertDoesNotThrow {
            syncExample.exampleTechAnalysisBB()
        }
    }

    @Test
    @DisplayName("Async request to Bollinger Bands indicator")
    fun testTechAnalysisBBAsync() {
        Assertions.assertDoesNotThrow {
            runBlocking {
                async {
                    asyncExample.exampleTechAnalysisBB()
                }.await()
            }
        }
    }
}
