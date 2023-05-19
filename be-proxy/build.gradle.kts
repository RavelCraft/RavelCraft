plugins {
    id("java")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

base.archivesName.set("${projectId}-BEproxy-${minecraftVersion}")

dependencies {
    compileOnly("dev.waterdog.waterdogpe:waterdog:2.0.0-SNAPSHOT")

    implementation(project(":shared"))
}
