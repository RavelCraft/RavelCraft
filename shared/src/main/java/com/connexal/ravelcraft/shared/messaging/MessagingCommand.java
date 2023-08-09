package com.connexal.ravelcraft.shared.messaging;

public enum MessagingCommand {
    HEARTBEAT,
    RESPONSE,

    PROXY_PLAYER_JOINED, //When a player joins the other proxy server
    PROXY_PLAYER_LEFT, //When a player leaves the other proxy server

    PROXY_SEND_MESSAGE, //When a proxy wants to send a message to a player that it doesn't own
    PROXY_TRANSFER_PLAYER, //When wanting to transfer a player to another backend server
    PROXY_TRANSFER_PLAYER_COMPLETE, //When a player has been transferred to another backend server
    PROXY_MOTD_GET, //When a proxy wants to get the message of the day
    PROXY_MOTD_SET, //When a proxy wants to set the message of the day
    PROXY_WHITELIST_ENABLED_GET, //When a proxy wants to get the servers on which the whitelist is enabled
    PROXY_WHITELIST_ENABLED_SET, //When a proxy wants to set the servers on which the whitelist is enabled
    PROXY_WHITELIST_SET, //When a proxy wants to set the whitelisted players for a server or the proxy
    PROXY_WHITELIST_GET, //When a proxy wants to get the whitelisted players for everything
    PROXY_BAN_ADD, //When a proxy wants to add a ban
    PROXY_BAN_REMOVE, //When a proxy wants to remove a ban
    PROXY_BAN_GET, //When a proxy wants to get the ban list
    PROXY_MAINTENANCE_SET, //When a proxy wants to set the maintenance status of a server or the proxy
    PROXY_MAINTENANCE_GET, //When a proxy wants to get the maintenance statuses of everything

    PLAYER_GET_INFO, //When a server wants to get info about a player
    PLAYER_GET_UUID_FROM_NAME, //When a server wants to get a player's UUID from their name
    PLAYER_GET_NAME_FROM_UUID, //When a server wants to get a player's name from their UUID

    PLAYER_SKIN_UPDATE, //Tell a server that a player's skin has been updated
    PLAYER_RANK_UPDATE, //Tell a server that a player's rank has been updated
    PLAYER_LANGUAGE_UPDATE, //Tell a server that a player's nickname has been updated
    PLAYER_KICK, //Tell a server or proxy to kick a player
}
