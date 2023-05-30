package com.connexal.ravelcraft.mod.server;

import com.connexal.ravelcraft.mod.server.commands.CommandRegistrarImpl;
import com.connexal.ravelcraft.mod.server.geyser.GeyserEventRegistration;
import com.connexal.ravelcraft.mod.server.players.PlayerManagerImpl;
import com.connexal.ravelcraft.mod.server.util.RavelLoggerImpl;
import com.connexal.ravelcraft.mod.server.velocity.VelocityModernForwarding;
import com.connexal.ravelcraft.shared.BuildConstants;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.RavelMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.geysermc.api.Geyser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class RavelModServer implements RavelMain, ModInitializer {
	private ModContainer mod;

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
		RavelInstance.setup(this, FabricLoader.getInstance().getConfigDir().resolve(BuildConstants.ID), new RavelLoggerImpl());
		RavelInstance.init(new CommandRegistrarImpl(), new PlayerManagerImpl());

		geyserEvents = new GeyserEventRegistration();

		if (!RavelInstance.getConfig().contains("forwarding-key")) {
			RavelInstance.getConfig().set("forwarding-key", "CHANGE ME");
			RavelInstance.getConfig().save();
			RavelInstance.getLogger().error("No forwarding key specified in config.yml! Please set one and restart the server.");
		}

		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
			RavelInstance.shutdown();
		});

		VelocityModernForwarding.init();
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
}
