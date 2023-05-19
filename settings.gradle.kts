dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()

        gradlePluginPortal()

        // Minecraft
        maven("https://libraries.minecraft.net") {
            name = "minecraft"
            mavenContent {
                releasesOnly()
            }
        }

        // Fabric
        maven("https://maven.fabricmc.net/")

        // Sponge Snapshots
        maven("https://repo.spongepowered.org/repository/maven-public/")

        // MCProtocolLib
        maven("https://jitpack.io") {
            content {
                includeGroupByRegex("com\\.github\\..*")
            }
        }

        // For Adventure snapshots
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://maven.fabricmc.net/")
    }
}

rootProject.name = "ravelcraft-parent"

include(":shared")

include(":backend", ":je-proxy", ":be-proxy")
