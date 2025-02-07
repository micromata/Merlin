plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    signing
}

description = "merlin-core"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.commons.io)
    api(libs.commons.lang3)
    api(libs.commons.beanutils)
    api(libs.poi)
    api(libs.poi.ooxml)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation(libs.mockito.core)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "merlin-core"
            from(components["java"])
            pom {
                name.set("Merlin core")
                description.set("A Java library for working with Excel and Word including validation, manipulation and templating.")
                url.set("https://github.com/micromata/merlin")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/micromata/merlin/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("kreinhard")
                        name.set("Kai Reinhard")
                    }
                }
                scm {
                    connection.set("scm:git:git://example.com/my-library.git")
                    developerConnection.set("scm:git:ssh://example.com/my-library.git")
                    url.set("https://example.com/my-library/")
                }
            }
        }
    }
    /* // Uncomment this block ONLY for publishing on Sonatype! Don't commit the uncommented block.
    repositories {
        maven {
            credentials {
                username = mavenUser
                password = mavenPassword
            }
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (version.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        }
    } */
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}
