package ru.ttech.piapi.generator.plugin

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property

abstract class CodeGeneratorPluginExtension(project: Project) {
    var packageName: String
        get() = packageNameProperty.get()
        set(value) = packageNameProperty.set(value)

    var generatedPackageName: String
        get() = generatedPackageNameProperty.get()
        set(value) = generatedPackageNameProperty.set(value)

    var outputPath: Directory
        get() = outputPathProperty.get()
        set(value) = outputPathProperty.set(value)

    var dependentProjects: List<String>
        get() = dependentProjectsProperty.get()
        set(value) = dependentProjectsProperty.set(value)

    private val objects = project.objects

    internal val packageNameProperty: Property<String> = objects.property(String::class.java)
        .convention(null as String?)

    internal val generatedPackageNameProperty: Property<String> = objects.property(String::class.java)
        .convention(null as String?)

    internal val outputPathProperty = objects.directoryProperty()
        .convention(project.layout.buildDirectory.dir("generated/main/kotlin"))

    internal val dependentProjectsProperty = objects.listProperty(String::class.java)
        .convention(listOf("kotlin-sdk-grpc-contract"))

}