package ru.ttech.piapi.generator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.get
import ru.ttech.piapi.generator.task.CodeGeneratorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("unused")
class CodeGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("helpersCodegen", CodeGeneratorPluginExtension::class.java, target)

        with(target.tasks) {

            val codegenTask = register("genHelpersClasses", CodeGeneratorTask::class.java) {
                group = "Code generation"
                description = "Generates Grpc client helper classes with single manageable factory"

                packageName = extension.packageName
                outputPackage = extension.generatedPackageName
                outputPath = extension.outputPath.asFile
            }

            codegenTask.configure {
                dependsOn(
                    target.parent?.childProjects
                        ?.filter { extension.dependentProjects.contains(it.value.name) }
                        ?.map { it.value.tasks.withType(Jar::class.java) }
                )
            }

            target.tasks.withType(KotlinCompile::class.java).configureEach {
                dependsOn(codegenTask)
            }
        }

        target.afterEvaluate {
            (target.extensions["sourceSets"] as SourceSetContainer)["main"]
                .java
                .srcDir(extension.outputPath.asFile)
        }
    }
}