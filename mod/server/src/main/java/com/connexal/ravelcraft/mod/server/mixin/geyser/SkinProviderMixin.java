package com.connexal.ravelcraft.mod.server.mixin.geyser;

import com.connexal.ravelcraft.shared.BuildConstants;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.skin.SkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(SkinProvider.class)
public class SkinProviderMixin {
    @Inject(at = @At(value = "TAIL"), method = "requestUnofficialCape", remap = false, cancellable = true)
    private static void getCape(SkinProvider.Cape officialCape, UUID playerId, String username, boolean newThread, CallbackInfoReturnable<CompletableFuture<SkinProvider.Cape>> info) {
        if (officialCape.failed() && GeyserImpl.getInstance().getConfig().isAllowThirdPartyCapes()) {
            CompletableFuture<SkinProvider.Cape> capeFuture = SkinProviderInvoker.requestCape("http://db." + BuildConstants.SERVER_IP + "/capes/" + playerId.toString() + ".png", SkinProvider.CapeProvider.LABYMOD, newThread);

            SkinProvider.Cape cape;
            try {
                cape = capeFuture.get(4, TimeUnit.SECONDS);
            } catch (Exception e) {
                return;
            }

            if (!cape.failed()) {
                info.setReturnValue(CompletableFuture.completedFuture(cape));
            }
        }
    }
}
