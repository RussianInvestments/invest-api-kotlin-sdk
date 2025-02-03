plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("io.grpc:grpc-netty:1.65.0")
    implementation("io.netty:netty-codec-http:4.1.111.Final")
    implementation("io.grpc:grpc-stub:1.65.0")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation(gradleApi())
    implementation("com.squareup:kotlinpoet:1.17.0")
    implementation(kotlin("reflect"))
    implementation("org.reflections:reflections:0.10.2")
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("io.grpc:grpc-protobuf:1.57.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.23.4")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24")
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("helpersCodegen") {
            description = "Generates Grpc client helper classes with single manageable factory"
            displayName = "Kotlin SDK codegen plugin"
            id = "ru.ttech.piapi.helpers-codegen-plugin"
            implementationClass = "ru.ttech.piapi.generator.plugin.CodeGeneratorPlugin"
        }
    }
}