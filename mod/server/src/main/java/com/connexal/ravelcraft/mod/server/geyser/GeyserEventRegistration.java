package com.connexal.ravelcraft.mod.server.geyser;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.event.Event;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.event.EventBus;
import org.geysermc.geyser.api.event.EventRegistrar;

import java.util.function.Consumer;

public class GeyserEventRegistration implements EventRegistrar {
    private final EventBus<EventRegistrar> eventBus;

    public GeyserEventRegistration() {
        this.eventBus = GeyserApi.api().eventBus();
        this.eventBus.register(this, this);
    }

    public <T extends Event> void register(Class<T> type, @NonNull Consumer<T> executor) {
        this.eventBus.subscribe(this, type, executor);
    }
}
