package com.connexal.ravelcraft.mod.server.util.registry;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface RegistrySyncExtension<T> {
    void ravelcraft$setServerEntry(T obj, boolean value);
    boolean ravelcraft$isServerEntry(T obj);

    Status ravelcraft$getStatus();
    void ravelcraft$setStatus(Status status);
    boolean updateStatus(Status status);
    void ravelcraft$clearStatus();

    enum Status {
        VANILLA(0),
        WITH_SERVER_ONLY(1),
        WITH_MODDED(2);

        private final int priority;

        Status(int priority) {
            this.priority = priority;
        }
    }
}