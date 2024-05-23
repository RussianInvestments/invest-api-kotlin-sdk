import com.google.protobuf.gradle.id

plugins {
    id("com.google.protobuf") version "0.9.4"
}

group = parent?.group!!
version = parent?.version!!

dependencies {
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("io.grpc:grpc-protobuf:1.57.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.23.4")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1")
}

sourceSets {
    main {
        proto {
            srcDir("../investAPI/src/docs/contracts")
        }
    }
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.23.4"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.57.1"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}