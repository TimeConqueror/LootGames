package ru.timeconqueror.lootgames.client;

import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.common.packet.CPacketCycleBlock;
import ru.timeconqueror.lootgames.common.packet.LGNetwork;
import ru.timeconqueror.lootgames.manager.LootGamesManagers;
import ru.timeconqueror.lootgames.utils.Utils;

import java.util.List;

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
        Block block = Utils.getBlockFromStack(selected);
        if(block == null) return;

        if(!LootGamesManagers.getBlockCycleManager().canBeCycled(block)) {
            return;
        }

        int signum = (int)Math.signum(event.getScrollDelta());
        if(signum == 0) {
            return;
        }

        LGNetwork.INSTANCE.sendToServer(new CPacketCycleBlock(signum > 0));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onItemTooltipGathered(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        Block block = Utils.getBlockFromStack(stack);

        if (LootGamesManagers.getBlockCycleManager().canBeCycled(block)) {
            Component keyDisplayName = Minecraft.getInstance().options.keySprint.getKey().getDisplayName();
            List<Either<FormattedText, TooltipComponent>> elements = event.getTooltipElements();
            int index = Math.max(elements.size() - 1, 0);
            elements.add(index, Either.left(Component.translatable("tooltip.lootgames.cycled_block", keyDisplayName)
                    .withStyle(ChatFormatting.DARK_GRAY)));
        }
    }
}
