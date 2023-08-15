package com.connexal.ravelcraft.mod.server.mixin;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect")
    private void playerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        boolean out = PlayerEvents.PRE_JOIN.invoker().onPlayerPreJoin(player, connection);
        if (!out) {
            return;
        }

        FabricRavelPlayer ravelPlayer = new FabricRavelPlayer(player);
        PlayerEvents.JOINED.invoker().onPlayerJoined(ravelPlayer);
    }

    //Remove the "player joined the game" message
    @Inject(at = @At(value = "HEAD"), method = "broadcast(Lnet/minecraft/text/Text;Z)V", cancellable = true)
    private void filterJoinedGameMessages(Text message, boolean overlay, CallbackInfo info) {
        String key = ((TranslatableTextContent) message.getContent()).getKey();
        if (key.equals("multiplayer.player.joined.renamed") || key.equals("multiplayer.player.joined") || key.equals("multiplayer.player.left")) {
            info.cancel();
        }
    }
}
