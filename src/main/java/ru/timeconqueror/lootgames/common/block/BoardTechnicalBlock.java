package ru.timeconqueror.lootgames.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import ru.timeconqueror.lootgames.api.minigame.BoardLootGame;

public class BoardTechnicalBlock extends TechnicalBlock {
    private static final VoxelShape OUTLINE_SHAPE = Shapes.box(0, 0, 0, 1, 1 + BoardLootGame.VERTICAL_RENDER_OFFSET, 1);

    public BoardTechnicalBlock(Properties properties, Configuration configuration) {
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
