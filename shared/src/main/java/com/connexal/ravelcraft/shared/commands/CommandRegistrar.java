package com.connexal.ravelcraft.shared.commands;

import com.connexal.ravelcraft.shared.RavelInstance;

import java.util.ServiceLoader;

public abstract class CommandRegistrar {
    private final ClassLoader loader;

    public CommandRegistrar(ClassLoader loader) {
        this.loader = loader;
    }

    public void register() {
        int count = 0;

        for (RavelCommand command : ServiceLoader.load(RavelCommand.class, this.loader)) {
            this.register(command);
            count++;
        }

        RavelInstance.getLogger().info("Registered " + count + " commands!");
    }

    protected abstract void register(RavelCommand command);
}
