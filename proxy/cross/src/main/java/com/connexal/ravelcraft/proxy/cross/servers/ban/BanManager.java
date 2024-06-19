package com.connexal.ravelcraft.proxy.cross.servers.ban;

import com.connexal.ravelcraft.proxy.cross.RavelProxyInstance;
import com.connexal.ravelcraft.shared.server.RavelInstance;
import com.connexal.ravelcraft.shared.server.messaging.Messager;
import com.connexal.ravelcraft.shared.server.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.server.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.all.util.StringUtils;
import com.connexal.ravelcraft.shared.server.util.server.RavelServer;
import com.connexal.ravelcraft.shared.all.text.RavelTextHardcoded;

import java.util.Map;
import java.util.UUID;

public abstract class BanManager {
    protected Map<UUID, BanData> banList = null;

    protected final Messager messager;

    public BanManager() {
        this.messager = RavelInstance.getMessager();
        this.messager.registerCommandHandler(MessagingCommand.PROXY_BAN_ADD, this::banAddCommand);
        this.messager.registerCommandHandler(MessagingCommand.PROXY_BAN_REMOVE, this::banRemoveCommand);

        this.messager.registerDisconnectHandler(server -> {
            this.banList = null;
        });
    }

    public static BanManager create() {
        if (MessagingConstants.isServer()) {
            return new MasterBanManager();
        } else {
            return new SlaveBanManager();
        }
    }

    protected abstract void ensureVariables();

    // --- Check ---

    public BanData isBanned(UUID uuid) {
        this.ensureVariables();

        BanData data = this.banList.get(uuid);
        if (data == null) {
            return null;
        }

        if (System.currentTimeMillis() > data.end()) {
            this.banRemoveInternal(uuid);
            return null;
        }

        return data;
    }

    // --- Remove ---

    protected abstract void banRemoveChildInternal(UUID uuid);

    private void banRemoveInternal(UUID uuid) {
        this.ensureVariables();
        this.banList.remove(uuid);
        this.banRemoveChildInternal(uuid);
    }

    public void removeBan(UUID uuid) {
        this.banRemoveInternal(uuid);
        this.messager.sendCommand(RavelProxyInstance.getOtherProxy(), MessagingCommand.PROXY_BAN_REMOVE, uuid.toString());
    }

    private String[] banRemoveCommand(RavelServer source, String[] args) {
        if (args.length != 1) {
            RavelInstance.getLogger().error("Invalid ban remove command!");
            return null;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Invalid UUID in ban remove command: " + args[0]);
            return null;
        }

        this.banRemoveInternal(uuid);
        return null;
    }

    // --- Add ---

    protected abstract void banAddChildInternal(UUID uuid, long end, String reason);

    private void banAddInternal(UUID uuid, long end, String reason) {
        this.ensureVariables();
        this.banList.put(uuid, new BanData(end, reason));
        this.banAddChildInternal(uuid, end, reason);
    }

    public void addBan(UUID uuid, long end, String reason) {
        this.banAddInternal(uuid, end, reason);
        this.messager.sendCommand(RavelProxyInstance.getOtherProxy(), MessagingCommand.PROXY_BAN_ADD, uuid.toString(), Long.toString(end), reason);
    }

    private String[] banAddCommand(RavelServer source, String[] args) {
        if (args.length != 3) {
            RavelInstance.getLogger().error("Invalid ban add command!");
            return null;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(args[0]);
        } catch (IllegalArgumentException e) {
            RavelInstance.getLogger().error("Invalid UUID in ban add command: " + args[0]);
            return null;
        }

        long end;
        try {
            end = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            RavelInstance.getLogger().error("Invalid end in ban add command: " + args[1]);
            return null;
        }

        this.banAddInternal(uuid, end, args[2]);
        return null;
    }

    public static String generateBanString(long end, String reason) {
        return RavelTextHardcoded.BANNED + StringUtils.formatDate(end) + "\n" + reason;
    }

    public record BanData(long end, String reason) {
    }
}
