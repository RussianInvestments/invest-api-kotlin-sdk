import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URI

plugins {
    id("ru.ttech.piapi.helpers-codegen-plugin")
    id("java-library")
    id("maven-publish")
    signing
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
    generatedPackageName = "ru.ttech.piapi.core"
}

tasks.named<AbstractArchiveTask>("kotlinSourcesJar").configure {
    from(tasks.getByName("genHelpersClasses"))
    mustRunAfter(tasks.getByName("genHelpersClasses"))
    dependsOn(tasks.getByName("genHelpersClasses"))
    archiveClassifier.set("sources")
    archiveExtension.set("jar")
}

java {
    withSourcesJar()
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


tasks.named<Jar>("sourcesJar") {
    from(tasks.getByName("genHelpersClasses"))
    mustRunAfter(tasks.getByName("genHelpersClasses"))
    dependsOn(tasks.getByName("genHelpersClasses"))
    archiveClassifier.set("sources")
    archiveExtension.set("jar")
}

tasks.named<DokkaTask>("dokkaJavadoc").configure {
    dependsOn(tasks.named("compileKotlin"))
    mustRunAfter(tasks.named("compileKotlin"))
    dokkaSourceSets {
        configureEach {
            skipEmptyPackages.set(false)
            suppressGeneratedFiles.set(false)
            sourceRoots.from(sourceSets.main.get().kotlin.srcDirs.toTypedArray())
        }
    }
    dependsOn(tasks.named("compileJava"))
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
            url = URI(findProperty("maven.publish.url").toString())
            credentials {
                username = findProperty("ossh.username").toString()
                password = findProperty("ossh.password").toString()
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
                description.set("Kotlin SDK for T-Invest API")
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
}