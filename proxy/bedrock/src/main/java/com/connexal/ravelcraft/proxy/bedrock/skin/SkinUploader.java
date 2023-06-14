/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Floodgate
 */

package com.connexal.ravelcraft.proxy.bedrock.skin;

import com.connexal.ravelcraft.proxy.bedrock.BeProxy;
import com.connexal.ravelcraft.shared.RavelInstance;
import com.connexal.ravelcraft.shared.messaging.MessagingCommand;
import com.connexal.ravelcraft.shared.players.RavelPlayer;
import com.connexal.ravelcraft.shared.util.RavelLogger;
import com.connexal.ravelcraft.shared.util.UUIDTools;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jwt.SignedJWT;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class SkinUploader {
    private final ObjectMapper JACKSON = new ObjectMapper();
    private final List<String> skinQueue = new ArrayList<>();

    private final RavelLogger logger;
    private final WebSocketClient client;
    private volatile boolean closed;

    public SkinUploader() {
        URI wsUri;
        try {
            wsUri = new URI("wss://api.geysermc.org/ws");
        } catch (Exception e) {
            throw new AssertionError("Failed to create websocket URI", e);
        }

        this.logger = RavelInstance.getLogger();
        this.client = new WebSocketClient(wsUri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                setConnectionLostTimeout(11);

                Iterator<String> queueIterator = skinQueue.iterator();
                while (this.isOpen() && queueIterator.hasNext()) {
                    this.send(queueIterator.next());
                    queueIterator.remove();
                }
            }

            @Override
            public void onMessage(String message) {
                try {
                    JsonNode node = JACKSON.readTree(message);
                    if (node.has("error")) {
                        logger.error("Got an error: " + node.get("error").asText());
                        return;
                    }

                    int typeId = node.get("event_id").asInt();
                    WebsocketEventType type = WebsocketEventType.fromId(typeId);
                    if (type == null) {
                        logger.warning(String.format(
                                "Got (unknown) type %s. Ensure that Geyser is on the latest version and report this issue!",
                                typeId));
                        return;
                    }

                    switch (type) {
                        case SKIN_UPLOADED -> {
                            String xuid = node.get("xuid").asText();
                            UUID uuid = UUIDTools.getJavaUUIDFromXUID(xuid);
                            if (uuid == null) {
                                logger.error("Invalid XUID returned by skin uploader: " + xuid);
                                return;
                            }
                            RavelPlayer player = RavelInstance.getPlayerManager().getPlayer(uuid);
                            if (player != null) {
                                if (!node.get("success").asBoolean()) {
                                    logger.info("Failed to upload skin for " + player.getName());
                                    return;
                                }

                                JsonNode data = node.get("data");

                                String value = data.get("value").asText();
                                String signature = data.get("signature").asText();

                                logger.info("Skin for " + player.getName() + " uploaded successfully");
                                RavelInstance.getMessager().sendCommand(player.getServer(), MessagingCommand.PLAYER_SKIN_UPDATE, uuid.toString(), value, signature);
                            }
                        }
                        case LOG_MESSAGE -> {
                            String logMessage = node.get("message").asText();
                            switch (node.get("priority").asInt()) {
                                case 0 -> logger.info("Got a message from skin uploader: " + logMessage);
                                case 1 -> logger.error("Got a message from skin uploader: " + logMessage);
                                default -> logger.info(logMessage);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error while receiving a message", e);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (reason != null && !reason.isEmpty()) {
                    try {
                        JsonNode node = JACKSON.readTree(reason);
                        // info means that the uploader itself did nothing wrong
                        if (node.has("info")) {
                            String info = node.get("info").asText();
                            logger.warning("Got disconnected from the skin uploader: " + info);
                        }
                        // error means that the uploader did something wrong
                        if (node.has("error")) {
                            String error = node.get("error").asText();
                            logger.info("Got disconnected from the skin uploader: " + error);
                        }
                    } catch (JsonProcessingException ignored) {
                        // ignore invalid json
                    } catch (Exception e) {
                        logger.error("Error while handling onClose", e);
                    }
                }

                // try to reconnect after a few seconds
                reconnectLater();
            }

            @Override
            public void onError(Exception ex) {
                if (ex instanceof UnknownHostException) {
                    logger.error("Unable to resolve the skin api! This can be caused by your connection or the skin api being unreachable. " + ex.getMessage());
                    return;
                }
                if (ex instanceof ConnectException || ex instanceof SSLException) {
                    return;
                }
                logger.error("Got an error", ex);
            }
        };
    }

    public void uploadSkin(List<SignedJWT> chainData, String clientData) {
        if (chainData == null || clientData == null) {
            this.logger.error("Invalid skin data, unable to upload");
            return;
        }

        ObjectNode node = this.JACKSON.createObjectNode();
        ArrayNode chainDataNode = this.JACKSON.createArrayNode();
        chainData.forEach(jwt -> chainDataNode.add(jwt.serialize()));
        node.set("chain_data", chainDataNode);
        node.put("client_data", clientData);

        String jsonString;
        try {
            jsonString = this.JACKSON.writeValueAsString(node);
        } catch (Exception e) {
            this.logger.error("Failed to upload skin", e);
            return;
        }

        if (this.client.isOpen()) {
            this.client.send(jsonString);
        } else {
            this.skinQueue.add(jsonString);
        }
    }

    private void reconnectLater() {
        // we can only reconnect when the thread pool is open
        if (this.closed) {
            this.logger.info("The skin uploader has been closed");
            return;
        }

        int additionalTime = ThreadLocalRandom.current().nextInt(7);
        // we don't have to check the result. onClose will handle that for us

        BeProxy.getServer().getScheduler().scheduleDelayed(client::reconnect, 20 * (8 + additionalTime)); // delay in ticks
    }

    public void start() {
        client.connect();
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.client.close();
        }
    }
}