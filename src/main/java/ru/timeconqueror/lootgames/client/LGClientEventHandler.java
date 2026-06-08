package ru.timeconqueror.lootgames.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.common.packet.CPacketCycleBlock;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.manager.BlockCycleManager;
import ru.timeconqueror.lootgames.manager.LootGamesManagers;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class LGClientEventHandler {
    @SubscribeEvent
    public static void onPlayerScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if(mc.screen != null || player == null || !mc.options.keySprint.isDown()) {
            return;
        }

        Inventory inventory = mc.player.getInventory();
        ItemStack selected = inventory.getSelected();
        if(selected.isEmpty() || !(selected.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        Block block = blockItem.getBlock();
        BlockCycleManager manager = LootGamesManagers.getBlockCycleManager();
        if (!manager.canBeCycled(block)) {
            return;
        }

        int signum = (int)Math.signum(event.getScrollDelta());
        if(signum == 0) {
            return;
        }

        LGNetwork.INSTANCE.sendToServer(new CPacketCycleBlock(signum > 0));
        event.setCanceled(true);
    }
}
