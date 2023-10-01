package com.connexal.ravelcraft.shared.util.uuid;

import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.server.RavelServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UUIDTools {
    private final RavelConfig config;
    private final Messager messager;

    private final Map<String, UUID> uuidCache = new HashMap<>();

    public UUIDTools() {
        this.messager = RavelInstance.getMessager();

        if (MessagingConstants.isServer()) {
            this.config = RavelInstance.getConfig("uuids");
            this.config.save();

            this.messager.registerCommandHandler(MessagingCommand.PLAYER_GET_UUID_FROM_NAME, this::commandUUIDFromName);
            this.messager.registerCommandHandler(MessagingCommand.PLAYER_GET_NAME_FROM_UUID, this::commandNameFromUUID);
        } else {
            this.config = null;
        }
    }

    private String[] commandUUIDFromName(RavelServer source, String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }

        UUID uuid = this.getUUID(args[0]);
        if (uuid == null) {
            return new String[] {};
        }
        return new String[] { uuid.toString() };
    }

    private String[] commandNameFromUUID(RavelServer source, String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }

        UUID uuid = UUID.fromString(args[0]);
        String name = this.getName(uuid);
        if (name == null) {
            return new String[] {};
        }
        return new String[] { name };
    }

    public static UUID getJavaUUIDFromXUID(String xuid) {
        try {
            long xuidLong = Long.parseLong(xuid);
            return new UUID(0, xuidLong);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private UUID addUUIDToCache(String name, String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        this.uuidCache.put(name, uuidObj);
        return uuidObj;
    }

    public UUID getUUID(String name) {
        if (this.uuidCache.containsKey(name)) {
            return this.uuidCache.get(name);
        }

        if (this.config == null) { //Ask the main server
            String[] uuidString = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PLAYER_GET_UUID_FROM_NAME, name);
            if (uuidString == null || uuidString.length != 1) {
                return null;
            }

            return this.addUUIDToCache(name, uuidString[0]);
        } else {
            for (String uuidString : this.config.getKeys()) {
                if (this.config.getString(uuidString).equals(name)) {
                    return this.addUUIDToCache(name, uuidString);
                }
            }

            UUID uuid = UUIDApi.getUUID(name);
            if (uuid == null) {
                return null;
            }

            this.config.set(uuid.toString(), name);
            this.config.save();
            this.uuidCache.put(name, uuid);
            return uuid;
        }
    }

    private String addNameToCache(UUID uuid, String name) {
        this.uuidCache.put(name, uuid);
        return name;
    }

    public String getName(UUID uuid) {
        for (Map.Entry<String, UUID> entry : this.uuidCache.entrySet()) {
            if (entry.getValue().equals(uuid)) {
                return entry.getKey();
            }
        }

        if (this.config == null) { //Ask the main server
            String[] name = this.messager.sendCommandWithResponse(MessagingConstants.MESSAGING_SERVER, MessagingCommand.PLAYER_GET_NAME_FROM_UUID, uuid.toString());
            if (name == null || name.length != 1) {
                return null;
            }

            return this.addNameToCache(uuid, name[0]);
        } else {
            if (this.config.contains(uuid.toString())) {
                return this.config.getString(uuid.toString());
            }

            String name = UUIDApi.getName(uuid);
            if (name == null) {
                return null;
            }

            this.config.set(uuid.toString(), name);
            this.config.save();
            return name;
        }
    }

    public void registerPlayerData(UUID uuid, String name) {
        this.uuidCache.put(name, uuid);

        if (this.config != null) {
            this.config.set(uuid.toString(), name);
            this.config.save();
        }
    }
}
