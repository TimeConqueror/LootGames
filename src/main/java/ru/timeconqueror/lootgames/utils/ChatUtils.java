package ru.timeconqueror.lootgames.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.timeconqueror.timecore.util.NetworkUtils;
import ru.timeconqueror.timecore.util.Pair;

import java.util.Arrays;

public class ChatUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Sends an error message to the player and prints extra info to the log file.
     *
     * @param modId     id of your mod
     * @param player    player to send the message
     * @param msg       message to send
     * @param extraInfo extra info that is present as the "name->value" pair array
     */
    @SafeVarargs
    public static void sendInformativeError(String modId, PlayerEntity player, String msg, Pair<String, Object>... extraInfo) {
        sendInformativeError(modId, player, msg, new Exception(""));
    }

    /**
     * Sends an error message to the player and prints extra info to the log file.
     *
     * @param modId     id of your mod
     * @param player    player to send the message
     * @param msg       message to send
     * @param exception exception that will be printed to logs.
     * @param extraInfo extra info that is present as the "name->value" pair array
     */
    @SafeVarargs
    public static void sendInformativeError(String modId, PlayerEntity player, String msg, Exception exception, Pair<String, Object>... extraInfo) {
        NetworkUtils.sendMessage(player, new TranslationTextComponent("msg.timecore.chat_error_header", msg).withStyle(TextFormatting.RED));
        NetworkUtils.sendMessage(player, new TranslationTextComponent("msg.timecore.chat_error_contact_us", modId).withStyle(TextFormatting.RED));
        LOGGER.error(msg);
        LOGGER.error("Extra information: " + Arrays.toString(extraInfo));
        LOGGER.error(exception);
    }
}
