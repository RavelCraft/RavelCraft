import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("com.github.johnrengelman.shadow") version("7.1.2")
    id("idea")
    id("fabric-loom") version("1.6-SNAPSHOT")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

val yarnMappings = project.property("yarn_mappings") as String
val fabricVersion = project.property("fabric_version") as String
val fabricLoaderVersion = project.property("fabric_loader_version") as String

val name = "${projectId}-crossMod-${minecraftVersion}"
base.archivesName.set(name)

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings("net.fabricmc:yarn:${yarnMappings}:v2")

    shadow(project(":shared-all"))

    if (project.hasProperty("runDatagen")) { //To run: ./gradlew runDatagen -PrunDatagen
        println("Data generation detected and enabled")

        modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

        implementation(project(":shared-all"))
    } else {
        modCompileOnly("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
        modCompileOnly("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
    }
}

tasks {
    named<Jar>("jar") {
        archiveClassifier.set("unshaded")
    }
    val shadowJar = named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")

        configurations = listOf(project.configurations.shadow.get())

        from(project.rootProject.file("LICENSE"))
        from(file("src/main/generated"))
    }
    val remapJar = named<RemapJarTask>("remapJar") {
        enabled = false
    }
    named("build") {
        dependsOn(shadowJar)
    }
}

loom {
    runs {
        create("datagen") {
            client()
            configName = "Data Generation"
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/main/generated")}")
            vmArg("-Dfabric-api.datagen.modid=$projectId")
            ideConfigGenerated(true)
            runDir("build/datagen")
        }
    }
}

sourceSets {
    main {
        resources {
            srcDirs.add(file("src/main/generated"))
        }
    }
}
