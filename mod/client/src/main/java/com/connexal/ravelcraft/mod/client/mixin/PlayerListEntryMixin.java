package com.connexal.ravelcraft.mod.client.mixin;

import com.connexal.ravelcraft.mod.client.capes.CapeProvider;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Shadow @Final
    private GameProfile profile;
    @Shadow @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;
    @Shadow
    private boolean texturesLoaded;

    // Note that loadTextures()V might be called like a foxton, so rejecting to run it has to be really fast
    @Inject(at = @At("HEAD"), method = "loadTextures()V")
    protected void loadTextures(CallbackInfo info) {
        if (this.texturesLoaded) {
            return;
        }

        CapeProvider.loadCape(this.profile, id -> {
            this.textures.putIfAbsent(MinecraftProfileTexture.Type.CAPE, id);
        });
    }
}
