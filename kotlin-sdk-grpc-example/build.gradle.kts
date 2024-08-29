import java.util.Properties

plugins {
    id("java-library")
    id("maven-publish")
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

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri(project.properties["maven.publish.url"]!!)
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
            pom {
                name.set(project.name)
                description.set("Kotlin SDK examples library for T-Invest API")
                url.set("https://github.com/RussianInvestments/invest-api-kotlin-sdk")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://github.com/RussianInvestments/invest-api-kotlin-sdk/blob/main/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("NemetsSY-TCS")
                        name.set("Sergey Nemets")
                    }
                }
            }
        }
    }
}