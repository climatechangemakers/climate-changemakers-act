import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories { mavenCentral() }
}

plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    alias(libs.plugins.sqldelight)
}

group = "org.climatechangemakers.act"

application {
    mainClass.set("org.climatechangemakers.act.ApplicationKt")
}

sqldelight {
    database("Database") {
        packageName = "org.climatechangemakers.act.database"
        dialect(libs.sqldelight.postgresql.dialect.get())
        deriveSchemaFromMigrations = false
        verifyMigrations = false
        migrationOutputDirectory = file("$buildDir/resources/main/migrations")
        migrationOutputFileFormat = ".sql"
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
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.serialization)

    implementation(libs.logback.classic)

    implementation(libs.kotlinx.datetime)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    implementation(libs.sqldelight.jdbc.driver)
    implementation(libs.postgresql)

    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.xmlutil.serialization.jvm)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.testcontainers.postgresql)
}