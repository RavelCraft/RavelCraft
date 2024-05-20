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

public abstract class PlayerManager {
    protected final Map<UUID, RavelPlayer> connectedPlayers = new HashMap<>();
    private final Map<UUID, PlayerSettings> settingCache = new HashMap<>();
    private final RavelConfig config;

    protected Messager messager = null;

    public PlayerManager() {
        this.config = RavelInstance.getConfig("players");
        this.config.save();
    }

    public void init() {
        this.messager = RavelInstance.getMessager();

        this.messager.registerCommandHandler(MessagingCommand.PLAYER_GET_INFO, this::getPlayerInfoCommand);

        this.messager.registerCommandHandler(MessagingCommand.PLAYER_RANK_UPDATE, this::rankUpdateCommand);
        this.messager.registerCommandHandler(MessagingCommand.PLAYER_LANGUAGE_UPDATE, this::languageUpdateCommand);

        this.messager.registerCommandHandler(MessagingCommand.PLAYER_KICK, this::playerKickCommand);

        this.messager.registerDisconnectHandler(this::disconnectedFromMessaging);
    }

    protected abstract boolean transferPlayerInternal(RavelPlayer player, RavelServer server);

    public boolean transferPlayerToServer(RavelPlayer player, RavelServer server) {
        if (server.isProxy()) {
            throw new IllegalArgumentException("Cannot set player server to a proxy server");
        }
        if (player.getServer() == server) {
            return true;
        }

        if (!RavelInstance.getServer().isProxy() || player.getOwnerProxy() != RavelInstance.getServer()) {
            String[] response = this.messager.sendCommandWithResponse(player.getOwnerProxy(), MessagingCommand.PROXY_TRANSFER_PLAYER, player.getUniqueID().toString(), server.name());
            if (response == null || response.length != 1) {
                return false;
            }

            return response[0].equals(MessagingConstants.COMMAND_SUCCESS);
        } else {
            return this.transferPlayerInternal(player, server);
        }
    }

    private RavelPlayer updateServersAboutPlayer(UUID uuid, MessagingCommand command, String data) {
        RavelPlayer player = this.getPlayer(uuid);
        RavelServer playerServer = null;
        if (player != null) {
            playerServer = player.getServer();
        }

        for (RavelServer server : RavelServer.values()) {
            if (server == RavelInstance.getServer()) { //We don't need to send a message to ourselves
                continue;
            }

            if (player == null) {
                if (server != MessagingConstants.MESSAGING_SERVER) {
                    continue;
                }
            } else {
                if (server != playerServer && !server.isProxy()) {
                    continue;
                }
            }

            String[] response = this.messager.sendCommandWithResponse(server, command, uuid.toString(), data);
            if (response == null || response.length != 1 || !response[0].equals(MessagingConstants.COMMAND_SUCCESS)) {
                RavelInstance.getLogger().error("Failed to update player info on " + server.name() + "!");
            }
        }

        return player;
    }

    public void languageUpdate(UUID uuid, Language language) {
        RavelPlayer player = this.updateServersAboutPlayer(uuid, MessagingCommand.PLAYER_LANGUAGE_UPDATE, language.name());
        this.languageConfigUpdate(uuid, language);

        if (player != null) {
            player.setLanguage(language);
        }
    }

    private void languageConfigUpdate(UUID uuid, Language language) {
        if (MessagingConstants.isServer()) {
            this.config.set(uuid + ".language", language.name());
            this.config.save();
        }

        this.settingCache.remove(uuid);
    }

    private String[] languageUpdateCommand(RavelServer source, String[] args) {
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

        this.languageConfigUpdate(uuid, language);
        RavelPlayer player = this.getPlayer(uuid);
        if (player != null) {
            player.setLanguage(language);
        }

        return new String[] {MessagingConstants.COMMAND_SUCCESS};
    }

    public void rankUpdate(UUID uuid, RavelRank rank) {
        RavelPlayer player = this.updateServersAboutPlayer(uuid, MessagingCommand.PLAYER_RANK_UPDATE, rank.name());
        this.rankConfigUpdate(uuid, rank);

        if (player != null) {
            player.setRank(rank);
            this.playerRankChanged(player, rank);
        }
    }

    private void rankConfigUpdate(UUID uuid, RavelRank rank) {
        if (MessagingConstants.isServer()) {
            this.config.set(uuid + ".rank", rank.name());
            this.config.save();
        }

        this.settingCache.remove(uuid);
    }

    private String[] rankUpdateCommand(RavelServer source, String[] args) {
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

        this.rankConfigUpdate(uuid, rank);
        RavelPlayer player = this.getPlayer(uuid);
        if (player != null) {
            player.setRank(rank);
            this.playerRankChanged(player, rank);
        }

        return new String[] {MessagingConstants.COMMAND_SUCCESS};
    }

    public abstract void applyPlayerRank(RavelPlayer player, RavelRank rank);

    protected abstract void playerRankChanged(RavelPlayer player, RavelRank rank);

    public abstract boolean kick(RavelPlayer player, String reason, boolean network);

    private String[] playerKickCommand(RavelServer source, String[] args) {
        if (args.length != 2) {
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        UUID uuid = UUID.fromString(args[0]);
        String reason = args[1];

        RavelPlayer player = this.getPlayer(uuid);
        if (player == null) {
            return new String[] {MessagingConstants.COMMAND_FAILURE};
        }

        this.kick(player, reason, RavelInstance.getServer().isProxy());

        return new String[] {MessagingConstants.COMMAND_SUCCESS};
    }

    public void disconnectedFromMessaging(RavelServer server) {
        RavelServer thisServer = RavelInstance.getServer();

        if (server == thisServer) { //This is the server that disconnected
            if (thisServer.isProxy()) {
                for (RavelPlayer player : this.connectedPlayers.values()) {
                    if (player.getOwnerProxy() != thisServer) { //Only kick players that were on this proxy, we can't communicate with the other
                        continue;
                    }

                    this.kick(player, "Network IPC error", true);
                }
            } else {
                for (RavelPlayer player : this.connectedPlayers.values()) {
                    this.kick(player, "Network IPC error", false);
                }
            }
        } else if (server.isProxy() && thisServer.isProxy()) { //A proxy disconnected, we need to kick our players
            for (RavelPlayer player : this.connectedPlayers.values()) {
                if (player.getOwnerProxy() != thisServer) {
                    continue;
                }

                this.kick(player, "Network IPC error", true);
            }
        }

        this.connectedPlayers.clear();
        this.settingCache.clear();
    }

    public void broadcast(Text message, String... args) {
        for (RavelPlayer player : this.connectedPlayers.values()) {
            player.sendMessage(message, args);
        }
    }

    public int getOnlineCount() {
        return this.connectedPlayers.size();
    }

    public Set<RavelPlayer> getConnectedPlayers() {
        return Set.copyOf(this.connectedPlayers.values());
    }

    public RavelPlayer getPlayer(UUID uuid) {
        return this.connectedPlayers.get(uuid);
    }

    public RavelPlayer getPlayer(String name) {
        for (RavelPlayer player : this.connectedPlayers.values()) {
            if (player.getName().equals(name)) {
                return player;
            }
        }

        return null;
    }

    public PlayerSettings getPlayerSettings(UUID uuid) {
        return this.getPlayerSettings(uuid, false);
    }

    public PlayerSettings getPlayerSettings(UUID uuid, boolean writeToCache) {
        if (this.settingCache.containsKey(uuid)) {
            return this.settingCache.get(uuid);
        }

        PlayerSettings settings;

        if (MessagingConstants.isServer()) {
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

            settings = new PlayerSettings(language, rank);
        } else {
            String[] infoString = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PLAYER_GET_INFO, uuid.toString());
            if (infoString == null) {
                RavelInstance.getLogger().error("Unable to get player info from server!");
                return null;
            }

            Language language;
            RavelRank rank;
            try {
                language = Language.valueOf(infoString[0]);
                rank = RavelRank.valueOf(infoString[1]);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Unable to understand message from server!");
            }

            settings = new PlayerSettings(language, rank);
        }

        if (writeToCache) {
            this.settingCache.put(uuid, settings);
        }
        return settings;
    }

    private String[] getPlayerInfoCommand(RavelServer source, String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }

        UUID uuid = UUID.fromString(args[0]);
        PlayerSettings info = this.getPlayerSettings(uuid);

        return new String[] {info.language().name(), info.rank().name()};
    }

    public void playerJoined(RavelPlayer player) {
        this.playerJoinedInternal(player);

        if (!RavelInstance.getServer().isProxy()) {
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
        if (RavelInstance.getServer().isProxy()) {
            for (RavelPlayer onlinePlayer : this.connectedPlayers.values()) {
                if (onlinePlayer.getOwnerProxy() != RavelInstance.getServer()) {
                    continue;
                }

                onlinePlayer.sendMessage(Text.PLAYERS_JOIN_NETWORK, player.getName());
            }
        }

        //TODO: Find a way to send a message to the player that just joined
        this.connectedPlayers.put(player.getUniqueID(), player);

        //This should take name changed into account
        RavelInstance.getUUIDTools().registerPlayerData(player.getUniqueID(), player.getName());
    }

    public void playerLeft(UUID uuid) {
        if (!RavelInstance.getServer().isProxy() && this.connectedPlayers.containsKey(uuid)) {
            String name = this.connectedPlayers.get(uuid).getName();
            this.broadcast(Text.PLAYERS_LEAVE_SERVER, name);
        }

        this.playerLeftInternal(uuid);

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
        if (RavelInstance.getServer().isProxy() && this.connectedPlayers.containsKey(uuid)) {
            String name = this.connectedPlayers.get(uuid).getName();

            for (RavelPlayer otherPlayer : this.connectedPlayers.values()) {
                if (otherPlayer.getOwnerProxy() != RavelInstance.getServer()) {
                    continue;
                }

                otherPlayer.sendMessage(Text.PLAYERS_LEAVE_NETWORK, name);
            }
        }

        this.connectedPlayers.remove(uuid);
        this.settingCache.remove(uuid);
    }

    public record PlayerSettings(Language language, RavelRank rank) {
    }
}
