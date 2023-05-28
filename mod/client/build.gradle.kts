plugins {
	id("com.github.johnrengelman.shadow") version("7.1.2")
	id("idea")
	id("fabric-loom") version("1.2-SNAPSHOT")
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
	remapJar {
		dependsOn(shadowJar)
		inputFile.set(shadowJar.get().archiveFile)

		archiveClassifier.set("")
		archiveVersion.set("")
	}

	shadowJar {
		archiveClassifier.set("shadowed")
		dependsOn(":mod-cross:shadowJar")

		configurations = listOf(project.configurations.shadow.get())
	}

	jar {
		archiveClassifier.set("dev")
	}

	build {
		dependsOn(remapJar)
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
