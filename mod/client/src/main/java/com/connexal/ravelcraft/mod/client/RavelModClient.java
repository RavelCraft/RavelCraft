package com.connexal.ravelcraft.mod.client;

import com.connexal.ravelcraft.mod.cross.BuildConstants;
import com.connexal.ravelcraft.mod.cross.RavelModInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import javax.imageio.IIOException;
import java.net.URL;

public class RavelModClient implements ModInitializer {
	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
			throw new IllegalStateException("RavelModClient must be loaded on a client environment.");
		}

		RavelModInstance.setup();
		RavelModInstance.init();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			//Query https://ravelcraft.fr/api/version, if the version is different, display a warning

			try {
				URL url = new URL("https://ravelcraft.fr/api/version");
				String version = new String(url.openStream().readAllBytes());

				if (!version.equals(BuildConstants.VERSION)) {
					String message = "Your version of RavelCraft is outdated. Please update to v" + version + " at https://" + BuildConstants.SERVER_IP + "/. You can also click this message.";
					client.inGameHud.getChatHud().addMessage(Text.literal(message).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://" + BuildConstants.SERVER_IP + "/"))));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
