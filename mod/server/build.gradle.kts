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

val geyserVersion = project.property("geyser_version") as String

val builtJarName = "${projectId}-serverMod-${minecraftVersion}"
base.archivesName.set(builtJarName)

dependencies {
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings("net.fabricmc:yarn:${yarnMappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

	implementation("com.google.auto.service:auto-service:1.0.1")
	annotationProcessor("com.google.auto.service:auto-service:1.0.1")

	implementation("org.geysermc.geyser:core:${geyserVersion}")
	implementation("org.geysermc.geyser:api:${geyserVersion}")

	shadow(project(":shared"))
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
		named("server") {
			server()
			configName = "Fabric Server"
			ideConfigGenerated(true)
			runDir("run")
		}
	}
}
