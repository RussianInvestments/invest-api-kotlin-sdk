package ru.ttech.piapi.generator

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import io.grpc.ClientCall
import io.grpc.ForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import org.slf4j.Logger

class LoggingClientCallGenerator : InternalTypeSpecGenerator {
    override fun generate(): TypeSpec {
        val reqT = TypeVariableName("ReqT")
        val respT = TypeVariableName("RespT")
        val typeVariables = listOf(reqT, respT)
        return TypeSpec.classBuilder("LoggingClientCall")
            .addTypeVariables(typeVariables)
            .superclass(
                ForwardingClientCall.SimpleForwardingClientCall::class.asTypeName()
                    .plusParameter(reqT)
                    .plusParameter(respT)
            )
            .addSuperclassConstructorParameter(CodeBlock.of("call"))
            .addModifiers(KModifier.INTERNAL)
            .primaryConstructor(
                generateConstructor(reqT, respT)
            )
            .addProperty(
                PropertySpec.builder("logger", org.slf4j.Logger::class, KModifier.PRIVATE).initializer("logger").build()
            )
            .addProperty(
                PropertySpec.builder(
                    "method",
                    MethodDescriptor::class.asTypeName().plusParameter(reqT)
                        .plusParameter(respT).copy(true),
                    KModifier.PRIVATE
                ).initializer("method").build()
            )
            .addFunction(
                generateStartFunction(respT)
            )
            .build()
    }

    private fun generateStartFunction(respT: TypeVariableName) = FunSpec.builder("start")
        .addParameter(
            "responseListener",
            ClientCall.Listener::class.asTypeName().plusParameter(respT)
        )
        .addParameter("headers", Metadata::class)
        .addModifiers(KModifier.OVERRIDE)
        .addStatement(
            """    logger.debug(
                  |             "Готовится вызов метода {} сервиса {}.",
                  |             method?.bareMethodName,
                  |             method?.serviceName
                  |        )
                  |        super.start(
                  |            LoggingClientCallListener(responseListener, logger, method),
                  |            headers
                  |        )
                """.trimMargin()
        ).build()

    private fun generateConstructor(
        reqT: TypeVariableName,
        respT: TypeVariableName
    ) = FunSpec.constructorBuilder()
        .addParameter(
            "call",
            ClientCall::class.asTypeName().plusParameter(reqT)
                .plusParameter(respT).copy(true)
        )
        .addParameter("logger", Logger::class)
        .addParameter(
            "method",
            MethodDescriptor::class.asTypeName().plusParameter(reqT)
                .plusParameter(respT).copy(true)
        )
        .build()
}