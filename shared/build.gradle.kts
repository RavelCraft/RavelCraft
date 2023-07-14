plugins {
    id("java")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

base.archivesName.set("${projectId}-shared-${minecraftVersion}")

dependencies {
    implementation("org.yaml:snakeyaml:2.0")
    implementation("com.google.auto.service:auto-service:1.0.1")
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
}
