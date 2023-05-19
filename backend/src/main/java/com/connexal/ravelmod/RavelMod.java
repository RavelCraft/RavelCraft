package com.connexal.ravelmod;

import com.connexal.ravelcraft.shared.BuildConstants;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelmod.util.RavelLoggerImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class RavelMod implements ModInitializer {
	@Override
	public void onInitialize() {
		RavelInstance.init(new RavelLoggerImpl(), FabricLoader.getInstance().getConfigDir().resolve(BuildConstants.ID));
	}
}
