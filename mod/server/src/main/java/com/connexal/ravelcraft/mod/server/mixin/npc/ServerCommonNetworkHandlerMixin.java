package com.connexal.ravelcraft.mod.server.mixin.npc;

import com.connexal.ravelcraft.mod.server.managers.npc.NpcEntity;
import com.connexal.ravelcraft.mod.server.managers.npc.NpcPlayerUpdate;
import com.connexal.ravelcraft.mod.server.mixin.accessors.EntitySpawnS2CPacketAccessor;
import com.connexal.ravelcraft.mod.server.mixin.accessors.EntityTrackerUpdateS2CPacketAccessor;
import com.connexal.ravelcraft.mod.server.mixin.accessors.PlayerListS2CPacketAccessor;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerCommonNetworkHandler.class)
public abstract class ServerCommonNetworkHandlerMixin {
    @Shadow public abstract void sendPacket(Packet<?> packet);

    @Shadow public abstract void send(Packet<?> packet, @Nullable PacketCallbacks callbacks);

    @Unique
    protected final Map<UUID, NpcPlayerUpdate> tablistQueue = new LinkedHashMap<>();
    @Unique
    protected boolean skipCheck = false;
    @Unique
    protected int queueTick = 0;
    @Unique
    protected ServerPlayerEntity playerCache = null;

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V", shift = At.Shift.BEFORE),
            cancellable = true)
    private void changeEntityType(Packet<?> packet, PacketCallbacks packetCallbacks, CallbackInfo ci) {
        if (this.playerCache == null) return;
        World world = this.playerCache.getWorld();

        if (packet instanceof BundlePacket<?> bPacket && !this.skipCheck) {
            for (Packet<?> subPacket : bPacket.getPackets()) {
                if (subPacket instanceof EntitySpawnS2CPacketAccessor) {
                    Entity entity = world.getEntityById(((EntitySpawnS2CPacketAccessor) subPacket).getId());
                    if (!(entity instanceof NpcEntity npc)) return;

                    GameProfile profile = npc.getGameProfile();
                    PlayerListS2CPacket playerAddPacket = PlayerListS2CPacket.entryFromPlayer(Collections.emptyList());
                    //noinspection ConstantConditions
                    PlayerListS2CPacket.Entry entry = new PlayerListS2CPacket.Entry(profile.getId(), profile, false, 0, GameMode.SURVIVAL, npc.getDisplayName(), null);
                    ((PlayerListS2CPacketAccessor) playerAddPacket).setEntries(Collections.singletonList(entry));
                    this.send(playerAddPacket, packetCallbacks);

                    // Before we send this packet, we have
                    // added player to tablist, otherwise client doesn't
                    // show it ... :mojank:
                    this.skipCheck = true;
                    this.send(packet, packetCallbacks);
                    this.skipCheck = false;

                    // And now we can remove it from tablist
                    // we must delay the tablist packet to allow
                    // the client to fetch skin.
                    // If player is immediately removed from the tablist,
                    // client doesn't care about the skin.
                    UUID uuid = npc.getGameProfile().getId();
                    this.tablistQueue.remove(uuid);
                    this.tablistQueue.put(uuid, new NpcPlayerUpdate(npc.getGameProfile(), npc.getTabListName(), this.queueTick + 30));

                    this.send(new EntitySetHeadYawS2CPacket(entity, (byte) ((int) (entity.getHeadYaw() * 256.0F / 360.0F))), packetCallbacks);

                    ci.cancel();
                } else if (subPacket instanceof EntityTrackerUpdateS2CPacket) {
                    Entity entity = world.getEntityById(((EntityTrackerUpdateS2CPacketAccessor) subPacket).getId());
                    if (!(entity instanceof NpcEntity npc)) return;

                    ServerPlayerEntity fakePlayer = npc.getFakePlayer();
                    List<DataTracker.SerializedEntry<?>> trackedValues = fakePlayer.getDataTracker().getChangedEntries();

                    ((EntityTrackerUpdateS2CPacketAccessor) subPacket).setTrackedValues(trackedValues);
                }
            }
        }
    }
}
