package com.connexal.ravelcraft.mod.server.listeners;

import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.players.RavelPlayer;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Random;

class ConnectionListener {
    static void register() {
        PlayerEvents.PRE_JOIN.register(ConnectionListener::onPlayerPreJoin);
        PlayerEvents.JOINED.register(ConnectionListener::onPlayerJoin);
        PlayerEvents.LEFT.register(ConnectionListener::onPlayerLeft);
    }

    private static boolean onPlayerPreJoin(ServerPlayerEntity player, ClientConnection connection) {
        if (!RavelInstance.getMessager().attemptConnect()) {
            connection.disconnect(Text.of("Network IPC connection establishment failed. Contact the server administrator."));
            return false;
        }

        return true;
    }

    private static void onPlayerJoin(RavelPlayer player) {
        RavelInstance.getPlayerManager().playerJoined(player);
        RavelInstance.getPlayerManager().applyPlayerRank(player, player.getRank());

        //Display some join info
        Random random = new Random();
        if (random.nextBoolean())  {
            switch (random.nextInt(3)) {
                case 0 -> player.sendMessage(RavelText.JOIN_INFO_RULES);
                case 1 -> player.sendMessage(RavelText.JOIN_INFO_ANNOUNCEMENTS);
                case 2 -> player.sendMessage(RavelText.JOIN_INFO_LANGUAGES);
            }
        }

        player.sendMessage(RavelText.PLAYER_DISPLAY_SERVER_NAME, RavelInstance.getServer().getName());
    }

    private static void onPlayerLeft(RavelPlayer player, String reason) {
        RavelInstance.getPlayerManager().playerLeft(player.getUniqueID());
    }
}
