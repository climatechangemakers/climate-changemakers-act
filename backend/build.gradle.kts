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
    implementation(libs.bcrypt)
    implementation(libs.dagger)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.contentnegotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.statuspages)
    implementation(libs.logback.classic)
    implementation(libs.postgresql)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.sqldelight.jdbc.driver)
    implementation(libs.xmlutil.serialization.jvm)
    kapt(libs.dagger.compiler)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.testcontainers.postgresql)
}