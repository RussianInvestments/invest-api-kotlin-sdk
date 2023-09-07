package ru.tinkoff.piapi.core

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.netty.NettyChannelBuilder
import io.grpc.stub.MetadataUtils
import io.netty.channel.ChannelOption
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.MarketDataStreamServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.OperationsServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.OperationsStreamServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.OrdersServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.OrdersStreamServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.SandboxServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.StopOrdersServiceGrpcKt
import ru.tinkoff.piapi.contract.v1.UsersServiceGrpcKt
import java.time.Duration
import java.util.concurrent.TimeUnit

class InvestApi private constructor(channel: Channel, val readonlyMode: Boolean = true, val sandboxMode: Boolean = false) {
    val usersService = UsersService(UsersServiceGrpcKt.UsersServiceCoroutineStub(channel))
    val operationsService = OperationsService(OperationsServiceGrpcKt.OperationsServiceCoroutineStub(channel))
    val instrumentsService = InstrumentsService(InstrumentsServiceGrpcKt.InstrumentsServiceCoroutineStub(channel))
    val ordersService = OrdersService(OrdersServiceGrpcKt.OrdersServiceCoroutineStub(channel))
    val stopOrdersService = StopOrdersService(StopOrdersServiceGrpcKt.StopOrdersServiceCoroutineStub(channel))
    val marketDataService = MarketDataService(MarketDataServiceGrpcKt.MarketDataServiceCoroutineStub(channel))
    val ordersStreamService = OrdersStreamService(OrdersStreamServiceGrpcKt.OrdersStreamServiceCoroutineStub(channel))
    val marketDataStreamService = MarketDataStreamService(MarketDataStreamServiceGrpcKt.MarketDataStreamServiceCoroutineStub(channel))
    val operationsStreamService = OperationsStreamService(OperationsStreamServiceGrpcKt.OperationsStreamServiceCoroutineStub(channel))
    val sandboxService = SandboxService(SandboxServiceGrpcKt.SandboxServiceCoroutineStub(channel))

    companion object {
        private const val defaultAppName = "tinkoff.invest-api-kotlin-sdk"

        fun createApi(channel: Channel): InvestApi {
            return InvestApi(channel)
        }

        fun defaultChannelBuilder(token: String, appName: String = defaultAppName, target: String): ManagedChannelBuilder<NettyChannelBuilder> {
            val headers = Metadata()
            headers.addAuthHeader(token)
            headers.addAppNameHeader(appName)
            val requestTimeout = Duration.ofSeconds(60)
            val connectionTimeout = Duration.ofSeconds(1)
            return NettyChannelBuilder
                .forTarget(target)
                .intercept(
                    LoggingInterceptor(),
                    MetadataUtils.newAttachHeadersInterceptor(headers),
                    TimeoutInterceptor(requestTimeout)
                )
                .withOption(
                    ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout.toMillis().toInt()
                ) // Намерено сужаем тип - предполагается, что таймаут имеет разумную величину.
                .useTransportSecurity()
                .keepAliveTimeout(60, TimeUnit.SECONDS)
                .maxInboundMessageSize(16777216) // 16 Mb
        }

        fun defaultChannel (token: String, appName: String = defaultAppName, target: String):
                ManagedChannel = defaultChannelBuilder(token, appName, target).build()

        private fun Metadata.addAppNameHeader(appName: String?) {
            val key = Metadata.Key.of("x-app-name", Metadata.ASCII_STRING_MARSHALLER)
            this.put(key, appName ?: defaultAppName)
        }

        private fun Metadata.addAuthHeader(token: String) {
            val authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)
            this.put(authKey, "Bearer $token")
        }
    }
}

internal class LoggingInterceptor : ClientInterceptor {
    private val logger: Logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)
    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>, callOptions: CallOptions, next: Channel
    ): ClientCall<ReqT, RespT> {
        return LoggingClientCall<ReqT, RespT>(
            next.newCall(method, callOptions), logger, method
        )
    }
}
internal class LoggingClientCall<ReqT, RespT>(
    call: ClientCall<ReqT, RespT>?,
    private val logger: Logger,
    private val method: MethodDescriptor<ReqT, RespT>
) : SimpleForwardingClientCall<ReqT, RespT>(call) {
    override fun start(responseListener: Listener<RespT>, headers: Metadata) {
        logger.debug(
            "Готовится вызов метода {} сервиса {}.",
            method.bareMethodName,
            method.serviceName
        )
        super.start(
            LoggingClientCallListener(responseListener, logger, method),
            headers
        )
    }
}

internal class LoggingClientCallListener<RespT>(
    listener: ClientCall.Listener<RespT>?,
    private val logger: Logger,
    private val method: MethodDescriptor<*, RespT>
) : SimpleForwardingClientCallListener<RespT>(listener) {
    @Volatile
    private var lastTrackingId: String? = null
    override fun onHeaders(headers: Metadata) {
        lastTrackingId = headers.get(trackingIdKey)
        delegate().onHeaders(headers)
    }

    override fun onMessage(message: RespT) {
        if (method.type == MethodDescriptor.MethodType.UNARY) {
            logger.debug(
                "Пришёл ответ от метода {} сервиса {}. (x-tracking-id = {})",
                method.bareMethodName,
                method.serviceName,
                lastTrackingId
            )
        } else {
            logger.debug(
                "Пришло сообщение от потока {} сервиса {}.",
                method.bareMethodName,
                method.serviceName
            )
        }
        delegate().onMessage(message)
    }

    companion object {
        private val trackingIdKey = Metadata.Key.of("x-tracking-id", Metadata.ASCII_STRING_MARSHALLER)
    }
}

internal class TimeoutInterceptor(private val timeout: Duration) : ClientInterceptor {
    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>, callOptions: CallOptions, next: Channel
    ): ClientCall<ReqT, RespT> {
        val updatedCallOptions = if (method.type == MethodDescriptor.MethodType.UNARY) callOptions.withDeadlineAfter(timeout.toMillis(), TimeUnit.MILLISECONDS) else callOptions
        return next.newCall(method, updatedCallOptions)
    }
}