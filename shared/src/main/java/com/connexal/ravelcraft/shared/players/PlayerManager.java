package com.connexal.ravelcraft.shared.players;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Language;
import com.connexal.ravelcraft.shared.util.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PlayerManager {
    private final Map<UUID, PlayerInfo> playerInfo = new HashMap<>();
    private final Map<UUID, RavelPlayer> connectedPlayers = new HashMap<>();
    private final RavelConfig config;

    public PlayerManager() {
        this.config = RavelInstance.getConfig("players");
        this.config.save();
    }

    public RavelServer getServer(RavelPlayer player) {
        PlayerInfo info = this.playerInfo.get(player.getUniqueID());
        if (info == null) {
            return null;
        } else {
            return info.getServer();
        }
    }

    public void setServer(RavelPlayer player, RavelServer server) {
        if (server.isProxy()) {
            throw new IllegalArgumentException("Cannot set player server to a proxy server");
        }

        if (!this.playerInfo.containsKey(player.getUniqueID())) {
            return;
        }
        this.playerInfo.get(player.getUniqueID()).updateServer(server);

        // --- Java proxy specific code ---
        /*
        Optional<Player> optionalPlayer = JeProxy.getServer().getPlayer(player.getUniqueID());
        if (optionalPlayer.isEmpty()) {
            return;
        }

        RegisteredServer registeredServer = JeProxy.getServer().getServer(server.getName()).orElse(null);
        if (registeredServer == null) {
            return;
        }

        Player velocityPlayer = optionalPlayer.get();
        velocityPlayer.createConnectionRequest(registeredServer).connect().thenAcceptAsync((result) -> {
            if (result.isSuccessful()) {
                this.playerInfo.get(player.getUniqueID()).updateServer(server);
                //There is no need to notify the backend server of the change, as player will be disconnected from it
            } else {
                RavelInstance.getLogger().error("Failed to connect player " + player.getName() + " to server " + server.getName() + "!");
            }
        });
         */
    }

    public Language getLanguage(RavelPlayer player) {
        //TODO: Implement language getter
        return Language.DEFAULT;
    }

    public void setLanguage(RavelPlayer player, Language language) {
        //TODO: Implement language setter
    }

    public RavelRank getRank(RavelPlayer player) {
        //TODO: Implement rank getter
        return RavelRank.NONE;
    }

    public void setRank(RavelPlayer player, RavelRank rank) {
        //TODO: Implement rank setter
    }

    public void kick(RavelPlayer player, String reason, boolean network) {
        //TODO: Implement kick
    }

    public void broadcast(Text message) {
        //TODO: Implement broadcast
    }

    public int getOnlineCount() {
        return this.connectedPlayers.size();
    }

    //Utils

    public PlayerInfo getPlayerInfo(UUID uuid) {
        PlayerInfo info = this.playerInfo.get(uuid);
        if (info != null) {
            return info;
        }

        if (RavelInstance.getServer() == MessagingConstants.MESSAGING_SERVER) {
            String rankString = this.config.getString(uuid + ".rank");
            RavelRank rank;
            if (rankString == null) {
                rank = RavelRank.NONE;
            } else {
                try {
                    rank = RavelRank.valueOf(rankString);
                } catch (IllegalArgumentException e) {
                    rank = RavelRank.NONE;
                }
            }

            String languageString = this.config.getString(uuid + ".language");
            Language language;
            if (languageString == null) {
                language = Language.ENGLISH;
            } else {
                try {
                    language = Language.valueOf(languageString);
                } catch (IllegalArgumentException e) {
                    language = Language.DEFAULT;
                }
            }

            return new PlayerInfo(RavelServer.getLobby(), language, rank);
        } else {
            //TODO: Send a message to the messaging server to get the player info
            return null;
        }
    }

    public String[] getMessagePlayerInfo(UUID uuid) {
        PlayerInfo info = this.getPlayerInfo(uuid);

        return new String[] {info.getServer().name(), info.getLanguage().name(), info.getRank().name()};
    }

    public PlayerInfo getMessagePlayerInfo(String[] info) {
        try {
            RavelServer server = RavelServer.valueOf(info[0]);
            Language language = Language.valueOf(info[1]);
            RavelRank rank = RavelRank.valueOf(info[2]);

            return new PlayerInfo(server, language, rank);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    //Cache section

    public void playerJoined(RavelPlayer player) {
        UUID uuid = player.getUniqueID();
        this.connectedPlayers.put(uuid, player);
        this.playerInfo.put(uuid, this.getPlayerInfo(uuid));

        //TODO: Tell other proxy about the player
    }

    public void playerLeft(UUID uuid) {
        this.connectedPlayers.remove(uuid);
        this.playerInfo.remove(uuid);

        //TODO: Tell other proxy about the player
    }

    public static class PlayerInfo {
        private RavelServer server;
        private Language language;
        private RavelRank rank;

        public PlayerInfo(RavelServer server, Language language, RavelRank rank) {
            this.server = server;
            this.language = language;
            this.rank = rank;
        }

        public RavelServer getServer() {
            return server;
        }

        public void updateServer(RavelServer server) {
            this.server = server;
        }

        public Language getLanguage() {
            return language;
        }

        public void updateLanguage(Language language) {
            this.language = language;
        }

        public RavelRank getRank() {
            return rank;
        }

        public void updateRank(RavelRank rank) {
            this.rank = rank;
        }
    }
}
