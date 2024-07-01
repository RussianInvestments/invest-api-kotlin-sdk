package ru.tinvest.piapi.generator.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import ru.tinvest.piapi.generator.generateFileSpecs
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile


abstract class CodeGeneratorTask : DefaultTask() {
    @get:Input
    lateinit var packageName: String

    @get:Input
    lateinit var outputPackage: String

    @get:OutputDirectory
    lateinit var outputPath: File

    @TaskAction
    fun execute() {
        defineClassLoader(packageName).use { classLoader ->
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

    private fun defineClassLoader(packageName: String): URLClassLoader {
        val fileList = project.parent?.childProjects?.flatMap { it.value.tasks.getByName("jar").outputs.files.files }
            ?.filter { it.exists() }
            ?: emptyList()
        val classLoader =
            URLClassLoader.newInstance(fileList.map { it.toURI().toURL() }.toTypedArray(), this.javaClass.classLoader)
        fileList.forEach {
            JarFile(it).use { jarFile ->
                for (jarEntry in jarFile.entries()) {
                    if (jarEntry.name.startsWith(packageName.replace(".", "/")) && jarEntry.name.endsWith(".class")) {
                        var className: String = jarEntry.name.substring(0, jarEntry.name.length - 6) // remove ".class"
                        className = className.replace('/', '.')
                        classLoader.loadClass(className)
                    }
                }
            }
        }
        return classLoader
    }
}