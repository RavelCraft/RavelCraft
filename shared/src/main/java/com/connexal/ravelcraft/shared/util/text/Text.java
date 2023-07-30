package com.connexal.ravelcraft.shared.util.text;

import com.connexal.ravelcraft.shared.RavelInstance;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public enum Text {
    CHAT_FORMAT("ravelcraft.chat.format"),

    PLAYERS_JOIN_NETWORK("ravelcraft.players.join.network"),
    PLAYERS_JOIN_SERVER("ravelcraft.players.join.server"),
    PLAYERS_LEAVE_NETWORK("ravelcraft.players.leave.network"),
    PLAYERS_LEAVE_SERVER("ravelcraft.players.leave.server"),

    COMMAND_REQUIRES_OP("ravelcraft.command.requires-op"),
    COMMAND_HELP("ravelcraft.command.help"),
    COMMAND_MUST_BE_PLAYER("ravelcraft.command.must-be-player"),
    COMMAND_PLAYER_NOT_FOUND("ravelcraft.command.player-not-found"),

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
    COMMAND_SERVER_ALREADY("ravelcraft.command.server.already");

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
