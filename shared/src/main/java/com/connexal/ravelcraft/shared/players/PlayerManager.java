package com.connexal.ravelcraft.shared.players;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.server.RavelServer;
import com.connexal.ravelcraft.shared.util.text.Language;
import com.connexal.ravelcraft.shared.util.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class PlayerManager {
    protected final Map<UUID, PlayerInfo> playerInfo = new HashMap<>();
    protected final Map<UUID, RavelPlayer> connectedPlayers = new HashMap<>();
    private final RavelConfig config;

    protected Messager messager = null;

    public PlayerManager() {
        this.config = RavelInstance.getConfig("players");
        this.config.save();
    }

    public void init() {
        this.messager = RavelInstance.getMessager();

        this.messager.registerCommandHandler(MessagingCommand.PLAYER_GET_INFO, this::getPlayerInfoCommand);

        this.messager.registerCommandHandler(MessagingCommand.PLAYER_RANK_UPDATE, this::setRankCommand);
        this.messager.registerCommandHandler(MessagingCommand.PLAYER_LANGUAGE_UPDATE, this::setLanguageCommand);
    }

    //This could also be a reconnect
    public abstract void messagingConnected(RavelServer server);

    public RavelServer getServer(RavelPlayer player) {
        if (!this.playerInfo.containsKey(player.getUniqueID())) {
            return null;
        }

        return this.playerInfo.get(player.getUniqueID()).getServer();
    }

    public void setServer(RavelPlayer player, RavelServer server) {
        if (server.isProxy()) {
            throw new IllegalArgumentException("Cannot set player server to a proxy server");
        }
        if (server == RavelInstance.getServer()) {
            throw new IllegalArgumentException("Cannot set player server to the current server");
        }

        boolean success = this.setServerInternal(player, server);
        if (!success) {
            return;
        }

        if (!this.playerInfo.containsKey(player.getUniqueID())) {
            return;
        }
        this.playerInfo.get(player.getUniqueID()).updateServer(server);
    }

    protected abstract boolean setServerInternal(RavelPlayer player, RavelServer server);

    public Language getLanguage(RavelPlayer player) {
        if (!this.playerInfo.containsKey(player.getUniqueID())) {
            return Language.DEFAULT;
        }

        return this.playerInfo.get(player.getUniqueID()).getLanguage();
    }

    public void setLanguage(RavelPlayer player, Language language) {
        if (!this.playerInfo.containsKey(player.getUniqueID())) {
            return;
        }

        RavelServer playerServer = this.getServer(player);

        for (RavelServer server : RavelServer.values()) {
            if (server == RavelInstance.getServer()) { //We don't need to send a message to ourselves
                continue;
            }

            if (server.isProxy() || server == playerServer) {
                CompletableFuture<String[]> returnData = this.messager.sendCommandWithResponse(server, MessagingCommand.PLAYER_LANGUAGE_UPDATE, player.getUniqueID().toString(), language.name());
                if (returnData == null) {
                    RavelInstance.getLogger().error("Failed to update player language for " + player.getName() + "!");
                    return;
                }
                String[] response = returnData.join();

                if (response.length != 1 || !response[0].equals(MessagingConstants.COMMAND_SUCCESS)) {
                    RavelInstance.getLogger().error("Failed to update player language for " + player.getName() + "!");
                    return;
                }
            }
        }

        this.playerInfo.get(player.getUniqueID()).updateLanguage(language);
    }

    private String[] setLanguageCommand(RavelServer source, String[] args) {
        if (args.length != 2) {
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        UUID uuid = UUID.fromString(args[0]);
        Language language;
        try {
            language = Language.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        PlayerInfo info = this.playerInfo.get(uuid);
        if (info == null) {
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        info.updateLanguage(language);

        return new String[] {MessagingConstants.COMMAND_SUCCESS};
    }

    public RavelRank getRank(RavelPlayer player) {
        if (!this.playerInfo.containsKey(player.getUniqueID())) {
            return RavelRank.NONE;
        }

        return this.playerInfo.get(player.getUniqueID()).getRank();
    }

    public void setRank(RavelPlayer player, RavelRank rank) {
        if (!this.playerInfo.containsKey(player.getUniqueID())) {
            return;
        }

        RavelServer playerServer = this.getServer(player);

        for (RavelServer server : RavelServer.values()) {
            if (server == RavelInstance.getServer()) { //We don't need to send a message to ourselves
                continue;
            }

            if (server.isProxy() || server == playerServer) {
                CompletableFuture<String[]> returnData = this.messager.sendCommandWithResponse(server, MessagingCommand.PLAYER_LANGUAGE_UPDATE, player.getUniqueID().toString(), rank.name());
                if (returnData == null) {
                    RavelInstance.getLogger().error("Failed to update player language for " + player.getName() + "!");
                    return;
                }
                String[] response = returnData.join();

                if (response.length != 1 || !response[0].equals(MessagingConstants.COMMAND_SUCCESS)) {
                    RavelInstance.getLogger().error("Failed to update player language for " + player.getName() + "!");
                    return;
                }
            }
        }

        this.playerInfo.get(player.getUniqueID()).updateRank(rank);
    }

    private String[] setRankCommand(RavelServer source, String[] args) {
        if (args.length != 2) {
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        UUID uuid = UUID.fromString(args[0]);
        RavelRank rank;
        try {
            rank = RavelRank.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        PlayerInfo info = this.playerInfo.get(uuid);
        if (info == null) {
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        info.updateRank(rank);

        return new String[] {MessagingConstants.COMMAND_SUCCESS};
    }

    public void kick(RavelPlayer player, String reason, boolean network) {
        //TODO: Implement kick
    }

    public void broadcast(Text message, String... args) {
        for (RavelPlayer player : this.connectedPlayers.values()) {
            player.sendMessage(message, args);
        }
    }

    public int getOnlineCount() {
        return this.connectedPlayers.size();
    }

    public Set<UUID> getConnectedUUIDs() {
        return this.connectedPlayers.keySet();
    }

    public Set<RavelPlayer> getConnectedPlayers() {
        return Set.copyOf(this.connectedPlayers.values());
    }

    public RavelPlayer getPlayer(UUID uuid) {
        return this.connectedPlayers.get(uuid);
    }

    //Utils

    private PlayerInfo getPlayerInfo(UUID uuid) {
        PlayerInfo info = this.playerInfo.get(uuid);
        if (info != null) {
            return info;
        }

        if (this.messager.isServer()) {
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

            info = new PlayerInfo(RavelServer.getLobby(), language, rank);
        } else {
            CompletableFuture<String[]> future = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PLAYER_GET_INFO, uuid.toString());
            String[] infoString = future.join();
            if (infoString == null) {
                return null;
            }

            info = this.getMessagePlayerInfo(infoString);
        }

        if (info != null) {
            this.playerInfo.put(uuid, info);
        }
        return info;
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

        if (RavelInstance.getServer().isProxy()) {
            this.broadcast(Text.PLAYERS_JOIN_NETWORK, player.getName());
        } else {
            this.broadcast(Text.PLAYERS_JOIN_SERVER, player.getName());
        }

        //Tell the other proxy about the player
        if (RavelInstance.getServer().isProxy()) {
            RavelServer server;
            if (RavelInstance.getServer().isJavaProxy()) {
                server = RavelServer.BE_PROXY;
            } else {
                server = RavelServer.JE_PROXY;
            }

            this.messager.sendCommand(server, MessagingCommand.PROXY_PLAYER_JOINED, player.getUniqueID().toString(), player.getName());
        }
    }

    protected void playerJoinedInternal(RavelPlayer player) {
        UUID uuid = player.getUniqueID();
        this.connectedPlayers.put(uuid, player);
        this.playerInfo.put(uuid, this.getPlayerInfo(uuid));
    }

    public void playerLeft(UUID uuid) {
        String name = this.connectedPlayers.get(uuid).getName();
        this.playerLeftInternal(uuid);

        if (RavelInstance.getServer().isProxy()) {
            this.broadcast(Text.PLAYERS_LEAVE_NETWORK, name);
        } else {
            this.broadcast(Text.PLAYERS_LEAVE_SERVER, name);
        }

        //Tell the other proxy about the player
        if (RavelInstance.getServer().isProxy()) {
            RavelServer server;
            if (RavelInstance.getServer().isJavaProxy()) {
                server = RavelServer.BE_PROXY;
            } else {
                server = RavelServer.JE_PROXY;
            }

            this.messager.sendCommand(server, MessagingCommand.PROXY_PLAYER_LEFT, uuid.toString());
        }
    }

    protected void playerLeftInternal(UUID uuid) {
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
