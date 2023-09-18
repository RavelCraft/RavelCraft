package com.connexal.ravelcraft.mod.server.managers;

import com.connexal.ravelcraft.shared.RavelInstance;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.java.ServerDefineCommandsEvent;

public class GeyserManager implements EventRegistrar {
    public GeyserManager() {
        GeyserApi.api().eventBus().register(this, this);
        GeyserApi.api().eventBus().subscribe(this, ServerDefineCommandsEvent.class, this::onDefineCommands);
    }

    @Subscribe
    public void onDefineCommands(ServerDefineCommandsEvent event) {
        event.commands().removeIf(command -> {
            RavelInstance.getLogger().info("Command: " + command + " remove? " + command.name().startsWith("//"));
            return command.name().startsWith("//");
        });
    }
}
