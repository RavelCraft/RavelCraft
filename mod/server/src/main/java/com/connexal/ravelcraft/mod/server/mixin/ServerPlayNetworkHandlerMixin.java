package com.connexal.ravelcraft.mod.server.mixin;

import com.connexal.ravelcraft.shared.RavelInstance;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public abstract ServerPlayerEntity getPlayer();

    @Inject(at = @At("TAIL"), method = "onChatMessage")
    private void chatMessageFormatting(ChatMessageC2SPacket packet, CallbackInfo callbackInfo) {
        //TODO: Format message with rank and whatnot
    }

    @Inject(at = @At("TAIL"), method = "onDisconnected")
    private void onPlayerLeave(Text reason, CallbackInfo info) {
        RavelInstance.getPlayerManager().playerLeft(this.getPlayer().getUuid());
    }
}
