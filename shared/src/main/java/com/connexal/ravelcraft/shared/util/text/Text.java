package com.connexal.ravelcraft.shared.util.text;

import com.connexal.ravelcraft.shared.RavelInstance;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public enum Text {
    //--- General ---
    COMMAND_REQUIRES_OP("ravelcraft.command.requires-op"),
    COMMAND_HELP("ravelcraft.command.help"),
    COMMAND_MUST_BE_PLAYER("ravelcraft.command.must-be-player"),
    COMMAND_PLAYER_NOT_FOUND("ravelcraft.command.player-not-found"),

    //--- Proxy ---
    CHAT_FORMAT("ravelcraft.chat.format"),

    PLAYERS_JOIN_NETWORK("ravelcraft.players.join.network"),
    PLAYERS_JOIN_SERVER("ravelcraft.players.join.server"),
    PLAYERS_LEAVE_NETWORK("ravelcraft.players.leave.network"),
    PLAYERS_LEAVE_SERVER("ravelcraft.players.leave.server"),

    PLAYERS_NOT_WHITELISTED_BACKEND("ravelcraft.players.not-whitelisted.backend"),
    PLAYERS_MAINTENANCE("ravelcraft.players.maintenance"),

    COMMAND_INFO("ravelcraft.command.info"),

    COMMAND_BROADCAST("ravelcraft.command.broadcast"),

    COMMAND_LANGUAGE_LIST("ravelcraft.command.language.list"),
    COMMAND_LANGUAGE_SET("ravelcraft.command.language.set"),
    COMMAND_LANGUAGE_INVALID("ravelcraft.command.language.invalid"),

    COMMAND_KICK_SUCCESS("ravelcraft.command.kick.success"),
    COMMAND_KICK_FAIL("ravelcraft.command.kick.fail"),

    COMMAND_RANK_LIST("ravelcraft.command.rank.list"),
    COMMAND_RANK_SET("ravelcraft.command.rank.set"),
    COMMAND_RANK_INVALID("ravelcraft.command.rank.invalid"),
    COMMAND_RANK_GET("ravelcraft.command.rank.get"),

    COMMAND_SERVER_INVALID("ravelcraft.command.server.invalid"),
    COMMAND_SERVER_SUCCESS_OTHER("ravelcraft.command.server.success.other"),
    COMMAND_SERVER_FAIL_SLEF("ravelcraft.command.server.fail.self"),
    COMMAND_SERVER_FAIL_OTHER("ravelcraft.command.server.fail.other"),
    COMMAND_SERVER_ALREADY("ravelcraft.command.server.already"),

    COMMAND_LIST_NO_PLAYERS("ravelcraft.command.list.no-players"),
    COMMAND_LIST_PLAYERS("ravelcraft.command.list.players"),

    COMMAND_MOTD_GET("ravelcraft.command.motd.get"),
    COMMAND_MOTD_SET("ravelcraft.command.motd.set"),

    COMMAND_WHITELIST_INVALID_SERVER("ravelcraft.command.whitelist.invalid-server"),
    COMMAND_WHITELIST_ENABLE_BOOL_INVALID("ravelcraft.command.whitelist.enable-bool-invalid"),
    COMMAND_WHITELIST_ENABLE_TRUE("ravelcraft.command.whitelist.enable-true"),
    COMMAND_WHITELIST_ENABLE_FALSE("ravelcraft.command.whitelist.enable-false"),
    COMMAND_WHITELIST_GLOBAL_ADD("ravelcraft.command.whitelist.global-add"),
    COMMAND_WHITELIST_GLOBAL_REMOVE("ravelcraft.command.whitelist.global-remove"),
    COMMAND_WHITELIST_BACKEND_ADD("ravelcraft.command.whitelist.backend-add"),
    COMMAND_WHITELIST_BACKEND_REMOVE("ravelcraft.command.whitelist.backend-remove"),

    COMMAND_BAN_INVALID_DAYS("ravelcraft.command.ban.invalid-days"),
    COMMAND_BAN_ALREADY_BANNED("ravelcraft.command.ban.already-banned"),
    COMMAND_BAN_SUCCESS("ravelcraft.command.ban.success"),

    COMMAND_PARDON_NOT_BANNED("ravelcraft.command.pardon.not-banned"),
    COMMAND_PARDON_SUCCESS("ravelcraft.command.pardon.success"),

    COMMAND_MAINTENANCE_INVALID_SERVER("ravelcraft.command.maintenance.invalid-server"),
    COMMAND_MAINTENANCE_GLOBAL_ENABLED("ravelcraft.command.maintenance.global-enabled"),
    COMMAND_MAINTENANCE_GLOBAL_DISABLED("ravelcraft.command.maintenance.global-disabled"),
    COMMAND_MAINTENANCE_BACKEND_ENABLED("ravelcraft.command.maintenance.backend-enabled"),
    COMMAND_MAINTENANCE_BACKEND_DISABLED("ravelcraft.command.maintenance.backend-disabled"),

    //--- Backend ---
    COMMAND_SPAWN_NOT_SET("ravelcraft.command.spawn.not-set"),
    COMMAND_SPAWN_SET("ravelcraft.command.spawn.set"),
    COMMAND_SPAWN_TELEPORT("ravelcraft.command.spawn.teleport"),

    COMMAND_KILL("ravelcraft.command.kill"),

    COMMAND_HOME_INVALID_NUMBER("ravelcraft.command.home.invalid-number"),
    COMMAND_HOME_OUT_OF_BOUNDS("ravelcraft.command.home.out-of-bounds"),
    COMMAND_HOME_NOT_SET("ravelcraft.command.home.not-set"),
    COMMAND_HOME_TELEPORTED("ravelcraft.command.home.teleported"),
    COMMAND_HOME_SET("ravelcraft.command.home.set"),
    COMMAND_HOME_GET("ravelcraft.command.home.get"),
    COMMAND_HOME_DELETED("ravelcraft.command.home.deleted");

    private static final Map<Language, Properties> languages = new HashMap<>();

    public static void init() {
        for (Language language : Language.values()) {
            InputStream localeStream = RavelInstance.getMain().getResource("ravel-lang/" + language.getCode() + ".properties");
            if (localeStream == null) {
                throw new RuntimeException("Could not find locale file for language " + language.getCode());
            }

            Properties properties = new Properties();
            try {
                properties.load(localeStream);
            } catch (Exception e) {
                throw new RuntimeException("Could not load locale file for language " + language.getCode());
            }

            languages.put(language, properties);
        }
    }

    public static String getFormatString(Language language, String key) {
        Properties properties = languages.get(language);
        if (properties == null) {
            return null;
        }

        return properties.getProperty(key);
    }

    private final String key;

    Text(String key) {
        this.key = key;
    }

    public String getMessage(Language language, Object[] values) {
        String formatString = getFormatString(language, this.key);
        if (formatString == null) {
            throw new IllegalArgumentException("String not found for text " + this.key + " in language " + language.getCode());
        }

        if (values == null || values.length == 0) {
            return formatString;
        }

        return MessageFormat.format(formatString, values);
    }
}
