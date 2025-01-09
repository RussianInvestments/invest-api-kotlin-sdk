package ru.ttech.piapi.generator

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import io.grpc.ClientCall
import io.grpc.ForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import org.slf4j.Logger

class LoggingClientCallListenerGenerator : InternalTypeSpecGenerator {
    override fun generate(): TypeSpec {

        val respT = TypeVariableName("RespT")
        return TypeSpec.classBuilder("LoggingClientCallListener").addTypeVariable(respT)
            .superclass(
                ForwardingClientCallListener.SimpleForwardingClientCallListener::class.asTypeName()
                    .plusParameter(respT)
            )
            .addSuperclassConstructorParameter(CodeBlock.of("listener"))
            .primaryConstructor(
                generateConstructor(respT)
            )
            .addProperty(PropertySpec.builder("logger", Logger::class, KModifier.PRIVATE).initializer("logger").build())
            .addProperty(
                PropertySpec.builder(
                    "method",
                    MethodDescriptor::class.asTypeName().plusParameter(STAR).plusParameter(respT)
                        .copy(true),
                    KModifier.PRIVATE
                ).initializer("method").build()
            )
            .addProperty(
                PropertySpec.builder(
                    "lastTrackingId",
                    String::class.asTypeName().copy(true),
                    KModifier.PRIVATE
                ).mutable(true).initializer("null").addAnnotation(Volatile::class).build()
            )
            .addFunction(
                generateOnHeaders()
            )
            .addFunction(
                generateOnMessage(respT)
            )
            .addType(generateCompanionObject())
            .build()
    }

    private fun generateOnMessage(respT: TypeVariableName) = FunSpec.builder("onMessage")
        .addModifiers(KModifier.OVERRIDE)
        .addParameter("message", respT)
        .addStatement(
            """    if (method?.type == MethodDescriptor.MethodType.UNARY) {
              |        logger.debug(
              |            "Пришёл ответ от метода {} сервиса {}. (x-tracking-id = {})",
              |            method.bareMethodName,
              |            method.serviceName,
              |            lastTrackingId
              |        )
              |    } else {
              |        logger.debug(
              |            "Пришло сообщение от потока {} сервиса {}.",
              |            method?.bareMethodName,
              |            method?.serviceName
              |        )
              |    }
              |    delegate().onMessage(message)""".trimMargin()
        )
        .build()

    private fun generateOnHeaders() = FunSpec.builder("onHeaders")
        .addParameter("headers", Metadata::class)
        .addModifiers(KModifier.OVERRIDE)
        .addStatement(
            """    lastTrackingId = headers.get(trackingIdKey)
                              |    delegate().onHeaders(headers)""".trimMargin()
        )
        .build()

    private fun generateConstructor(respT: TypeVariableName) = FunSpec.constructorBuilder()
        .addParameter(
            "listener",
            ClientCall.Listener::class.asTypeName().plusParameter(respT).copy(true)
        )
        .addParameter("logger", Logger::class)
        .addParameter(
            "method",
            MethodDescriptor::class.asTypeName().plusParameter(STAR)
                .plusParameter(respT).copy(true)
        )
        .build()

    private fun generateCompanionObject() = TypeSpec.companionObjectBuilder()
        .addProperty(
            PropertySpec.builder("trackingIdKey", Metadata.Key::class.plusParameter(String::class), KModifier.PRIVATE)
                .initializer(CodeBlock.of("Metadata.Key.of(\"x-tracking-id\", Metadata.ASCII_STRING_MARSHALLER)"))
                .build()
        )
        .build()
}