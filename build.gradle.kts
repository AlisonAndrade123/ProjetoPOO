// Import necessário para configurar a tarefa ShadowJar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    // Plugin para criar o Fat JAR
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "br.edu.ifpb.lojavirtual"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("br.edu.ifpb.lojavirtual.Launcher")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    implementation("com.github.librepdf:openpdf:1.3.30")
    implementation("org.slf4j:slf4j-simple:1.7.32")

    val junitVersion = "5.10.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("simulador-loja-poo")
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())

    manifest {
        attributes("Main-Class" to "br.edu.ifpb.lojavirtual.Launcher")
    }
}