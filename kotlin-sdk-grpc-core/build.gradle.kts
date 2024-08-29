import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("ru.tinvest.piapi.helpers-codegen-plugin")
    id("java-library")
    id("maven-publish")
    id("java")
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
    generatedPackageName = "ru.tinvest.piapi.core"
}

tasks.named<AbstractArchiveTask>("kotlinSourcesJar").configure {
    from(tasks.getByName("genHelpersClasses"))
    mustRunAfter(tasks.getByName("genHelpersClasses"))
    dependsOn(tasks.getByName("genHelpersClasses"))
    archiveClassifier.set("sources")
    archiveExtension.set("jar")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register("sourcesJar", Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
    archiveExtension.set("jar")
}

tasks.named<Jar>("sourcesJar") {
    from(tasks.getByName("genHelpersClasses"))
    mustRunAfter(tasks.getByName("genHelpersClasses"))
    dependsOn(tasks.getByName("genHelpersClasses"))
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

publishing {
    afterEvaluate {
        signing.sign(publishing.publications[project.name])
    }
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
            }
        }
    }
}