package com.connexal.ravelcraft.mod.client;

import com.connexal.ravelcraft.mod.cross.RavelModInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class RavelModClient implements ModInitializer {
	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
			throw new IllegalStateException("RavelModServer must be loaded on a client environment.");
		}

		RavelModInstance.setup();
		RavelModInstance.init();
	}
}
