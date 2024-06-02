package com.connexal.ravelcraft.mod.server.mixin.npc;

import com.connexal.ravelcraft.mod.server.managers.npc.NpcPlayerUpdate;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        this.playerCache = player;
    }

    @Inject(method = "onPlayerMove", at = @At("RETURN"))
    private void removeNpcFromTablist(CallbackInfo ci) {
        if (this.tablistQueue.isEmpty()) return;

        this.queueTick++;

        List<UUID> toRemove = new ArrayList<>();
        for (Iterator<NpcPlayerUpdate> iterator = this.tablistQueue.values().iterator(); iterator.hasNext(); ) {
            NpcPlayerUpdate current = iterator.next();
            if (current.removeAt() > this.queueTick) break;

            iterator.remove();
            toRemove.add(current.profile().getId());
        }
        if (toRemove.isEmpty()) return;

        PlayerRemoveS2CPacket removePacket = new PlayerRemoveS2CPacket(toRemove);

        this.skipCheck = true;
        this.sendPacket(removePacket);
        this.skipCheck = false;
    }
}
