package com.connexal.ravelcraft.mod.server.mixin;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.events.ItemEvents;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.PlayerManager;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    private PlayerManager playerManager = null;
    private FabricRavelPlayer ravelPlayer = null;

    @Shadow
    public abstract ServerPlayerEntity getPlayer();

    private boolean updateData() {
        if (this.ravelPlayer == null) {
            this.playerManager = RavelInstance.getPlayerManager();
            this.ravelPlayer = (FabricRavelPlayer) this.playerManager.getPlayer(this.getPlayer().getUuid());
            return this.ravelPlayer != null;
        }

        return true;
    }


    //Remove the chat signature, send everything as the server
    @Inject(method = "getSignedMessage", at = @At("RETURN"), cancellable = true)
    private void disableSecureChat(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<SignedMessage> cir) {
        this.updateData();

        String message = packet.chatMessage();
        this.playerManager.broadcast(Text.CHAT_FORMAT, this.ravelPlayer.displayName(), message);
        PlayerEvents.CHAT.invoker().onPlayerChat(this.ravelPlayer, message);

        cir.setReturnValue(null);
    }

    @Inject(at = @At("TAIL"), method = "onDisconnected")
    private void onPlayerLeave(net.minecraft.text.Text reason, CallbackInfo ci) {
        if (this.updateData()) {
            PlayerEvents.LEFT.invoker().onPlayerLeft(this.ravelPlayer, reason.getString());
        } else {
            throw new RuntimeException("Player left without being initialized... Something is wrong!");
        }
    }

    @Inject(at = @At("TAIL"), method = "onCommandExecution")
    private void onCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        if (this.updateData()) {
            PlayerEvents.COMMAND.invoker().onPlayerCommandPreprocess(this.ravelPlayer, packet.command());
        }
    }

    @Inject(at = @At("HEAD"), method = "requestTeleport(DDDFFLjava/util/Set;)V")
    private void onTeleport(double x, double y, double z, float yaw, float pitch, Set<PositionFlag> flags, CallbackInfo ci) {
        if (this.updateData()) {
            PlayerEvents.TELEPORT.invoker().onPlayerTeleport(this.ravelPlayer, this.getPlayer().getWorld(), x, y, z, yaw, pitch);
        }
    }
}
