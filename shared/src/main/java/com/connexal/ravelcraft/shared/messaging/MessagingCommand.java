package com.connexal.ravelcraft.shared.messaging;

public enum MessagingCommand {
    HEARTBEAT,
    RESPONSE,

    PROXY_PLAYER_JOINED, //When a player joins the other proxy server
    PROXY_PLAYER_LEFT, //When a player leaves the other proxy server
    PROXY_SEND_MESSAGE, //When a proxy wants to send a message to a player that it doesn't own
    PROXY_TRANSFER_PLAYER, //When wanting to transfer a player to another backend server
    PROXY_TRANSFER_PLAYER_COMPLETE, //When a player has been transferred to another backend server
    PROXY_MOTD_GET,
    PROXY_MOTD_SET,

    PLAYER_GET_INFO, //When a server wants to get info about a player
    PLAYER_GET_UUID_FROM_NAME, //When a server wants to get a player's UUID from their name
    PLAYER_GET_NAME_FROM_UUID, //When a server wants to get a player's name from their UUID

    PLAYER_SKIN_UPDATE, //Tell a server that a player's skin has been updated
    PLAYER_RANK_UPDATE, //Tell a server that a player's rank has been updated
    PLAYER_LANGUAGE_UPDATE, //Tell a server that a player's nickname has been updated

    PLAYER_KICK, //Tell a server or proxy to kick a player
}
