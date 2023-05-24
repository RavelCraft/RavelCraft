package com.connexal.ravelcraft.shared.commands;

import java.util.ServiceLoader;

public abstract class CommandRegistrar {
    public void register() {
        for (RavelCommand command : ServiceLoader.load(RavelCommand.class)) {
            this.register(command);
        }
    }

    protected abstract void register(RavelCommand command);
}
