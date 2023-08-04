package com.connexal.ravelcraft.mod.server;

import com.connexal.ravelcraft.mod.cross.RavelModInstance;
import com.connexal.ravelcraft.mod.server.commands.impl.FabricCommandRegistrar;
import com.connexal.ravelcraft.mod.server.geyser.GeyserEventRegistration;
import com.connexal.ravelcraft.mod.server.geyser.custom.GeyserCustomRegistration;
import com.connexal.ravelcraft.mod.server.players.FabricPlayerManager;
import com.connexal.ravelcraft.mod.server.util.FabricRavelLogger;
import com.connexal.ravelcraft.mod.server.velocity.VelocityModernForwarding;
import com.connexal.ravelcraft.shared.BuildConstants;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.RavelMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;
import org.geysermc.api.Geyser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class RavelModServer implements RavelMain, ModInitializer {
	private ModContainer mod;

	private static MinecraftServer server;
	private static GeyserEventRegistration geyserEvents;

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
			throw new IllegalStateException("RavelModServer must be loaded on a server environment.");
		}
		if (!Geyser.isRegistered()) {
			RavelInstance.getLogger().error("Geyser is not registered! Your server won't understand Bedrock clients!");
		}

		mod = FabricLoader.getInstance().getModContainer(BuildConstants.ID).orElseThrow();
		RavelInstance.setup(this, FabricLoader.getInstance().getConfigDir().resolve(BuildConstants.ID), new FabricRavelLogger());
		RavelModInstance.setup();

		RavelInstance.init(new FabricCommandRegistrar(), new FabricPlayerManager());
		RavelModInstance.init();

		geyserEvents = new GeyserEventRegistration();
		//GeyserCustomRegistration.setup();

		if (!RavelInstance.getConfig().contains("forwarding-key")) {
			RavelInstance.getConfig().set("forwarding-key", "CHANGE ME");
			RavelInstance.getConfig().save();
			RavelInstance.getLogger().error("No forwarding key specified in config.yml! Please set one and restart the server.");
		}

		VelocityModernForwarding.init();

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			RavelModServer.server = server;
		});

		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
			RavelInstance.shutdown();
		});
	}

	@Override
	public InputStream getResource(String name) {
		Path path = this.mod.findPath(name).orElse(null);
		if (path == null) {
			return null;
		}

		try {
			return path.getFileSystem()
					.provider()
					.newInputStream(path);
		} catch (IOException e) {
			return null;
		}
	}

	public static GeyserEventRegistration getGeyserEvents() {
		return geyserEvents;
	}

	public static MinecraftServer getServer() {
		return server;
	}
}
