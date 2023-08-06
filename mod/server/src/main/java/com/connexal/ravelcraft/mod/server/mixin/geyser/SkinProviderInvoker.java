package com.connexal.ravelcraft.mod.server.mixin.geyser;

import org.geysermc.geyser.skin.SkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;

@Mixin(SkinProvider.class)
public interface SkinProviderInvoker {
    @Invoker(value = "requestCape", remap = false)
    static CompletableFuture<SkinProvider.Cape> requestCape(String capeUrl, SkinProvider.CapeProvider provider, boolean newThread) {
        throw new AssertionError();
    }
}
