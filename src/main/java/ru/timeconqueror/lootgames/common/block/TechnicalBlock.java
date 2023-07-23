package ru.timeconqueror.lootgames.common.block;

import lombok.Builder;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("deprecation")
public class TechnicalBlock extends Block {
    @Getter
    private final Configuration configuration;

    public TechnicalBlock(Properties properties, Configuration configuration) {
        super(properties);
        this.configuration = configuration;
    }

    @Override
    public void fallOn(Level world_, BlockState state_, BlockPos pos_, Entity entity_, float fallDistance_) {
        // make no damage
    }

    @Override
    public RenderShape getRenderShape(BlockState state_) {
        return configuration.renderShape != null ? configuration.renderShape : super.getRenderShape(state_);
    }

    @Builder
    public static class Configuration {
        @Nullable
        private RenderShape renderShape;
    }
}
