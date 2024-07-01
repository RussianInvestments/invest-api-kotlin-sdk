plugins {
    id("ru.tinvest.piapi.helpers-codegen-plugin")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.reflections:reflections:0.10.2")
    implementation(project(":kotlin-sdk-grpc-contract"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("io.grpc:grpc-netty:1.65.0")
    implementation("io.netty:netty-codec-http:4.1.111.Final")
    implementation("org.slf4j:slf4j-api:2.0.7")
}

group = parent?.group!!
version = parent?.version!!

helpersCodegen {
    packageName = "ru.tinkoff.piapi.contract.v1"
    generatedPackageName = "ru.tinvest.piapi.core"
}
