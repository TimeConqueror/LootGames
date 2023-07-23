package ru.timeconqueror.lootgames.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MinesweeperTechnicalBlock extends TechnicalBlock {
    public static final double VERTICAL_OFFSET = -0.35;
    private static final VoxelShape OUTLINE_SHAPE = Shapes.box(0, 0, 0, 1, 1 + VERTICAL_OFFSET, 1);

    public MinesweeperTechnicalBlock(Properties properties, Configuration configuration) {
        super(properties, configuration);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state_, BlockGetter world_, BlockPos pos_, CollisionContext context_) {
        return Shapes.block();
    }

    @Override
    // shape for highlighting on client
    public VoxelShape getShape(BlockState state_, BlockGetter world_, BlockPos pos_, CollisionContext context_) {
        return OUTLINE_SHAPE;
    }
}
