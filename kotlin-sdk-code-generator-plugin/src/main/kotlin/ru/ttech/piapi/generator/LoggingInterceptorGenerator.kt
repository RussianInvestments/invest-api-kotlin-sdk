package ru.ttech.piapi.generator

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.MethodDescriptor
import org.slf4j.Logger

class LoggingInterceptorGenerator : InternalTypeSpecGenerator {
    override fun generate(): TypeSpec {
        val reqT = TypeVariableName("ReqT")
        val respT = TypeVariableName("RespT")
        return TypeSpec.classBuilder("LoggingInterceptor")
            .addSuperinterface(ClientInterceptor::class)
            .addModifiers(KModifier.INTERNAL)
            .primaryConstructor(
                generateConstructor()
            )
            .addProperty(
                PropertySpec.builder("logger", Logger::class)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("logger")
                    .build()
            )
            .addFunction(
                generateInterceptCall(reqT, respT)
            )
            .build()
    }

    private fun generateInterceptCall(
        reqT: TypeVariableName,
        respT: TypeVariableName
    ): FunSpec {
        val typeVariables = listOf(reqT, respT)

        return FunSpec.builder("interceptCall")
            .addTypeVariables(typeVariables)
            .addParameter(
                "method",
                MethodDescriptor::class.asTypeName().plusParameter(reqT)
                    .plusParameter(respT)
            )
            .addParameter("callOptions", CallOptions::class.asTypeName().copy(nullable = true))
            .addParameter("next", Channel::class.asTypeName().copy(nullable = true))
            .returns(
                ClientCall::class.asTypeName().plusParameter(reqT)
                    .plusParameter(respT)
            )
            .addModifiers(KModifier.OVERRIDE)
            .addStatement(
                """    return LoggingClientCall<ReqT, RespT>(
             |     next?.newCall(method, callOptions), logger, method
             |     )""".trimMargin()
            )
            .build()
    }

    private fun generateConstructor() = FunSpec.constructorBuilder().addParameter(
        ParameterSpec.builder("logger", Logger::class).defaultValue(
            CodeBlock.of("LoggerFactory.getLogger(LoggingInterceptor::class.java)")
        ).build()
    ).build()
}