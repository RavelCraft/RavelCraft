package com.connexal.ravelcraft.shared.messaging;

public enum MessagingCommand {
    RESPONSE,

    PLAYER_JOINED_PROXY, //When a player joins the other proxy server
    PLAYER_LEFT_PROXY, //When a player leaves the other proxy server
    PLAYER_GET_INFO, //When any server wants to get info about a player
}
