plugins {
    id("java")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

val gsonVersion = project.property("gson_version") as String

base.archivesName.set("${projectId}-sharedServer-${minecraftVersion}")

dependencies {
    implementation("com.google.code.gson:gson:${gsonVersion}")

    implementation(project(":shared-all"))
}
