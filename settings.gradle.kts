pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    
}
rootProject.name = "kotlin-sdk"
include("kotlin-sdk-grpc-contract", "kotlin-sdk-grpc-core")
include("kotlin-sdk-grpc-example")
