dependencies {
    implementation(project(mapOf("path" to ":kotlin-sdk-grpc-contract")))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("io.grpc:grpc-netty:1.57.1")
    implementation("org.slf4j:slf4j-api:2.0.7")
}
group = parent?.group!!
version = parent?.version!!

