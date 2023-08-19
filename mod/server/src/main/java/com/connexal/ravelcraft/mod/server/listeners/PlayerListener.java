package com.connexal.ravelcraft.mod.server.listeners;

import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.text.Text;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerListener {
    public static void register() {
        PlayerEvents.PRE_JOIN.register(PlayerListener::onPlayerPreJoin);
        PlayerEvents.JOINED.register(PlayerListener::onPlayerJoin);
        PlayerEvents.LEFT.register(PlayerListener::onPlayerLeft);
    }

    private static boolean onPlayerPreJoin(ServerPlayerEntity player, ClientConnection connection) {
        if (!RavelInstance.getMessager().attemptConnect()) {
            connection.disconnect(net.minecraft.text.Text.of("Network IPC connection establishment failed. Contact the server administrator."));
            return false;
        }

        return true;
    }

    private static void onPlayerJoin(RavelPlayer player) {
        RavelInstance.getPlayerManager().playerJoined(player);
        RavelInstance.getPlayerManager().applyPlayerRank(player, player.getRank());

        player.sendMessage(Text.PLAYER_DISPLAY_SERVER_NAME, RavelInstance.getServer().getName());
    }

    private static void onPlayerLeft(RavelPlayer player, String reason) {
        RavelInstance.getPlayerManager().playerLeft(player.getUniqueID());
    }
}
