package com.connexal.ravelcraft.shared.messaging;

public enum MessageFormat {
    SOURCE(0),
    DESTINATION(1),
    TYPE(2),
    COMMAND(3),
    RESPONSE_ID(4);

    private final int index;

    MessageFormat(int index) {
        this.index = index;
    }

    public int index() {
        return this.index;
    }

    public static int length() {
        return MessageFormat.values().length;
    }
}
