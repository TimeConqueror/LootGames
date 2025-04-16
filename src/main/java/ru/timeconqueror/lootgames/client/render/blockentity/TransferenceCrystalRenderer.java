package ru.timeconqueror.lootgames.client.render.blockentity;

import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.client.BlockModels;
import ru.timeconqueror.lootgames.common.block.entity.TransferenceCrystalBlockEntity;
import ru.timeconqueror.timecore.animation.renderer.AnimatedBlockEntityRenderer;
import ru.timeconqueror.timecore.animation.renderer.ModelConfiguration;
import ru.timeconqueror.timecore.client.render.model.TimeModel;

@Log4j2
public class TransferenceCrystalRenderer extends AnimatedBlockEntityRenderer<TransferenceCrystalBlockEntity> {
    public static final ResourceLocation BASE = LootGames.tl("block/transference_crystal.png").fullLocation();

    public TransferenceCrystalRenderer(BlockEntityRendererProvider.Context context) {
        super(new TimeModel(ModelConfiguration.builder(BlockModels.TRANSFERENCE_CRYSTAL).build()));
    }

    @Override
    protected ResourceLocation getTexture(TransferenceCrystalBlockEntity blockEntity) {
        return BASE;
    }
}
