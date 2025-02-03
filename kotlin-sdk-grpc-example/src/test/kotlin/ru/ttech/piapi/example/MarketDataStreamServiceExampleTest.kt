package ru.ttech.piapi.example

import io.grpc.ManagedChannel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import ru.ttech.piapi.core.InvestApi

class MarketDataStreamServiceExampleTest {

    private lateinit var channel: ManagedChannel
    private var marketDataStreamServiceExample: MarketDataStreamServiceExample = MarketDataStreamServiceExample()

    @BeforeEach
    fun tearUp() {
        channel = InvestApi.defaultChannel(
            token = System.getProperty("token"),
            target = System.getProperty("target")
        )
    }

    @AfterEach
    fun tearDown() {
        channel.shutdown()
    }

    @Test
    @DisplayName("Работа с server-side stream")
    fun marketDataServerSideStream() {
        Assertions.assertDoesNotThrow {
            marketDataStreamServiceExample.marketDataServerSideStream(channel)
        }

    }

    @Test
    @DisplayName("Работа с двунаправленным стримом")
    fun marketDataStream() {
        Assertions.assertDoesNotThrow {
            marketDataStreamServiceExample.biDirectionalStreamExample(channel)
        }
    }


}