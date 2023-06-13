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

import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import org.geysermc.geyser.api.event.EventRegistrar;

import static com.connexal.ravelcraft.mod.server.velocity.VelocityLib.PLAYER_INFO_CHANNEL;
import static com.connexal.ravelcraft.mod.server.velocity.VelocityLib.PLAYER_INFO_PACKET;

public class VelocityModernForwarding implements EventRegistrar {
    private static VelocityPacketHandler handler;

    public static void init() {
        handler = new VelocityPacketHandler();

        ServerLoginNetworking.registerGlobalReceiver(PLAYER_INFO_CHANNEL, handler::handleVelocityPacket);
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> sender.sendPacket(PLAYER_INFO_CHANNEL, PLAYER_INFO_PACKET));
    }
}
