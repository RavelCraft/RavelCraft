plugins {
    id("java")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

base.archivesName.set("${projectId}-shared-${minecraftVersion}")

dependencies {
    implementation("org.yaml:snakeyaml:2.0")
}