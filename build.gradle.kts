val projectId = project.property("project_id") as String
val projectName = project.property("project_name") as String
val projectVersion = project.property("project_version") as String

allprojects {
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<ProcessResources> {
        filesMatching(listOf("fabric.mod.json", "plugin.yml")) {
            expand(mapOf(
                "project_id" to projectId,
                "project_name" to projectName,
                "project_version" to projectVersion,
            ))
        }
    }

    repositories {
        mavenCentral()

        maven("https://jitpack.io") {
            content {
                includeGroupByRegex("com\\.github\\..*")
            }
        }

        maven("https://repo.opencollab.dev/main/")
        maven("https://repo.opencollab.dev/maven-snapshots/")

        maven("https://repo.waterdog.dev/artifactory/main/")

        maven("https://repo.papermc.io/repository/maven-public/")

        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}
