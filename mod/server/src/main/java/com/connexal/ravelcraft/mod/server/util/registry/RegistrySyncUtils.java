package com.connexal.ravelcraft.mod.server.util.registry;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class RegistrySyncUtils {
    public static <T> boolean isServerEntry(Registry<T> registry, T entry) {
        if (registry instanceof RegistrySyncExtension<?>) {
            return ((RegistrySyncExtension<T>) registry).ravelcraft$isServerEntry(entry);
        } else {
            return false;
        }
    }

    public static <T> boolean isServerEntry(Registry<T> registry, Identifier identifier) {
        return registry.containsId(identifier) ? isServerEntry(registry, registry.get(identifier)) : false;
    }

    public static <T> void setServerEntry(Registry<T> registry, T entry) {
        if (registry instanceof RegistrySyncExtension<?>) {
            ((RegistrySyncExtension<T>) registry).ravelcraft$setServerEntry(entry, true);
        }
    }

    public static <T> void setServerEntry(Registry<T> registry, Identifier identifier) {
        if (registry.containsId(identifier)) {
            setServerEntry(registry, registry.get(identifier));
        } else {
            throw new IllegalArgumentException("Entry '" + identifier + "' of registry '" + registry.getKey().getValue() + "' isn't registered!");
        }
    }
}