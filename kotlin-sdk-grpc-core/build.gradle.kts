import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("ru.ttech.piapi.helpers-codegen-plugin")
    id("java-library")
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

tasks.register("dokkaJavadocJar", Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

tasks.withType<AbstractPublishToMaven> {
    dependsOn("dokkaJavadocJar")
    dependsOn("sourcesJar")
}

tasks.named("dokkaHtml") {
    dependsOn(tasks.named("genHelpersClasses"))
}
