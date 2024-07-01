pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("kotlin-sdk-code-generator-plugin")
}
rootProject.name = "kotlin-sdk"
include("kotlin-sdk-grpc-contract")
include("kotlin-sdk-grpc-core")
include("kotlin-sdk-grpc-example")
