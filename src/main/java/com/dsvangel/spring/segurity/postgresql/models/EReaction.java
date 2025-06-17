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

    // Usar el emoji de texto por defecto para evitar problemas de codificación
    public String getEmoji() {
        return textEmoji;
    }

    // Para el frontend, intentar usar Unicode si está disponible
    public String getDisplayEmoji() {
        return unicodeEmoji;
    }

    @Override
    public String toString() {
        return textEmoji;
    }
}