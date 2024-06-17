package com.connexal.ravelcraft.mod.client.mixin;

import com.connexal.ravelcraft.mod.client.capes.CapeProvider;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Shadow @Final
    private GameProfile profile;

    @Inject(at = @At("HEAD"), method = "texturesSupplier")
    private static void loadTextures(GameProfile profile, CallbackInfoReturnable<Supplier<SkinTextures>> cir) {
        //CapeProvider.Companion.onLoadTexture(profile);
    }

    @Inject(at = @At("TAIL"), method = "getSkinTextures", cancellable = true)
    protected void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        /*CapeProvider handler = CapeProvider.Companion.fromProfile(this.profile);
        if (handler.getHasCape()) {
            SkinTextures oldTextures = cir.getReturnValue();
            Identifier capeTexture = handler.getCape();
            Identifier elytraTexture = handler.getHasElytraTexture() ? capeTexture : Identifier.of("textures/entity/elytra.png");
            SkinTextures newTextures = new SkinTextures(oldTextures.texture(), oldTextures.textureUrl(), capeTexture, elytraTexture, oldTextures.model(), oldTextures.secure());
            cir.setReturnValue(newTextures);
        }*/
    }
}
