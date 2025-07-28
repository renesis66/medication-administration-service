plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.24"
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.2"
    id("io.micronaut.test-resources") version "4.4.2"
}

version = "0.1"
group = "com.healthcare.medication"

val kotlinVersion = project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

dependencies {
    // Micronaut core
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.validation:micronaut-validation")
    
    // Kotlin support
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    
    // YAML configuration support (required for application.yml)
    runtimeOnly("org.yaml:snakeyaml")
    
    // Kotlin coroutines (required for suspend functions)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    
    // Logging implementation
    runtimeOnly("ch.qos.logback:logback-classic")
    
    // JSON processing
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // DynamoDB
    implementation("software.amazon.awssdk:dynamodb-enhanced")
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:netty-nio-client")
    
    // Annotation processing
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.validation:micronaut-validation-processor")
    
    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("ch.qos.logback:logback-classic")
}

application {
    mainClass.set("com.healthcare.medication.ApplicationKt")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.healthcare.medication.*")
    }
}