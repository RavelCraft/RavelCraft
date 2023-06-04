import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version("7.1.2")
    id("java")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

val name = "${projectId}-crossProxy-${minecraftVersion}"
base.archivesName.set(name)

dependencies {
    implementation("com.google.auto.service:auto-service:1.0.1")
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")

    shadow(project(":shared"))
}

tasks {
    named<Jar>("jar") {
        archiveClassifier.set("unshaded")
        from(project.rootProject.file("LICENSE"))
    }
    val shadowJar = named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")

        configurations = listOf(project.configurations.shadow.get())
    }
    named("build") {
        dependsOn(shadowJar)
    }
}
