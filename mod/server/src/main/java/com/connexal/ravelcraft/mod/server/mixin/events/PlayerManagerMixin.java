package com.connexal.ravelcraft.mod.server.mixin.events;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.util.text.Text;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void playerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        boolean out = PlayerEvents.PRE_JOIN.invoker().onPlayerPreJoin(player, connection);
        if (!out) {
            return;
        }

        FabricRavelPlayer ravelPlayer = new FabricRavelPlayer(player); //This is referenced in PlayerManagerMixin.md
        PlayerEvents.JOINED.invoker().onPlayerJoined(ravelPlayer);
    }

    //Remove the chat signature, send everything as the server
    @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params, CallbackInfo ci) {
        FabricRavelPlayer player = (FabricRavelPlayer) RavelInstance.getPlayerManager().getPlayer(sender.getUuid());

        boolean allowed = PlayerEvents.CHAT.invoker().onPlayerChat(player, message.getSignedContent());
        if (allowed) {
            RavelInstance.getPlayerManager().broadcast(Text.CHAT_FORMAT, player.displayName(), message.getSignedContent());
        }

        ci.cancel();
    }
}
