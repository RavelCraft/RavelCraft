package com.connexal.ravelcraft.shared.util.text;

public enum Language {
    ENGLISH("en"),
    FRENCH("fr");

    public static final Language DEFAULT = ENGLISH;

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
