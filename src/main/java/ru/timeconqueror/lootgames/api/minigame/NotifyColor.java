package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.util.text.TextFormatting;

/**
 * Default notify colors.
 */
public enum NotifyColor {
    SUCCESS(TextFormatting.GREEN),
    WARN(TextFormatting.YELLOW),
    NOTIFY(TextFormatting.AQUA),
    FAIL(TextFormatting.DARK_PURPLE),
    GRAVE_NOTIFY(TextFormatting.RED);

    private final TextFormatting color;

    NotifyColor(TextFormatting color) {
        this.color = color;
    }

    public TextFormatting getColor() {
        return color;
    }
}
