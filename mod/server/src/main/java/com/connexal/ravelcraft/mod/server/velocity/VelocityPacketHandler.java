/*
 * MIT License
 *
 * Copyright (c) 2021 OKTW Network
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

package com.connexal.ravelcraft.mod.server.velocity;

import com.connexal.ravelcraft.mod.server.geyser.GeyserSkinGetter;
import com.connexal.ravelcraft.mod.server.mixin.velocity.ClientConnection_AddressAccessor;
import com.connexal.ravelcraft.mod.server.mixin.velocity.ServerLoginNetworkHandlerAccessor;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.UUIDTools;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import org.geysermc.api.connection.Connection;
import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;

//Bedrock login handler added for the purposes of this mod
public class VelocityPacketHandler {
    void handleVelocityPacket(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender ignored) {
        if (understood) {
            this.javaLogin(server, handler, buf, synchronizer);
            return;
        }

        String name = ((ServerLoginNetworkHandlerAccessor) handler).getProfile().getName();
        String xuid = null;
        for (Connection connection : GeyserApi.api().onlineConnections()) {
            if (connection.bedrockUsername().equals(name)) {
                xuid = connection.xuid();
                break;
            }
        }
        if (xuid != null) {
            this.bedrockLogin(server, handler, synchronizer, name, xuid);
            return;
        }

        handler.disconnect(Text.of("This server requires you to connect through the Proxy!"));
    }

    private void javaLogin(MinecraftServer server, ServerLoginNetworkHandler handler, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer) {
        synchronizer.waitFor(server.submit(() -> {
            try {
                if (!VelocityLib.checkIntegrity(buf)) {
                    handler.disconnect(Text.of("Unable to verify player details"));
                    return;
                }
                VelocityLib.checkVersion(buf);
            } catch (Throwable e) {
                RavelInstance.getLogger().error("Secret check failed.", e);
                handler.disconnect(Text.of("Unable to verify player details"));
                return;
            }

            ClientConnection connection = ((ServerLoginNetworkHandlerAccessor) handler).getConnection();
            ((ClientConnection_AddressAccessor) connection).setAddress(new java.net.InetSocketAddress(VelocityLib.readAddress(buf), ((java.net.InetSocketAddress) (connection.getAddress())).getPort()));

            GameProfile profile;
            try {
                profile = VelocityLib.createProfile(buf);
            } catch (Exception e) {
                RavelInstance.getLogger().error("Profile create failed.", e);
                handler.disconnect(Text.of("Unable to read player profile"));
                return;
            }

            ((ServerLoginNetworkHandlerAccessor) handler).setProfile(profile);
        }));
    }

    private void bedrockLogin(MinecraftServer server, ServerLoginNetworkHandler handler, ServerLoginNetworking.LoginSynchronizer synchronizer, String name, String xuid) {
        synchronizer.waitFor(server.submit(() -> {
            UUID playerUUID = UUIDTools.getJavaUUIDFromXUID(xuid);

            String playerName = name;
            if (!playerName.startsWith(RavelPlayer.BEDROCK_PREFIX)) {
                playerName = "." + playerName;
            }
            if (playerName.contains(" ")) {
                playerName = playerName.replace(" ", RavelPlayer.BEDROCK_SPACE_REPLACEMENT);
            }
            if (playerName.length() > 16) {
                playerName = playerName.substring(0, 16);
            }

            GameProfile profile = new GameProfile(playerUUID, playerName);

            //Try to get skin
            GeyserSkinGetter.SkinData skinData = GeyserSkinGetter.getSkin(xuid);
            if (skinData != null) {
                profile.getProperties().removeAll("textures");
                profile.getProperties().put("textures", new Property("textures", skinData.value, skinData.signature));
            }

            ((ServerLoginNetworkHandlerAccessor) handler).setProfile(profile);
        }));
    }
}
