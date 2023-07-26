package com.connexal.ravelcraft.mod.server.mixin;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.players.RavelRank;
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

        RavelPlayer ravelPlayer = new FabricRavelPlayer(player);
        RavelInstance.getPlayerManager().playerJoined(ravelPlayer);

        RavelRank rank = ravelPlayer.getRank();
        if (rank.isOperator()) {
            RavelModServer.getServer().getPlayerManager().addToOperators(player.getGameProfile());
        } else {
            RavelModServer.getServer().getPlayerManager().removeFromOperators(player.getGameProfile());
        }
    }
}
