package com.connexal.ravelcraft.mod.server.mixin;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    private PlayerManager playerManager = null;
    private RavelPlayer ravelPlayer = null;

    @Shadow public abstract ServerPlayerEntity getPlayer();

    private void updateData() {
        if (this.ravelPlayer == null) {
            this.playerManager = RavelInstance.getPlayerManager();
            this.ravelPlayer = this.playerManager.getPlayer(this.getPlayer().getUuid());

            if (this.ravelPlayer == null) {
                throw new RuntimeException("Player not found in player manager");
            }
        }
    }

    @Inject(method = "getSignedMessage", at = @At("RETURN"), cancellable = true)
    private void disableSecureChat(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<SignedMessage> cir) {
        this.updateData();

        this.playerManager.broadcast(com.connexal.ravelcraft.shared.util.text.Text.CHAT_FORMAT, this.ravelPlayer.displayName(), packet.chatMessage());

        cir.setReturnValue(null);
    }

    @Inject(at = @At("TAIL"), method = "onDisconnected")
    private void onPlayerLeave(Text reason, CallbackInfo info) {
        RavelInstance.getPlayerManager().playerLeft(this.getPlayer().getUuid());
    }
}
