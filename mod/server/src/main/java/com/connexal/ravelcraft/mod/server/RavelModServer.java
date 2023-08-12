package com.connexal.ravelcraft.mod.server;

import com.connexal.ravelcraft.mod.cross.RavelModInstance;
import com.connexal.ravelcraft.mod.server.commands.impl.FabricCommandRegistrar;
import com.connexal.ravelcraft.mod.server.geyser.GeyserEventRegistration;
import com.connexal.ravelcraft.mod.server.managers.HomeManager;
import com.connexal.ravelcraft.mod.server.managers.MiniBlockManager;
import com.connexal.ravelcraft.mod.server.managers.TpaManager;
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
import net.minecraft.server.ServerTask;
import org.geysermc.api.Geyser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class RavelModServer implements RavelMain, ModInitializer {
	public static final String[] OVERRIDDEN_COMMANDS = new String[] { "ban", "ban-ip", "banlist", "pardon", "pardon-ip", "kick", "list", "whitelist" };

	private ModContainer mod;

	private static MinecraftServer server = null;
	private static GeyserEventRegistration geyserEvents;

	private static HomeManager homeManager;
	private static MiniBlockManager miniBlockManager;
	private static TpaManager tpaManager;

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

		if (!RavelInstance.getConfig().contains("forwarding-key")) {
			RavelInstance.getConfig().set("forwarding-key", "CHANGE ME");
			RavelInstance.getConfig().save();
			RavelInstance.getLogger().error("No forwarding key specified in config.yml! Please set one and restart the server.");
		}

		VelocityModernForwarding.init();

		homeManager = new HomeManager();
		miniBlockManager = new MiniBlockManager();
		tpaManager = new TpaManager();

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

	@Override
	public void runTask(Runnable runnable) {
		new Thread(runnable).start();
	}

	@Override
	public void runTask(Runnable runnable, int secondsDelay) {
		new Thread(() -> {
			try {
				Thread.sleep(1000L * secondsDelay);
			} catch (InterruptedException e) {
				RavelInstance.getLogger().error("Interrupted while waiting to run task", e);
			}
			runnable.run();
		}).start();
	}

	public static GeyserEventRegistration getGeyserEvents() {
		return geyserEvents;
	}

	public static MinecraftServer getServer() {
		return server;
	}


	public static HomeManager getHomeManager() {
		return homeManager;
	}

	public static MiniBlockManager getMiniBlockManager() {
		return miniBlockManager;
	}

	public static TpaManager getTpaManager() {
		return tpaManager;
	}
}
