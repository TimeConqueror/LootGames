package ru.timeconqueror.lootgames;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;
import ru.timeconqueror.lootgames.common.packet.CPacketCycleBlock;
import ru.timeconqueror.lootgames.manager.BlockCycleManager;
import ru.timeconqueror.lootgames.manager.LootGamesManagers;

public class ServerPacketHandler {
    public static void onCycleBlockPacketReceived(CPacketCycleBlock packet, NetworkEvent.Context ctx) {
        ServerPlayer sender = ctx.getSender();
        if(sender == null) return;

        Inventory inventory = sender.getInventory();

        ItemStack selected = inventory.getSelected();
        if(selected.isEmpty() || !(selected.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        Block block = blockItem.getBlock();
        //noinspection ConstantValue
        if(block == null) {
            return;
        }

        BlockCycleManager manager = LootGamesManagers.getBlockCycleManager();
        Block replace = packet.isForward() ? manager.next(block) : manager.prev(block);
        if(replace == null) return;

        int selectedIndex = inventory.selected;

        inventory.setItem(selectedIndex, new ItemStack(replace.asItem(), selected.getCount()));
    }
}
