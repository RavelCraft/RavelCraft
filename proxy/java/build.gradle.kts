import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version("7.1.2")
    id("java")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String
val autoServiceVersion = project.property("auto_service_version") as String
val velocityVersion = project.property("velocity_version") as String

val name = "${projectId}-javaProxy-${minecraftVersion}"
base.archivesName.set(name)

dependencies {
    compileOnly("com.velocitypowered:velocity-api:${velocityVersion}")
    annotationProcessor("com.velocitypowered:velocity-api:${velocityVersion}")

    implementation("com.google.auto.service:auto-service:${autoServiceVersion}")
    annotationProcessor("com.google.auto.service:auto-service:${autoServiceVersion}")


    shadow(project(":shared-all"))
    shadow(project(":shared-server"))
    shadow(project(":proxy-cross"))
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
