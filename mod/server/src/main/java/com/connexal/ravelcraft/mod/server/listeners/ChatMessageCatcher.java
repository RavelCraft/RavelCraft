package com.connexal.ravelcraft.mod.server.listeners;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.mod.server.util.events.PlayerEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatMessageCatcher {
    private static final Map<UUID, CompletableFuture<String>> chatCatchers = new HashMap<>();

    public static CompletableFuture<String> registerCatch(UUID playerUUID) {
        if (ChatMessageCatcher.chatCatchers.containsKey(playerUUID)) {
            throw new IllegalStateException("Player already has a chat catcher registered");
        }

        CompletableFuture<String> catcher = new CompletableFuture<>();
        ChatMessageCatcher.chatCatchers.put(playerUUID, catcher);

        return catcher;
    }

    public static void register() {
        PlayerEvents.CHAT.register(ChatMessageCatcher::catchChat);
        PlayerEvents.LEFT.register(ChatMessageCatcher::playerLeft);
    }

    private static boolean catchChat(FabricRavelPlayer player, String message) {
        if (!ChatMessageCatcher.chatCatchers.isEmpty()) {
            CompletableFuture<String> catcher = ChatMessageCatcher.chatCatchers.remove(player.getUniqueID());
            if (catcher != null) {
                catcher.complete(message);
                return false;
            }
        }

        return true;
    }

    private static void playerLeft(FabricRavelPlayer player, String reason) {
        ChatMessageCatcher.chatCatchers.remove(player.getUniqueID());
    }
}
