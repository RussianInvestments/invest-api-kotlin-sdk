package ru.ttech.piapi.generator.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import ru.ttech.piapi.generator.generateFileSpecs
import java.io.File
import java.net.URL
import java.net.URLClassLoader


abstract class CodeGeneratorTask : DefaultTask() {
    @get:Input
    lateinit var packageName: String

    @get:Input
    lateinit var outputPackage: String

    @get:OutputDirectory
    lateinit var outputPath: File

    @TaskAction
    fun execute() {
        defineClassLoader().use { classLoader ->
            val fileSpecs = generateFileSpecs(packageName, outputPackage, classLoader)
            val outputDir = outputPath
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            fileSpecs.forEach {
                it.writeTo(outputDir)
            }
        }
    }

    private fun defineClassLoader(): URLClassLoader {
        val fileList = project.parent?.childProjects?.flatMap { it.value.tasks.getByName("jar").outputs.files.files }
            ?.filter { it.exists() }
            ?: emptyList()
        return URLClassLoader.newInstance(
            fileList.map { it.toURI().toURL() }.toTypedArray<URL?>(),
            javaClass.classLoader
        )
    }
}