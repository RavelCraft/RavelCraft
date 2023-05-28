package com.connexal.ravelcraft.mod.server.mixin;

import com.connexal.ravelcraft.shared.RavelInstance;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void playerJoinChecks(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        if (!RavelInstance.getMessager().attemptConnect()) {
            connection.disconnect(Text.of("Network IPC connection establishment failed. Contact the server administrator."));
            return;
        }

        //TODO: Tell player manager about the join
    }
}
