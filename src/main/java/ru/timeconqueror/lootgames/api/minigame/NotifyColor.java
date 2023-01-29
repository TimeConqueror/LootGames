package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.ChatFormatting;

/**
 * Default notify colors.
 */
public enum NotifyColor {
    SUCCESS(ChatFormatting.GREEN),
    WARN(ChatFormatting.YELLOW),
    NOTIFY(ChatFormatting.AQUA),
    FAIL(ChatFormatting.DARK_PURPLE),
    GRAVE_NOTIFY(ChatFormatting.RED);

    private final ChatFormatting color;

    NotifyColor(ChatFormatting color) {
        this.color = color;
    }

    public ChatFormatting getColor() {
        return color;
    }
}
