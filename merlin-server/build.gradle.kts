import java.util.Properties // ✅ Import hinzufügen

plugins {
    kotlin("jvm")
    application
    distribution
}

description = "merlin-server"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":merlin-core"))
    implementation(libs.commons.cli)
    implementation(libs.commons.io)
    implementation(libs.commons.collections4)
    implementation(libs.commons.text)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.annotations)
    implementation(libs.poi)
    implementation(libs.slf4j.log4j12)

    implementation(libs.jetty.server)
    implementation(libs.jetty.servlet)
    implementation(libs.jetty.servlets)
    implementation(libs.jaxb.core)
    implementation(libs.jaxb.runtime)
    implementation(libs.jersey.container.servlet)
    implementation(libs.jersey.media.multipart)
    implementation(libs.jersey.media.json.jackson)
    implementation(libs.jersey.hk2)
    implementation(libs.jaxb.api)
    implementation(libs.jaxws.api)

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation(libs.mockito.core)
}

application {
    mainClass.set("de.micromata.merlin.server.Main")
    applicationDefaultJvmArgs = listOf("-DapplicationHome=MY_APPLICATION_HOME")
}

tasks.register<JavaExec>("runApp") {
    dependsOn(":merlin-webapp:npmBuild")
    doFirst {
        jvmArgs = listOf("-DapplicationHome=${rootDir}")
    }
}

// 🔹 Erstellung der Versionsdatei mit modernem `layout.buildDirectory`
tasks.register("createVersionProperties") {
    dependsOn(tasks.processResources)
    doLast {
        val versionFile = layout.buildDirectory.file("resources/main/version.properties").get().asFile
        versionFile.parentFile.mkdirs()
        versionFile.writer().use { writer ->
            val props = Properties() // ✅ `java.util.Properties` jetzt erkannt!
            props["version"] = project.version.toString()
            props["name"] = project.name
            props["build.date.millis"] = System.currentTimeMillis().toString()
            props.store(writer, null)
        }
    }
}

tasks.named("classes") {
    dependsOn("createVersionProperties")
}

// Workaround für App-Home-Verzeichnis
tasks.named<CreateStartScripts>("startScripts") {
    doLast {
        unixScript.writeText(unixScript.readText().replace("MY_APPLICATION_HOME", "\$APP_HOME"))
        windowsScript.writeText(windowsScript.readText().replace("MY_APPLICATION_HOME", "%~dp0.."))
    }
}

// Distribution-Erstellung
distributions {
    main {
        contents {
            from("${project(":merlin-webapp").projectDir}/build") {
                into("web")
            }
            from("${rootProject.projectDir}/examples") {
                into("examples")
            }
        }
    }
}

tasks.named("distZip") {
    dependsOn(":merlin-webapp:npmBuild")
}

tasks.register("dist") {
    dependsOn("distZip")
}

// Setzt Kotlin JVM-Zielversion
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}
