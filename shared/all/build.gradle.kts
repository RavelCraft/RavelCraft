plugins {
    id("java")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

val snakeYmlVersion = project.property("snakeyaml_version") as String
val autoServiceVersion = project.property("auto_service_version") as String
val gsonVersion = project.property("gson_version") as String

base.archivesName.set("${projectId}-sharedAll-${minecraftVersion}")

dependencies {
    implementation("org.yaml:snakeyaml:${snakeYmlVersion}")
    implementation("com.google.auto.service:auto-service:${autoServiceVersion}")
    annotationProcessor("com.google.auto.service:auto-service:${autoServiceVersion}")
    implementation("com.google.code.gson:gson:${gsonVersion}")
}

sourceSets {
    main {
        resources {
            srcDirs.add(file("src/main/generated"))
        }
    }
}