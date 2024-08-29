import com.google.protobuf.gradle.id

plugins {
    id("com.google.protobuf") version "0.9.4"
    id("java-library")
    id("maven-publish")
    signing
}

group = parent?.group!!
version = parent?.version!!

dependencies {
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("io.grpc:grpc-protobuf:1.57.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.23.4")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1")
}

sourceSets {
    main {
        proto {
            srcDir("../investAPI/src/docs/contracts")
        }
    }
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.23.4"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.57.1"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

java {
    withSourcesJar()
}

tasks.named("dokkaJavadoc") {
    dependsOn(tasks.named("compileJava"))
    mustRunAfter(tasks.named("compileJava"))
}

tasks.create<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

tasks.withType<AbstractPublishToMaven> {
    dependsOn("dokkaJavadocJar")
    dependsOn("sourcesJar")
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri(project.properties["maven.publish.url"]!!)
            credentials {
                username = if (System.getenv("MAVEN_USERNAME").isNullOrEmpty()) project.properties["ossh.username"].toString() else System.getenv("MAVEN_USERNAME")
                password = if (System.getenv("MAVEN_PASSWORD").isNullOrEmpty()) project.properties["ossh.password"].toString() else System.getenv("MAVEN_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
            artifact(tasks.getByName("dokkaJavadocJar"))
            the<SigningExtension>().sign(this)

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
            }
        }
    }
}
