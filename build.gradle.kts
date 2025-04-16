plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

//kotlin {
//    jvmToolchain {
//        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
//    }
//}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Работа с JSON
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    // Работа с HTTP (для общения с Ollama API)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Kotlin reflection (необходим для jackson-kotlin)
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
}

application {
    mainClass.set("assistant.MainKt")
}

tasks {
    test {
        useJUnitPlatform()
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "21"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    register<Jar>("fatJar") {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes["Main-Class"] = "assistant.MainKt"
        }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)
    }
//    shadowJar {
//        archiveBaseName.set("intelligent-assistant")
//        archiveClassifier.set("")
//        archiveVersion.set("")
//        manifest {
//            attributes["Main-Class"] = "com.example.MainKt"
//        }
//    }
}