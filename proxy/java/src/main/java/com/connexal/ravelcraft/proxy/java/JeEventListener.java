package com.connexal.ravelcraft.proxy.java;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;

public class JeEventListener {
    @Subscribe(order = PostOrder.FIRST)
    public void onLoginEvent(LoginEvent event) {
        //TODO: Check if player is banned

        //TODO: Check if player is whitelisted


    }
}
