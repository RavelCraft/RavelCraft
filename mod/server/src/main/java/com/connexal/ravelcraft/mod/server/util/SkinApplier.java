/*
 * MIT License
 *
 * Copyright (c) 2021 LionariusPy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.connexal.ravelcraft.mod.server.util;

import com.connexal.ravelcraft.mod.server.RavelModServer;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.biome.source.BiomeAccess;

import java.util.Collections;
import java.util.Optional;

public final class SkinApplier {
    public static void applySkin(ServerPlayerEntity player, String skinValue, String skinSignature) {
        RavelInstance.getLogger().info("Applying skin to " + player.getName() + " (" + player.getUuidAsString() + "):\n - " + skinValue + "\n - " + skinSignature);

        RavelModServer.getServer().execute(() -> {
            PropertyMap properties = player.getGameProfile().getProperties();

            properties.removeAll("textures");
            properties.put("textures", new Property("textures", skinValue, skinSignature));

            for (PlayerEntity observer : player.getWorld().getPlayers()) {
                ServerPlayerEntity observer1 = (ServerPlayerEntity) observer;
                observer1.networkHandler.sendPacket(new PlayerRemoveS2CPacket(Collections.singletonList(player.getUuid())));
                observer1.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player)); // refresh the player information
                if (player != observer1 && observer1.canSee(player)) {
                    observer1.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(player.getId()));
                    observer1.networkHandler.sendPacket(new EntitySpawnS2CPacket(player));
                    observer1.networkHandler.sendPacket(new EntityPositionS2CPacket(player));
                    observer1.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(player.getId(), player.getDataTracker().getChangedEntries()));
                } else if (player == observer1) {
                    observer1.networkHandler.sendPacket(new PlayerRespawnS2CPacket(
                            new CommonPlayerSpawnInfo(
                                    observer1.getWorld().getDimensionKey(),
                                    observer1.getWorld().getRegistryKey(),
                                    BiomeAccess.hashSeed(observer1.getServerWorld().getSeed()),
                                    observer1.interactionManager.getGameMode(),
                                    observer1.interactionManager.getPreviousGameMode(),
                                    observer1.getWorld().isDebugWorld(),
                                    observer1.getServerWorld().isFlat(),
                                    Optional.empty(),
                                    observer1.getPortalCooldown()
                            ),
                            (byte)2
                    ));
                    observer1.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(observer1.getInventory().selectedSlot));
                    observer1.sendAbilitiesUpdate();
                    observer1.playerScreenHandler.updateToClient();
                    for (StatusEffectInstance instance : observer1.getStatusEffects()) {
                        observer1.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(observer1.getId(), instance));
                    }
                    observer1.networkHandler.requestTeleport(observer1.getX(), observer1.getY(), observer1.getZ(), observer1.getYaw(), observer1.getPitch());
                    observer1.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(player.getId(), player.getDataTracker().getChangedEntries()));
                    observer1.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
                }
            }

            RavelInstance.getLogger().info("Applied skin to " + player.getName().getString());
        });
    }
}
