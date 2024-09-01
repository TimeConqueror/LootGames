package ru.timeconqueror.lootgames.client.render.blockentity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.client.BlockModels;
import ru.timeconqueror.lootgames.common.block.entity.MicroverseManipulatorBlockEntity;
import ru.timeconqueror.timecore.animation.renderer.AnimatedBlockEntityRenderer;
import ru.timeconqueror.timecore.animation.renderer.ModelConfiguration;
import ru.timeconqueror.timecore.client.render.model.TimeModel;
import ru.timeconqueror.timecore.client.render.processor.PropertyBasedRotationProcessor;
import ru.timeconqueror.timecore.client.render.processor.RotationProcessor;

public class MicroverseManipulatorRenderer extends AnimatedBlockEntityRenderer<MicroverseManipulatorBlockEntity> {
    public static final ResourceLocation BASE = LootGames.tl("blockentity/microverse_manipulator_base.png").fullLocation();

    public MicroverseManipulatorRenderer(BlockEntityRendererProvider.Context context) {
        super(new TimeModel(ModelConfiguration.builder(BlockModels.MICROVERSE_MANIPULATOR).build()));

        getPuppeteer().addModelProcessor(RotationProcessor.byDegrees(Direction.Axis.Y, 180));
        getPuppeteer().addModelProcessor(PropertyBasedRotationProcessor.horizontalDirectionalBased());
    }

    @Override
    protected ResourceLocation getTexture(MicroverseManipulatorBlockEntity tileEntityIn) {
        return BASE;
    }
}
