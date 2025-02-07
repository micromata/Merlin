plugins {
    kotlin("jvm")
}

description = "merlin-velocity"

repositories {
    mavenCentral()
}

dependencies {
    api("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}
