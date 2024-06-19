package com.connexal.ravelcraft.mod.server;

import com.connexal.ravelcraft.mod.cross.RavelModInstance;
import com.connexal.ravelcraft.mod.server.commands.impl.FabricCommandRegistrar;
import com.connexal.ravelcraft.mod.server.listeners.Listeners;
import com.connexal.ravelcraft.mod.server.managers.*;
import com.connexal.ravelcraft.mod.server.managers.geyser.GeyserManager;
import com.connexal.ravelcraft.mod.server.managers.npc.NpcManager;
import com.connexal.ravelcraft.mod.server.players.FabricPlayerManager;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.geysermc.api.Geyser;

public class RavelModServer implements ModInitializer {
	public static final String[] OVERRIDDEN_COMMANDS = new String[] { "ban", "ban-ip", "banlist", "pardon", "pardon-ip", "kick", "list", "whitelist", "msg", "tell" };

	private static MinecraftServer server = null;

	private static HomeManager homeManager;
	private static MiniBlockManager miniBlockManager;
	private static TpaManager tpaManager;
	private static Ravel1984Manager ravel1984Manager;
	private static SpawnManager spawnManager;
	private static GeyserManager geyserManager;

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
			throw new IllegalStateException("RavelModServer must be loaded on a server environment.");
		}

		RavelModInstance.setup();
		RavelInstance.setup();

		if (!Geyser.isRegistered()) {
			RavelInstance.getLogger().error("Geyser is not registered! Your server won't understand Bedrock clients!");
		}

		RavelInstance.init(new FabricCommandRegistrar(), new FabricPlayerManager());
		RavelModInstance.init();

		NpcManager.setup();
		homeManager = new HomeManager();
		miniBlockManager = new MiniBlockManager();
		tpaManager = new TpaManager();
		ravel1984Manager = Ravel1984Manager.create();
		spawnManager = new SpawnManager();
		geyserManager = new GeyserManager();

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
