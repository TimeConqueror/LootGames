package ru.timeconqueror.lootgames.api.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class NetworkUtils {
    public static void sendColoredMessage(EntityPlayer p, ITextComponent msg, TextFormatting color) {
        msg.getStyle().setColor(color);
        p.sendMessage(msg);
    }
}
