package com.connexal.ravelcraft.mod.server.mixin;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(at = @At("TAIL"), method = "onChatMessage")
    private void chatMessageFormatting(ChatMessageC2SPacket packet, CallbackInfo callbackInfo ) {
        //TODO: Format message with rank and whatnot
    }
}
