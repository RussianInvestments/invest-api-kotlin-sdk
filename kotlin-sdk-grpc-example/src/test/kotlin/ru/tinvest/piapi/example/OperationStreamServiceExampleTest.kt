package ru.tinvest.piapi.example

import io.grpc.ManagedChannel
import org.junit.jupiter.api.*
import ru.tinvest.piapi.core.InvestApi

class OperationStreamServiceExampleTest {

    private lateinit var channel: ManagedChannel
    private var streamExample = OperationStreamServiceExample()

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
    @DisplayName("Stream subscribe to PortfolioStream")
    fun testPortfolioStream() {
        Assertions.assertDoesNotThrow {
            streamExample.examplePortfolioStream(channel)
        }
    }

    @Test
    @DisplayName("Stream subscribe to PositionsStream")
    fun testPositionsStream() {
        Assertions.assertDoesNotThrow {
            streamExample.examplePositionsStream(channel)
        }
    }
}