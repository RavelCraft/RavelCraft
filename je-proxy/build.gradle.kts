plugins {
    id("java")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

base.archivesName.set("${projectId}-JEproxy-${minecraftVersion}")

dependencies {
    compileOnly("com.velocitypowered:velocity-api:4.0.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-annotation-processor:4.0.0-SNAPSHOT")

    implementation(project(":shared"))
}
