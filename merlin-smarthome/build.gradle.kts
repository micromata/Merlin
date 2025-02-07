plugins {
  kotlin("jvm")
  application
}

description = "merlin-smarthome"

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":merlin-core"))
  implementation(project(":merlin-velocity"))
  implementation("org.slf4j:slf4j-log4j12:2.0.16")
  implementation("io.github.microutils:kotlin-logging:1.12.0")
}

application {
  mainClass.set("de.micromata.merlin.smarthome.examples.homeassistant_knx.HomeAssistantBuilder")
}

sourceSets {
  named("main") {
    java.setSrcDirs(emptyList<String>())  // Kein separater Java-Ordner
    withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
      kotlin.srcDirs("src/main/java", "src/main/kotlin")
    }
    resources.srcDirs("src/main/resources")
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions.jvmTarget = "1.8"
}
