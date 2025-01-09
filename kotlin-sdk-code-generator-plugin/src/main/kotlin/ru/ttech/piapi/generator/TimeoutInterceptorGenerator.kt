package ru.ttech.piapi.generator

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
import java.time.Duration

class TimeoutInterceptorGenerator : InternalTypeSpecGenerator {
    override fun generate(): TypeSpec {

        return TypeSpec.classBuilder("TimeoutInterceptor")
            .addSuperinterface(ClientInterceptor::class)
            .addModifiers(KModifier.INTERNAL)
            .primaryConstructor(
                generateConstructor()
            )
            .addProperty(
                PropertySpec.builder("timeout", Duration::class)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("timeout")
                    .build()
            )
            .addFunction(
                generateInterceptCall()
            )
            .build()
    }

    private fun generateInterceptCall(): FunSpec {
        val reqT = TypeVariableName("ReqT")
        val respT = TypeVariableName("RespT")
        return FunSpec.builder("interceptCall")
            .addTypeVariables(listOf(reqT, respT))
            .addParameter(
                ParameterSpec.builder(
                    "method",
                    MethodDescriptor::class.asTypeName().plusParameter(reqT)
                        .plusParameter(respT)
                ).build()
            )
            .addParameter(ParameterSpec.builder("callOptions", CallOptions::class).build())
            .addParameter(ParameterSpec.builder("next", Channel::class).build())
            .returns(
                ClientCall::class.asTypeName().plusParameter(reqT)
                    .plusParameter(respT)
            )
            .addModifiers(KModifier.OVERRIDE)
            .addStatement(
                """    val updatedCallOptions = if (method.type == MethodDescriptor.MethodType.UNARY) callOptions.withDeadlineAfter(timeout.toMillis(), TimeUnit.MILLISECONDS) else callOptions
                              |return next.newCall(method, updatedCallOptions)
                """.trimMargin()
            )
            .build()
    }

    private fun generateConstructor() =
        FunSpec.constructorBuilder().addParameter(ParameterSpec.builder("timeout", Duration::class).build()).build()
}