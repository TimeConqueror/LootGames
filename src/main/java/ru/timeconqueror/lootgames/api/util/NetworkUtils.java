package ru.timeconqueror.lootgames.api.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * @apiNote For use on logical server side <i>(when !world.isRemote)</i>.
 */
public class NetworkUtils {
    /**
     * Sends colored message.
     */
    public static void sendColoredMessage(EntityPlayer p, ITextComponent msg, TextFormatting color) {
        msg.getStyle().setColor(color);
        p.sendMessage(msg);
    }

    /**
     * Sends message to all players in given distance.
     *
     * @param distanceIn distance from {@code fromPos}, in which players will be get a message.
     */
    public static void sendMessageToAllNearby(BlockPos fromPos, ITextComponent msg, int distanceIn) {
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            double distance = player.getDistance(fromPos.getX(), fromPos.getY(), fromPos.getZ());
            if (distance <= distanceIn) {
                player.sendMessage(msg);
            }
        }
    }

    /**
     * Sends colored message to all players in given distance.
     *
     * @param distanceIn distance from {@code fromPos}, in which players will be get a message.
     */
    public static void sendColoredMessageToAllNearby(BlockPos fromPos, ITextComponent msg, TextFormatting color, int distanceIn) {
        msg.getStyle().setColor(color);

        sendMessageToAllNearby(fromPos, msg, distanceIn);
    }
}
