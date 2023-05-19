package com.connexal.ravelcraft.shared;

import com.connexal.ravelcraft.shared.data.Server;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingClient;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.messaging.MessagingServer;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.RavelLogger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RavelInstance {
    private static Path dataPath = null;
    private static RavelLogger logger = null;
    private static Server server = null;
    private static Messager messager = null;
    private static final Map<String, RavelConfig> configs = new HashMap<>();

    public static void init(RavelLogger logger, Path dataPath) {
        RavelInstance.logger = logger;
        logger.info("RavelCraft is initializing...");

        // --- Setup server info ---
        RavelConfig config = getConfig();
        if (config.contains("server-name")) {
            try {
                server = Server.valueOf(config.getString("server-name"));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid server name specified in config.yml!");
            }
        } else {
            config.set("server-name", "SERVER NAME");
            config.save();

            throw new RuntimeException("No server name specified in config.yml!");
        }

        // --- Setup messaging ---
        if (server == MessagingConstants.MESSAGING_SERVER) {
            messager = new MessagingServer();
        } else {
            String hostname;
            if (config.contains("messaging-server")) {
                hostname = config.getString("messaging-server");
            } else {
                config.set("messaging-server-hostname", "server.domain.name");
                config.save();

                throw new RuntimeException("No messaging server hostname specified in config.yml!");
            }

            messager = new MessagingClient(hostname);
        }
    }

    public static RavelLogger getLogger() {
        return logger;
    }

    public static Server getServer() {
        return server;
    }

    public static Messager getMessager() {
        return messager;
    }

    public static RavelConfig getConfig(String name) {
        if (configs.containsKey(name)) {
            return configs.get(name);
        } else {
            RavelConfig config = new RavelConfig(dataPath.resolve(name + ".yml"));
            configs.put(name, config);
            return config;
        }
    }

    public static RavelConfig getConfig() {
        return getConfig("config");
    }
}
