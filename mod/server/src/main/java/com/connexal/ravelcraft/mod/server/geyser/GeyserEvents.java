package com.connexal.ravelcraft.mod.server.geyser;

import com.connexal.ravelcraft.shared.RavelInstance;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.bedrock.SessionInitializeEvent;

public class GeyserEvents implements EventRegistrar {
    public GeyserEvents() {
        GeyserApi.api().eventBus().register(this, this);
        GeyserApi.api().eventBus().subscribe(this, SessionInitializeEvent.class, this::geyserPlayerJoin);
    }

    @Subscribe
    public void geyserPlayerJoin(SessionInitializeEvent event) {
        GeyserConnection connection = event.connection();
        RavelInstance.getLogger().info(connection.name() + " logged in (" + connection.javaUuid() + ", " + connection.javaUsername() + ")");
    }
}
