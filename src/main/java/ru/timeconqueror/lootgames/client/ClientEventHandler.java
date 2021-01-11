package ru.timeconqueror.lootgames.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.registry.LGBlocks;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void disableSubordinateHighlight(DrawHighlightEvent event) {
        RayTraceResult target = event.getTarget();
        if (target.getType() == RayTraceResult.Type.BLOCK && target instanceof BlockRayTraceResult) {
            BlockRayTraceResult result = (BlockRayTraceResult) target;
            if (Minecraft.getInstance().level.getBlockState(result.getBlockPos()).getBlock() == LGBlocks.SMART_SUBORDINATE) {
                event.setCanceled(true);
            }
        }
    }
}
