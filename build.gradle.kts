import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm") version "1.9.20" apply false
    id("org.jetbrains.dokka") version "1.9.20"
    id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "ru.t-technologies.invest.piapi.kotlin"
version = "1.33.0"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.vanniktech.maven.publish")
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        mavenCentral()
    }

    val testImplementation by configurations
    val testRuntimeOnly by configurations
    val implementation by configurations

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
        implementation("io.grpc:grpc-kotlin-stub:1.3.0")
        implementation("io.grpc:grpc-protobuf:1.57.1")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
        options.encoding = "UTF-8"
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()

        pom {
            name.set(project.name)
            description.set("Kotlin SDK contract for T-Invest API")
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

            scm {
                connection.set("scm:git:git://github.com/RussianInvestments/invest-api-kotlin-sdk.git")
                developerConnection.set("scm:git:ssh://github.com/RussianInvestments/invest-api-kotlin-sdk.git")
                url.set("https://github.com/RussianInvestments/invest-api-kotlin-sdk")
            }

        }
    }
}