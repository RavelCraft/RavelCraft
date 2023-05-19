package com.connexal.ravelmod;

import com.connexal.ravelcraft.shared.BuildConstants;
import com.connexal.ravelcraft.shared.RavelInstance;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RavelMod implements ModInitializer {
	@Override
	public void onInitialize() {
		RavelInstance.init(new RavelLoggerImpl());
	}
}
