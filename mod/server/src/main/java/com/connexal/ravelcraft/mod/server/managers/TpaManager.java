package com.connexal.ravelcraft.mod.server.managers;

import com.connexal.ravelcraft.mod.server.players.FabricRavelPlayer;
import com.connexal.ravelcraft.shared.all.text.RavelText;
import com.connexal.ravelcraft.shared.server.RavelInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {
    private final Map<UUID, TpaRequest> requests = new HashMap<>();

    public void queueRequest(FabricRavelPlayer sender, FabricRavelPlayer receiver, TpaType type) {
        if (sender.getUniqueID() == receiver.getUniqueID()) {
            sender.sendMessage(RavelText.COMMAND_TPA_SELF);
            return;
        }
        if (this.requests.get(receiver.getUniqueID()) != null) {
            sender.sendMessage(RavelText.COMMAND_TPA_REQUEST_PENDING, receiver.getName());
            return;
        }

        TpaRequest request = new TpaRequest(sender, receiver, type);
        this.requests.put(receiver.getUniqueID(), request);

        int timeout = 30;
        RavelInstance.scheduleTask(() -> {
            TpaRequest expiredReq = this.requests.get(receiver.getUniqueID());
            if (expiredReq == null) {
                return;
            }

            expiredReq.sender.sendMessage(RavelText.COMMAND_TPA_EXPIRED_SENDER, expiredReq.receiver.getName());
            expiredReq.receiver.sendMessage(RavelText.COMMAND_TPA_EXPIRED_RECEIVER, expiredReq.sender.getName());
            this.requests.remove(receiver.getUniqueID());
        }, timeout);

        if (type == TpaType.SENDER_TO_RECEIVER) {
            request.sender.sendMessage(RavelText.COMMAND_TPA_STR_SENT, request.receiver.getName());
            request.receiver.sendMessage(RavelText.COMMAND_TPA_STR_RECEIVED, request.sender.getName(), Integer.toString(timeout));
        } else {
            request.sender.sendMessage(RavelText.COMMAND_TPA_RTS_SENT, request.receiver.getName());
            request.receiver.sendMessage(RavelText.COMMAND_TPA_RTS_RECEIVED, request.sender.getName(), Integer.toString(timeout));
        }
    }

    public void acceptRequest(FabricRavelPlayer receiver) {
        TpaRequest request = this.requests.get(receiver.getUniqueID());
        if (request == null) {
            receiver.sendMessage(RavelText.COMMAND_TPA_NO_REQUESTS);
            return;
        }

        if (request.type == TpaType.SENDER_TO_RECEIVER) {
            request.sender.teleport(request.receiver);
        } else {
            request.receiver.teleport(request.sender);
        }

        request.sender.sendMessage(RavelText.COMMAND_TPA_ACCEPT_RECEIVED, request.receiver.getName());
        request.receiver.sendMessage(RavelText.COMMAND_TPA_ACCEPT_SENT);

        this.requests.remove(receiver.getUniqueID());
    }

    public void denyRequest(FabricRavelPlayer receiver) {
        TpaRequest request = this.requests.get(receiver.getUniqueID());
        if (request == null) {
            receiver.sendMessage(RavelText.COMMAND_TPA_NO_REQUESTS);
            return;
        }

        request.sender.sendMessage(RavelText.COMMAND_TPA_DENY_RECEIVED, request.receiver.getName());
        request.receiver.sendMessage(RavelText.COMMAND_TPA_DENY_SENT);

        this.requests.remove(receiver.getUniqueID());
    }

    private record TpaRequest(FabricRavelPlayer sender, FabricRavelPlayer receiver, TpaType type) {
    }

    public enum TpaType {
        SENDER_TO_RECEIVER,
        RECEIVER_TO_SENDER
    }
}
