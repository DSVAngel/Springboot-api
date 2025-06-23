package com.dsvangel.spring.segurity.postgresql.models;

public enum EReaction {
    LIKE(":thumbsup:", "👍"),
    LOVE(":heart:", "❤️"),
    LAUGH(":joy:", "😂"),
    WOW(":open_mouth:", "😮"),
    SAD(":cry:", "😢"),
    ANGRY(":rage:", "😡");

    private final String textEmoji;
    private final String unicodeEmoji;

    EReaction(String textEmoji, String unicodeEmoji) {
        this.textEmoji = textEmoji;
        this.unicodeEmoji = unicodeEmoji;
    }

    public String getTextEmoji() {
        return textEmoji;
    }

    public String getUnicodeEmoji() {
        return unicodeEmoji;
    }

    // Usar el emoji Unicode por defecto
    public String getEmoji() {
        return unicodeEmoji;
    }

    // Para el frontend, usar Unicode
    public String getDisplayEmoji() {
        return unicodeEmoji;
    }

    @Override
    public String toString() {
        return unicodeEmoji;
    }
}