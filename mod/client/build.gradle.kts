import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

plugins {
	id("com.github.johnrengelman.shadow") version("7.1.2")
	id("idea")
	id("fabric-loom") version("1.0-SNAPSHOT")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

val yarnMappings = project.property("yarn_mappings") as String
val fabricVersion = project.property("fabric_version") as String
val fabricLoaderVersion = project.property("fabric_loader_version") as String

val name = "${projectId}-clientMod-${minecraftVersion}"
base.archivesName.set(name)

dependencies {
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings("net.fabricmc:yarn:${yarnMappings}:v2")
	modCompileOnly("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
	modCompileOnly("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

	shadow(project(":mod-cross"))
}

tasks {
	named<Jar>("jar") {
		archiveClassifier.set("unshaded")
		from(project.rootProject.file("LICENSE"))
	}
	val shadowJar = named<ShadowJar>("shadowJar") {
		archiveClassifier.set("")

		configurations = listOf(project.configurations.shadow.get())
	}
	val remapJar = named<RemapJarTask>("remapJar") {
		dependsOn(shadowJar)
		mustRunAfter(shadowJar)
		archiveBaseName.set(name)
	}
	named("build") {
		dependsOn(shadowJar)
	}
}

loom {
	runs {
		named("client") {
			client()
			configName = "Fabric Client"
			ideConfigGenerated(true)
			runDir("run")
		}
	}
}
