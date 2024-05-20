package com.connexal.ravelcraft.mod.client.mixin;

import com.connexal.ravelcraft.mod.client.capes.CapeProvider;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {
    @Shadow @Final
    private GameProfile profile;

    @Inject(at = @At("TAIL"), method = "getSkinTextures", cancellable = true)
    protected void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        cir.setReturnValue(CapeProvider.loadCape(this.profile, cir.getReturnValue()));
    }
}
