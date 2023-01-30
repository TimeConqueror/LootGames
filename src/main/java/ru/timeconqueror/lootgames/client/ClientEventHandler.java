package ru.timeconqueror.lootgames.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.registry.LGBlocks;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void disableSubordinateHighlight(RenderHighlightEvent event) {
        HitResult target = event.getTarget();
        if (target.getType() == HitResult.Type.BLOCK && target instanceof BlockHitResult result) {
            if (Minecraft.getInstance().level.getBlockState(result.getBlockPos()).getBlock() == LGBlocks.SMART_SUBORDINATE) {
                event.setCanceled(true);
            }
        }
    }
}
