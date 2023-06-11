package com.connexal.ravelcraft.shared.players;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Language;
import com.connexal.ravelcraft.shared.util.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class PlayerManager {
    private final Map<UUID, PlayerInfo> playerInfo = new HashMap<>();
    private final Map<UUID, RavelPlayer> connectedPlayers = new HashMap<>();
    private final RavelConfig config;
    private Messager messager = null;

    public PlayerManager() {
        this.config = RavelInstance.getConfig("players");
        this.config.save();
    }

    public void init() {
        this.messager = RavelInstance.getMessager();

        this.messager.registerCommandHandler(MessagingCommand.PLAYER_JOINED_PROXY, this::playerJoinedProxyCommand);
        this.messager.registerCommandHandler(MessagingCommand.PLAYER_LEFT_PROXY, this::playerLeftProxyCommand);
        this.messager.registerCommandHandler(MessagingCommand.PLAYER_GET_INFO, this::getPlayerInfoCommand);
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

    private PlayerInfo getPlayerInfo(UUID uuid) {
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
            CompletableFuture<String[]> future = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PLAYER_GET_INFO, uuid.toString());
            String[] infoString = future.join();
            if (infoString == null) {
                return null;
            }

            return this.getMessagePlayerInfo(infoString);
        }
    }

    private String[] getPlayerInfoCommand(RavelServer source, String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }

        return this.getMessagePlayerInfo(UUID.fromString(args[0]));
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
        this.playerJoinedInternal(player);

        //Tell the other proxy about the player
        if (RavelInstance.getServer().isProxy()) {
            RavelServer server;
            if (RavelInstance.getServer().isJavaProxy()) {
                server = RavelServer.BE_PROXY;
            } else {
                server = RavelServer.JE_PROXY;
            }

            this.messager.sendCommand(server, MessagingCommand.PLAYER_JOINED_PROXY, player.getUniqueID().toString(), player.getName());
        }
    }

    protected abstract String[] playerJoinedProxyCommand(RavelServer source, String[] args);

    protected void playerJoinedInternal(RavelPlayer player) {
        RavelInstance.getLogger().info("Player " + player.getName() + " joined the network");

        UUID uuid = player.getUniqueID();
        this.connectedPlayers.put(uuid, player);
        this.playerInfo.put(uuid, this.getPlayerInfo(uuid));
    }

    public void playerLeft(UUID uuid) {
        this.playerLeftInternal(uuid);

        //Tell the other proxy about the player
        if (RavelInstance.getServer().isProxy()) {
            RavelServer server;
            if (RavelInstance.getServer().isJavaProxy()) {
                server = RavelServer.BE_PROXY;
            } else {
                server = RavelServer.JE_PROXY;
            }

            this.messager.sendCommand(server, MessagingCommand.PLAYER_LEFT_PROXY, uuid.toString());
        }
    }

    protected abstract String[] playerLeftProxyCommand(RavelServer source, String[] args);

    protected void playerLeftInternal(UUID uuid) {
        RavelInstance.getLogger().info("Player " + this.connectedPlayers.get(uuid).getName() + " left the network");

        this.connectedPlayers.remove(uuid);
        this.playerInfo.remove(uuid);
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
