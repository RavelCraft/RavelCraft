package com.connexal.ravelcraft.shared.server.util.uuid;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.UUID;

class UUIDTypeAdapter extends TypeAdapter<UUID> {
    public void write(final JsonWriter out, final UUID value) throws IOException {
        out.value(fromUUID(value));
    }

    public UUID read(final JsonReader in) throws IOException {
        return fromString(in.nextString());
    }

    public static String fromUUID(final UUID value) {
        return value.toString().replace("-", "");
    }

    public static UUID fromString(final String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    public static UUID fromXUID(final String input) {
        long xuid;
        try {
            xuid = Long.parseLong(input);
        } catch (NumberFormatException e) {
            return null;
        }

        return new UUID(0, xuid);
    }

    public static String toXUID(final UUID value) {
        return Long.toString(value.getLeastSignificantBits());
    }
}
