import java.util.*

plugins {
    id("java-library")
    signing
}

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
    systemProperty("target-main", properties.getProperty("target-main"))
    systemProperty("instrumentIds", properties.getProperty("instrumentIds"))
}

java {
    withSourcesJar()
}

tasks.named("dokkaJavadoc") {
    dependsOn(tasks.withType<JavaCompile>())
    dependsOn(tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>())
    mustRunAfter(tasks.withType<JavaCompile>())
    mustRunAfter(tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>())
}

tasks.register("dokkaJavadocJar", Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

tasks.withType<AbstractPublishToMaven> {
    dependsOn("dokkaJavadocJar")
    dependsOn("sourcesJar")
}
