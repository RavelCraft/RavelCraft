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

val geyserVersion = project.property("geyser_version") as String
val sguiVersion = project.property("sgui_version") as String
val autoServiceVersion = project.property("auto_service_version") as String

val builtJarName = "${projectId}-serverMod-${minecraftVersion}"
base.archivesName.set(builtJarName)

dependencies {
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings("net.fabricmc:yarn:${yarnMappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

	implementation("com.google.auto.service:auto-service:${autoServiceVersion}")
	annotationProcessor("com.google.auto.service:auto-service:${autoServiceVersion}")

	implementation("org.geysermc.geyser:api:${geyserVersion}")

	modImplementation("eu.pb4:sgui:${sguiVersion}")

	shadow(project(":shared-all"))
	shadow(project(":shared-server"))
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
			ideConfigGenerated(false)
			runDir("run")
		}
	}
}
