package com.connexal.ravelcraft.shared.messaging;

public enum MessagingCommand {
    RESPONSE,

    PLAYER_JOINED_PROXY, //When a player joins the other proxy server
    PLAYER_LEFT_PROXY, //When a player leaves the other proxy server
    PROXY_QUERY_CONNECTED, //When a proxy connects to the network, it asks the other about online players
    PROXY_SEND_MESSAGE, //When a proxy wants to send a message to a player that it doesn't own

    PLAYER_GET_INFO, //When any server wants to get info about a player
}
