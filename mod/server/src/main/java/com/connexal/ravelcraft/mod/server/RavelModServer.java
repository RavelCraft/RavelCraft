package com.connexal.ravelcraft.mod.server;

import com.connexal.ravelcraft.mod.cross.RavelModInstance;
import com.connexal.ravelcraft.mod.server.commands.impl.FabricCommandRegistrar;
import com.connexal.ravelcraft.mod.server.listeners.Listeners;
import com.connexal.ravelcraft.mod.server.managers.*;
import com.connexal.ravelcraft.mod.server.managers.npc.NpcManager;
import com.connexal.ravelcraft.mod.server.players.FabricPlayerManager;
import com.connexal.ravelcraft.mod.server.players.velocity.VelocityModernForwarding;
import com.connexal.ravelcraft.mod.server.util.FabricRavelLogger;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RavelModServer implements RavelMain, ModInitializer {
	public static final String[] OVERRIDDEN_COMMANDS = new String[] { "ban", "ban-ip", "banlist", "pardon", "pardon-ip", "kick", "list", "whitelist" };

	private ModContainer mod;

	private static MinecraftServer server = null;
	private static ScheduledExecutorService scheduler;

	private static HomeManager homeManager;
	private static MiniBlockManager miniBlockManager;
	private static TpaManager tpaManager;
	private static Ravel1984Manager ravel1984Manager;
	private static SpawnManager spawnManager;

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
			throw new IllegalStateException("RavelModServer must be loaded on a server environment.");
		}
		if (!Geyser.isRegistered()) {
			RavelInstance.getLogger().error("Geyser is not registered! Your server won't understand Bedrock clients!");
		}

		scheduler = Executors.newScheduledThreadPool(3);

		mod = FabricLoader.getInstance().getModContainer(BuildConstants.ID).orElseThrow();
		RavelInstance.setup(this, FabricLoader.getInstance().getConfigDir().resolve(BuildConstants.ID), new FabricRavelLogger());
		RavelModInstance.setup();

		RavelInstance.init(new FabricCommandRegistrar(), new FabricPlayerManager());
		RavelModInstance.init();

		VelocityModernForwarding.init();

		NpcManager.setup();
		homeManager = new HomeManager();
		miniBlockManager = new MiniBlockManager();
		tpaManager = new TpaManager();
		ravel1984Manager = Ravel1984Manager.create();
		spawnManager = new SpawnManager();

		Listeners.register();

		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			RavelModServer.server = server;
		});

		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
			if (ravel1984Manager != null) {
				ravel1984Manager.flushCache();
			}

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
	public void scheduleTask(Runnable runnable) {
		scheduler.execute(runnable);
	}

	@Override
	public void scheduleTask(Runnable runnable, int secondsDelay) {
		scheduler.schedule(runnable, secondsDelay, TimeUnit.SECONDS);
	}

	@Override
	public void scheduleRepeatingTask(Runnable runnable, int secondsInterval) {
		scheduler.scheduleAtFixedRate(runnable, 0, secondsInterval, TimeUnit.SECONDS);
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

	public static Ravel1984Manager getRavel1984Manager() {
		return ravel1984Manager;
	}

	public static SpawnManager getSpawnManager() {
		return spawnManager;
	}
}
