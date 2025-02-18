package com.connexal.ravelcraft.shared.all.text;

import com.connexal.ravelcraft.shared.all.RavelMain;
import com.google.common.collect.ImmutableMap;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public enum RavelText {
    //--- General ---
    COMMAND_REQUIRES_OP("ravelcraft.command.requires-op"),
    COMMAND_HELP("ravelcraft.command.help"),
    COMMAND_MUST_BE_PLAYER("ravelcraft.command.must-be-player"),
    COMMAND_PLAYER_NOT_FOUND("ravelcraft.command.player-not-found"),
    COMMAND_PLAYER_NOT_ONLINE("ravelcraft.command.player-not-online"),

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

    PLAYER_DISPLAY_SERVER_NAME("ravelcraft.player.display.server-name"),

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
    COMMAND_HOME_DELETED("ravelcraft.command.home.deleted"),

    COMMAND_TPA_STR_SENT("ravelcraft.command.tpa.str.sent"),
    COMMAND_TPA_STR_RECEIVED("ravelcraft.command.tpa.str.received"),
    COMMAND_TPA_RTS_SENT("ravelcraft.command.tpa.rts.sent"),
    COMMAND_TPA_RTS_RECEIVED("ravelcraft.command.tpa.rts.received"),
    COMMAND_TPA_DENY_SENT("ravelcraft.command.tpa.deny.sent"),
    COMMAND_TPA_DENY_RECEIVED("ravelcraft.command.tpa.deny.received"),
    COMMAND_TPA_ACCEPT_SENT("ravelcraft.command.tpa.accept.sent"),
    COMMAND_TPA_ACCEPT_RECEIVED("ravelcraft.command.tpa.accept.received"),
    COMMAND_TPA_EXPIRED_SENDER("ravelcraft.command.tpa.expired.sender"),
    COMMAND_TPA_EXPIRED_RECEIVER("ravelcraft.command.tpa.expired.receiver"),
    COMMAND_TPA_NO_REQUESTS("ravelcraft.command.tpa.no-requests"),
    COMMAND_TPA_REQUEST_PENDING("ravelcraft.command.tpa.request-pending"),
    COMMAND_TPA_SELF("ravelcraft.command.tpa.self"),

    COMMAND_MAP_NAN("ravelcraft.command.map.nan"),
    COMMAND_MAP_INVALID_URL("ravelcraft.command.map.invalid.url"),
    COMMAND_MAP_INVALID_ID("ravelcraft.command.map.invalid.id"),
    COMMAND_MAP_DONE("ravelcraft.command.map.done"),

    COMMAND_MSG_SENDER("ravelcraft.command.msg.sender"),
    COMMAND_MSG_RECEIVER("ravelcraft.command.msg.receiver"),

    JOIN_INFO_RULES("ravelcraft.join-info.rules"),
    JOIN_INFO_ANNOUNCEMENTS("ravelcraft.join-info.announcements"),
    JOIN_INFO_LANGUAGES("ravelcraft.join-info.languages"),

    // --- Java proxy ---

    COMMAND_CRACKED_RELOAD("ravelcraft.command.cracked.reload");

    private static final ImmutableMap<Language, Properties> languages = buildLanguageFiles();

    public static void init() {
        //No-op
    }

    private static ImmutableMap<Language, Properties> buildLanguageFiles() {
        Map<Language, Properties> languages = new HashMap<>();

        for (Language language : Language.values()) {
            InputStream localeStream = RavelMain.get().getResource("ravel-lang/" + language.getCode() + ".properties");
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

        return ImmutableMap.copyOf(languages);
    }

    public static String getFormatString(Language language, String key) {
        Properties properties = languages.get(language);
        if (properties == null) {
            return null;
        }

        return properties.getProperty(key);
    }

    private final String key;

    RavelText(String key) {
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
