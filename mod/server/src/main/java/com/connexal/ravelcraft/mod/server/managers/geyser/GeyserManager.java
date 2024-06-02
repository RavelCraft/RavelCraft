package com.connexal.ravelcraft.mod.server.managers.geyser;

import com.connexal.ravelcraft.mod.server.managers.geyser.capes.CapeFetcher;
import com.connexal.ravelcraft.mod.server.managers.geyser.capes.CapeProvider;
import com.connexal.ravelcraft.mod.server.managers.geyser.ears.EarsFetcher;
import com.connexal.ravelcraft.mod.server.managers.geyser.ears.EarsProvider;
import com.connexal.ravelcraft.shared.RavelInstance;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.bedrock.SessionSkinApplyEvent;
import org.geysermc.geyser.api.event.java.ServerDefineCommandsEvent;
import org.geysermc.geyser.api.skin.Cape;
import org.geysermc.geyser.api.skin.Skin;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomBlocksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent;

public class GeyserManager implements EventRegistrar {
    public GeyserManager() {
        GeyserApi.api().eventBus().register(this, this);
        GeyserApi.api().eventBus().subscribe(this, ServerDefineCommandsEvent.class, this::onDefineCommands);
        GeyserApi.api().eventBus().subscribe(this, SessionSkinApplyEvent.class, this::onSkinApplyEvent);
        GeyserApi.api().eventBus().subscribe(this, GeyserDefineCustomItemsEvent.class, this::onDefineCustomItems);
        GeyserApi.api().eventBus().subscribe(this, GeyserDefineCustomBlocksEvent.class, this::onDefineCustomBlocks);
    }

    @Subscribe
    public void onDefineCommands(ServerDefineCommandsEvent event) {
        event.commands().removeIf(command -> command.name().startsWith("/"));
    }

    // Code from https://github.com/GeyserMC/ThirdPartyCosmetics
    @Subscribe
    public void onSkinApplyEvent(SessionSkinApplyEvent event) {
        Cape cape = Utils.getOrDefault(CapeFetcher.request(
                event.skinData().cape(), event.uuid(), event.username()
        ), event.skinData().cape(), CapeProvider.values().length * 3);

        if (!cape.failed() && cape != event.skinData().cape()) {
            RavelInstance.getLogger().info("Applied cape texture for " + event.username() + " (" + event.uuid() + ")");
            event.cape(cape);
        }

        // Let deadmau5 have his ears
        if ("deadmau5".equals(event.username())) {
            event.geometry(EarsFetcher.geometry(event.slim()));
            return;
        }

        // Get the ears texture for the player
        Skin skin = Utils.getOrDefault(EarsFetcher.request(
                event.skinData().skin(), event.uuid(), event.username()
        ), event.skinData().skin(), EarsProvider.VALUES.length * 3);

        // Does the skin have an ears texture
        if (skin != event.skinData().skin()) {
            RavelInstance.getLogger().info("Applied ears texture for " + event.username() + " (" + event.uuid() + ")");
            event.geometry(EarsFetcher.geometry(event.slim()));
            event.skin(skin);
        }
    }

    @Subscribe
    public void onDefineCustomItems(GeyserDefineCustomItemsEvent event) {

    }

    @Subscribe
    public void onDefineCustomBlocks(GeyserDefineCustomBlocksEvent event) {

    }
}
