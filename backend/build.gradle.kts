plugins {
	id("idea")
	id("fabric-loom") version("1.0-SNAPSHOT")
}

val projectId = project.property("project_id") as String
val minecraftVersion = project.property("minecraft_version") as String

val yarnMappings = project.property("yarn_mappings") as String
val fabricVersion = project.property("fabric_version") as String
val fabricLoaderVersion = project.property("fabric_loader_version") as String

base.archivesName.set("${projectId}-backend-${minecraftVersion}")

dependencies {
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	mappings("net.fabricmc:yarn:${yarnMappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

	implementation(project(":shared"))
}

loom {
	runs {
		named("client") {
			client()
			configName = "Fabric Client"
			ideConfigGenerated(true)
			runDir("run")
		}
		named("server") {
			server()
			configName = "Fabric Server"
			ideConfigGenerated(true)
			runDir("run")
		}
	}
}
