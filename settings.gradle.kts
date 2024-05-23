pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    
}
rootProject.name = "kotlin-sdk"
include("kotlin-sdk-grpc-contract")
include("kotlin-sdk-grpc-core")
include("kotlin-sdk-grpc-example")
