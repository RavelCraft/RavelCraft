package com.connexal.ravelmod;

import com.connexal.ravelcraft.shared.BuildConstants;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RavelMod implements ModInitializer {
	private static final Logger logger = LoggerFactory.getLogger(BuildConstants.ID);

	@Override
	public void onInitialize() {
		logger.info("Initialised " + BuildConstants.NAME);
	}
}
