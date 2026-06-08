package ru.timeconqueror.lootgames.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.client.render.LGBlockEntityWithoutLevelRenderer;
import ru.timeconqueror.lootgames.registry.LGDimensions;
import ru.timeconqueror.timecore.api.util.holder.Late;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LGClientLoadEventHandler {
    public static final Late<LGBlockEntityWithoutLevelRenderer> BLOCK_ENTITY_WITHOUT_LEVEL_RENDERER = new Late.Insertable<>();

    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(LGDimensions.TEST_SITE_DIM_TYPE.location(), new RoomWorldSpecialEffects());
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        EntityModelSet entityModels = Minecraft.getInstance().getEntityModels();

        if (!BLOCK_ENTITY_WITHOUT_LEVEL_RENDERER.isPresent()) {
            Late.Insertable.cast(BLOCK_ENTITY_WITHOUT_LEVEL_RENDERER)
                    .set(new LGBlockEntityWithoutLevelRenderer(blockEntityRenderDispatcher, entityModels));
        }

        event.registerReloadListener(BLOCK_ENTITY_WITHOUT_LEVEL_RENDERER.get());
    }
}
