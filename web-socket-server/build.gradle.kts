import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "5.1.0"
    application
}
group = "ca.alejandrobicelis"
version = "0.1"

repositories {
    mavenCentral()
}
dependencies {
    testImplementation(kotlin("test-junit"))

    // Java-WebSocket
    implementation (group= "org.java-websocket", name= "Java-WebSocket", version= "1.5.1")

    // Simple Logging Facade for Java
    implementation (group= "org.slf4j", name= "slf4j-simple", version= "1.7.30")

    // Apache commons
    implementation (group= "commons-io", name= "commons-io", version= "2.8.0")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}

tasks.withType<ShadowJar>() {
    val destination = File(project.rootDir.path + "/fatjar")
    destinationDirectory.set(destination)

    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}

tasks.withType<Delete>() {
    doFirst {
        File(project.rootDir.path + "/fatjar").deleteRecursively()
    }
}