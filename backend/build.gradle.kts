import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.cli.jvm.compiler.findMainClass
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

buildscript {
    repositories { gradlePluginPortal() }
    dependencies {
        classpath("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.1")
    }
}

plugins {
    application
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
    kotlin("kapt") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.squareup.sqldelight") version "1.5.1"
}

group = "org.climatechangemakers.act"

application {
    mainClass.set("org.climatechangemakers.act.ApplicationKt")
}

sqldelight {
    database("Database") {
        packageName = "org.climatechangemakers.act.database"
        dialect = "postgresql"
        deriveSchemaFromMigrations = false
        verifyMigrations = false
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes("Main-Class" to "org.climatechangemakers.act.ApplicationKt")
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("com.google.dagger:dagger:2.37")
    kapt("com.google.dagger:dagger-compiler:2.37")

    implementation("com.squareup.sqldelight:jdbc-driver:1.5.1")
    implementation("org.postgresql:postgresql:42.2.16")


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("io.ktor:ktor-serialization:$ktor_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation("org.testcontainers:postgresql:1.15.3")
}