package com.connexal.ravelcraft.mod.server.mixin.registry;

import com.connexal.ravelcraft.mod.server.util.registry.RegistrySyncExtension;
import com.connexal.ravelcraft.mod.server.util.registry.RegistrySyncUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> implements RegistrySyncExtension<T>, MutableRegistry<T> {
    @Unique
    private final Object2BooleanMap<T> ravelcraft$entryStatus = new Object2BooleanOpenHashMap<>();

    @Unique
    private Status ravelcraft$status = null;

    @Shadow
    public abstract Set<Identifier> getIds();

    @Inject(method = "add", at = @At("TAIL"))
    private <V extends T> void polymer_registry_sync$resetStatus(RegistryKey<T> key, T value, RegistryEntryInfo info, CallbackInfoReturnable<RegistryEntry.Reference<T>> cir) {
        this.ravelcraft$status = null;
    }

    @Override
    public Status ravelcraft$getStatus() {
        if (this.ravelcraft$status == null) {
            var status = Status.VANILLA;
            for (var id : this.getIds()) {
                if (id.getNamespace().equals("minecraft") || id.getNamespace().equals("brigadier")) {
                    continue;
                }

                if (RegistrySyncUtils.isServerEntry(this, id)) {
                    status = Status.WITH_SERVER_ONLY;
                } else {
                    status = Status.WITH_MODDED;
                    break;
                }
            }

            this.ravelcraft$status = status;
        }

        return this.ravelcraft$status;
    }

    @Override
    public void ravelcraft$setStatus(Status status) {
        this.ravelcraft$status = status;
    }

    @Override
    public void ravelcraft$clearStatus() {
        this.ravelcraft$status = null;
    }

    @Override
    public boolean ravelcraft$isServerEntry(T obj) {
        return this.ravelcraft$entryStatus.getBoolean(obj);
    }

    @Override
    public void ravelcraft$setServerEntry(T obj, boolean value) {
        this.ravelcraft$status = null;
        this.ravelcraft$entryStatus.put(obj, value);
    }
}
