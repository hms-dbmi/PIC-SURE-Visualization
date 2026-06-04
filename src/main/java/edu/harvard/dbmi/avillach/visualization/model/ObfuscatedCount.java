package edu.harvard.dbmi.avillach.visualization.model;

public record ObfuscatedCount(int count, String display) {

    /**
     * Wraps a plain (non-obfuscated) integer count. The display is just the
     * stringified number; this is the right factory for the authorized path
     * where no threshold floor or variance applies.
     */
    public static ObfuscatedCount ofInt(int count) {
        return new ObfuscatedCount(count, Integer.toString(count));
    }
}
