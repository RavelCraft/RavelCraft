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

        // For SGUI
        maven("https://maven.nucleoid.xyz")
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

include(":shared", ":mod-cross", ":proxy-cross")
include(":proxy-bedrock", ":proxy-java", ":mod-server", ":mod-client")

project(":proxy-cross").projectDir = file("proxy/cross")
project(":proxy-bedrock").projectDir = file("proxy/bedrock")
project(":proxy-java").projectDir = file("proxy/java")

project(":mod-cross").projectDir = file("mod/cross")
project(":mod-server").projectDir = file("mod/server")
project(":mod-client").projectDir = file("mod/client")