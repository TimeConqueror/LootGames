package ru.timeconqueror.lootgames.common.handler;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.api.block.LeftInteractible;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void onBlockBreak(PlayerInteractEvent.LeftClickBlock event) {
        BlockState blockState = event.getLevel().getBlockState(event.getPos());
        Block block = blockState.getBlock();

        if (block instanceof LeftInteractible) {
            event.setCanceled(((LeftInteractible) block).onLeftClick(event.getLevel(), event.getEntity(), event.getPos(), event.getFace()));
        }
    }
}
