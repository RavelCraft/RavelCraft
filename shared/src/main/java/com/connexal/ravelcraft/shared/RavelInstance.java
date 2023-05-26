package com.connexal.ravelcraft.shared;

import com.connexal.ravelcraft.shared.commands.CommandRegistrar;
import com.connexal.ravelcraft.shared.util.RavelServer;
import com.connexal.ravelcraft.shared.messaging.Messager;
import com.connexal.ravelcraft.shared.messaging.MessagingClient;
import com.connexal.ravelcraft.shared.messaging.MessagingConstants;
import com.connexal.ravelcraft.shared.messaging.MessagingServer;
import com.connexal.ravelcraft.shared.util.RavelConfig;
import com.connexal.ravelcraft.shared.util.RavelLogger;
import com.connexal.ravelcraft.shared.util.text.Text;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RavelInstance {
    private static RavelMain main = null;
    private static Path dataPath = null;
    private static RavelLogger logger = null;
    private static RavelServer server = null;
    private static Messager messager = null;
    private static CommandRegistrar commandRegistrar = null;

    private static final Map<String, RavelConfig> configs = new HashMap<>();

    public static void init(RavelMain main, RavelLogger logger, CommandRegistrar commandRegistrar, Path dataPath) {
        RavelInstance.main = main;
        RavelInstance.logger = logger;
        RavelInstance.commandRegistrar = commandRegistrar;
        RavelInstance.dataPath = dataPath;
        logger.info("RavelCraft is initializing...");

        //Setup languages
        Text.init();

        //Setup server info
        RavelConfig config = getConfig();
        if (config.contains("server-name")) {
            try {
                server = RavelServer.valueOf(config.getString("server-name"));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid server name specified in config.yml!");
            }
        } else {
            config.set("server-name", "SERVER NAME");
            config.save();

            throw new RuntimeException("No server name specified in config.yml!");
        }

        logger.info("Server identified as: " + server.name());

        //Setup messaging
        if (MessagingConstants.isServer()) {
            messager = new MessagingServer();
        } else {
            String hostname;
            if (config.contains("messaging-server")) {
                hostname = config.getString("messaging-server");
            } else {
                config.set("messaging-server", "server.domain.name");
                config.save();

                throw new RuntimeException("No messaging server hostname specified in config.yml!");
            }

            messager = new MessagingClient(hostname);
        }

        //Setup commands
        commandRegistrar.register();
    }

    public static void shutdown() {
        logger.info("RavelCraft is shutting down...");
        messager.close();
    }

    public static RavelMain getMain() {
        return main;
    }

    public static RavelLogger getLogger() {
        return logger;
    }

    public static RavelServer getServer() {
        return server;
    }

    public static Messager getMessager() {
        return messager;
    }

    public static CommandRegistrar getCommandRegistrar() {
        return commandRegistrar;
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
