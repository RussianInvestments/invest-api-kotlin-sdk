package ru.ttech.piapi.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import io.grpc.Channel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.kotlin.StubFor
import io.grpc.netty.NettyChannelBuilder
import io.grpc.stub.MetadataUtils
import io.netty.channel.ChannelOption
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.functions
import kotlin.reflect.full.starProjectedType

fun main() {
    val genPackagePath = "ru.tinkoff.piapi.contract.v1"
    val packageName = "ru.tinkoff.piapi.core"

    generateFileSpecs(genPackagePath, packageName).forEach {
        println(it.packageName)
        it.writeTo(System.out)
    }

}

fun generateFileSpecs(genPackageName: String, packageName: String, classLoader: ClassLoader = ClassLoader.getSystemClassLoader()): List<FileSpec> {
    val allClasses = scanPackage(genPackageName, classLoader)
    val fileSpecs = allClasses.flatMap { kClass ->
        arrayOf(true, false).mapNotNull { isAsync ->
            generateDelegateClass(
                kClass,
                ClassName(packageName, kClass.simpleName!!.removeSuffix("CoroutineStub")),
                isAsync
            )
        }
    }
    val investApi = generateApiClass(packageName, fileSpecs)
    return sequenceOf(fileSpecs.asSequence(), sequenceOf(investApi)).flatMap { it }.toList()
}

internal fun scanPackage(packageName: String, classLoader: ClassLoader): Set<KClass<*>> {
    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackage(packageName)
            .setUrls(ClasspathHelper.forClassLoader(classLoader))
            .filterInputsBy(FilterBuilder().includePackage(packageName))
    )

    return reflections.get(Scanners.TypesAnnotated.of(StubFor::class.java))
        .map { Class.forName(it, true, classLoader).kotlin }.toCollection(HashSet())
}

internal fun scanFunctions(kClass: KClass<*>): List<KFunction<*>> =
    kClass.functions.filter { f ->
        f.isSuspend || f.returnType.classifier == Flow::class.starProjectedType.classifier
    }.toCollection(ArrayList())

internal fun scanSyncFunctions(kClass: KClass<*>): List<KFunction<*>> =
    kClass.functions
        .filter { f ->
            f.isSuspend || f.returnType.classifier == Flow::class.starProjectedType.classifier
        }
        .filter { !isStreamFunction(it) }
        .toCollection(ArrayList())

internal fun generateDelegateClass(
    kClass: KClass<*>,
    className: ClassName,
    isAsync: Boolean = true
): FileSpec? {
    val typeSpecBuilder = TypeSpec.classBuilder("${className.simpleName}${if (isAsync) "Async" else "Sync"}")
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter(
                    ParameterSpec.builder("coroutineStub", kClass.asTypeName()).build()
                ).build()
        )
        .addModifiers(KModifier.DATA)
        .addProperty(
            PropertySpec.builder("coroutineStub", kClass.asTypeName())
                .addModifiers(KModifier.PRIVATE)
                .initializer("coroutineStub")
                .build()
        )

    val classFunctions = if (isAsync) scanFunctions(kClass) else scanSyncFunctions(kClass)

    if (classFunctions.isEmpty()) {
        //no functions in class (for example, no sync functions when generating sync class)
        return null
    }

    val fileSpecBuilder =
        FileSpec.builder(className.packageName, "${className.simpleName}${if (isAsync) "Async" else "Sync"}")

    typeSpecBuilder.addFunctions(
        classFunctions.map {
            if (isStreamFunction(it)) {
                fileSpecBuilder.addImport("kotlinx.coroutines", "Job")
                fileSpecBuilder.addImport("kotlinx.coroutines", "coroutineScope")
                fileSpecBuilder.addImport("kotlinx.coroutines.flow", "cancellable")
                fileSpecBuilder.addImport("kotlinx.coroutines", "launch")
                fileSpecBuilder.addImport("kotlinx.coroutines.flow", "map")
                streamFuncSpec(it)
            } else {
                unaryFuncSpec(it, isAsync)
            }
        }
    ).addFunction(
        FunSpec.constructorBuilder()
            .addParameter(ParameterSpec("channel", Channel::class.asTypeName()))
            .callThisConstructor("${kClass.asClassName()}(channel)")
            .build()
    )

    fileSpecBuilder.addType(typeSpecBuilder.build())

    if (!isAsync) {
        fileSpecBuilder.addImport("kotlinx.coroutines", "runBlocking")
    }

    return fileSpecBuilder.build()
}

internal fun unaryFuncSpec(it: KFunction<*>, isAsync: Boolean): FunSpec {
    val params = it.parameters.filter { p -> p.kind != KParameter.Kind.INSTANCE }
        .filter { p -> p.type != Metadata::class.starProjectedType }

    val funSpecBuilder = FunSpec.builder(it.name)
        .returns(it.returnType.asTypeName())
        .addParameters(params.map { p ->
            ParameterSpec.builder(p.name!!, p.type.asTypeName())
                .build()
        })

    if (isAsync) {
        funSpecBuilder.addModifiers(KModifier.SUSPEND)
        funSpecBuilder.addStatement("return coroutineStub.${it.name}(${params.joinToString { p -> p.name!! }})")
    } else {
        funSpecBuilder.addStatement("return runBlocking·{ coroutineStub.${it.name}(${params.joinToString { p -> p.name!! }}) }")
    }

    return funSpecBuilder.build()
}

internal fun streamFuncSpec(it: KFunction<*>): FunSpec {
    val params = it.parameters.filter { p -> p.kind != KParameter.Kind.INSTANCE }
        .filter { p -> p.type != Metadata::class.starProjectedType }

    val funSpecBuilder = FunSpec.builder(it.name)
        .returns(Job::class)
        .addParameters(params.map { p ->
            ParameterSpec.builder(p.name!!, p.type.asTypeName())
                .build()
        })
        .addParameter(
            ParameterSpec.builder(
                "consumers",
                LambdaTypeName.get(
                    parameters = listOf(
                        ParameterSpec.builder(
                            "it",
                            it.returnType.arguments.first().type!!.asTypeName()
                        ).build()
                    ), returnType = Unit::class.asTypeName()
                )
            ).addModifiers(KModifier.VARARG).build()
        )

    funSpecBuilder.addModifiers(KModifier.SUSPEND)
    funSpecBuilder.addStatement(
        """return coroutineScope·{
            |   var stub = coroutineStub.${it.name}(${params.joinToString { p -> p.name!! }}).cancellable()
            |   launch {
            |       consumers.forEachIndexed { index, consumer ->
            |           if (index == consumers.size - 1)
            |               stub.collect { consumer.invoke(it) }
            |           else
            |               stub = stub.map { consumer.invoke(it); it }
            |        }
            |    }
            |}
        """.trimMargin()
    )

    return funSpecBuilder.build()
}

internal fun isStreamFunction(kFunction: KFunction<*>): Boolean {
    return kFunction.returnType.classifier == Flow::class.starProjectedType.classifier
}

internal fun generateApiClass(packageName: String, classes: List<FileSpec>): FileSpec {
    val investApiBuilder = investApiBuilder(classes, packageName)

    val additionalClassGenerators = listOf(
        TimeoutInterceptorGenerator(),
        LoggingInterceptorGenerator(),
        LoggingClientCallGenerator(),
        LoggingClientCallListenerGenerator()
    )

    return FileSpec.builder(packageName, "InvestApi").addTypes(
        (sequenceOf(investApiBuilder.build()) + additionalClassGenerators.map { it.generate() }).toList()
    )
        .addImport(MetadataUtils::class.asTypeName().packageName, MetadataUtils::class.asTypeName().simpleName)
        .addImport(ChannelOption::class.asTypeName().packageName, ChannelOption::class.asTypeName().simpleName)
        .addImport(TimeUnit::class.asTypeName().packageName, TimeUnit::class.asTypeName().simpleName)
        .addImport(LoggerFactory::class.asTypeName().packageName, LoggerFactory::class.asTypeName().simpleName)

        .build()

}

private fun investApiBuilder(
    classes: List<FileSpec>,
    packageName: String
): TypeSpec.Builder {
    val investApiBuilder = TypeSpec.classBuilder("InvestApi")
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter(
                    ParameterSpec.builder("channel", Channel::class.asTypeName()).build()
                )
                .addModifiers(KModifier.PRIVATE)
                .build()
        )
        .addProperties(
            classes.map {
                PropertySpec.builder(
                    it.name.replaceFirstChar { n -> n.lowercase() },
                    ClassName(it.packageName, it.name)
                ).initializer(CodeBlock.of("${it.name}(channel)"))
                    .build()
            }
        )

    val companionObject = generateCompanionObject(packageName)

    investApiBuilder.addType(companionObject)
    return investApiBuilder
}

private fun generateCompanionObject(packageName: String): TypeSpec {
    val parameterSpecs = listOf(
        ParameterSpec("token", String::class.asTypeName()),
        ParameterSpec.builder("appName", String::class).defaultValue("InvestApi.defaultAppName")
            .build(),
        ParameterSpec("target", String::class.asTypeName())
    )
    return TypeSpec.companionObjectBuilder()
        .addProperty(
            PropertySpec.builder("defaultAppName", String::class.asTypeName()).mutable(false)
                .addModifiers(KModifier.PRIVATE, KModifier.CONST).initializer("\"tinkoff.invest-api-kotlin-sdk\"")
                .build()
        )
        .addFunction(
            FunSpec.builder("createApi").returns(ClassName(packageName, "InvestApi"))
                .addParameter("channel", Channel::class.asTypeName())
                .addCode("return InvestApi(channel)")
                .addKdoc("Creates InvestApi instance from channel")
                .build()
        )
        .addFunction(
            FunSpec.builder("defaultChannelBuilder")
                .addParameters(
                    parameterSpecs
                )
                .returns(
                    ManagedChannelBuilder::class.asTypeName().plusParameter(NettyChannelBuilder::class.asTypeName())
                )
                .addStatement(
                    """    val headers = Metadata()
                          |headers.addAuthHeader(token)
                          |headers.addAppNameHeader(appName)
                          |val requestTimeout = Duration.ofSeconds(60)
                          |val connectionTimeout = Duration.ofSeconds(1)
                          |return NettyChannelBuilder
                          |    .forTarget(target)
                          |    .intercept(
                          |        LoggingInterceptor(),
                          |        MetadataUtils.newAttachHeadersInterceptor(headers),
                          |        TimeoutInterceptor(requestTimeout)
                          |    )
                          |    .withOption(
                          |        ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout.toMillis().toInt()
                          |    ) // Намерено сужаем тип - предполагается, что таймаут имеет разумную величину.
                          |    .useTransportSecurity()
                          |    .keepAliveTimeout(60, TimeUnit.SECONDS)
                          |    .maxInboundMessageSize(16777216) // 16 Mb""".trimMargin()
                )
                .build()
        )
        .addFunction(
            FunSpec.builder("defaultChannel")
                .addParameters(
                    parameterSpecs
                )
                .returns(ManagedChannel::class.asTypeName())
                .addStatement("return InvestApi.defaultChannelBuilder(token, appName, target).build()")
                .build()
        )
        .addFunction(
            FunSpec.builder("addAppNameHeader")
                .receiver(Metadata::class.asTypeName())
                .addModifiers(KModifier.PRIVATE)
                .addParameter("appName", String::class.asTypeName().copy(true))
                .addStatement(
                    """    val key = Metadata.Key.of("x-app-name", Metadata.ASCII_STRING_MARSHALLER)
                          |this.put(key, appName ?: InvestApi.defaultAppName)""".trimMargin()
                )
                .build()
        )
        .addFunction(
            FunSpec.builder("addAuthHeader")
                .receiver(Metadata::class.asTypeName())
                .addModifiers(KModifier.PRIVATE)
                .addParameter("token", String::class)
                .addStatement(
                    """    val authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)
                          |this.put(authKey, "Bearer ${'$'}token")""".trimMargin()
                )
                .build()
        )
        .build()
}