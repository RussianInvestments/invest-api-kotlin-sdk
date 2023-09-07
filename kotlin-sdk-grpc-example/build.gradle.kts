import java.util.*

dependencies {
    implementation(project(mapOf("path" to ":kotlin-sdk-grpc-core")))
    implementation(project(mapOf("path" to ":kotlin-sdk-grpc-contract")))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.slf4j:slf4j-simple:2.0.7")
}

group = parent?.group!!
version = parent?.version!!

tasks.withType<Test> {
    val properties = Properties()
    properties.load(project.file("local.properties").inputStream())
    systemProperty("token", properties.getProperty("token"))
    systemProperty("target", properties.getProperty("target"))
    systemProperty("instrumentIds", properties.getProperty("instrumentIds"))
}