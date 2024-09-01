package ru.timeconqueror.tc;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerUtils {
    public static void addToInventoryOrDrop(Player player, InteractionHand hand, ItemStack stack) {
        // copied from FlowerPotBlock#use
        if (player.getItemInHand(hand) == ItemStack.EMPTY) {
            player.setItemInHand(hand, stack);
        } else if (!player.addItem(stack)) {
            player.drop(stack, false);
        }
    }
}
