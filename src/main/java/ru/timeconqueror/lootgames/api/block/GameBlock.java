package ru.timeconqueror.lootgames.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import ru.timeconqueror.timecore.api.registry.util.BlockPropsFactory;

public abstract class GameBlock extends Block {
    public static final BlockPropsFactory DEF_PROPS = new BlockPropsFactory(() ->
            BlockPropsFactory.unbreakable(Properties.of(Material.BARRIER)
                    .lightLevel(value -> 1))
    );

    public GameBlock() {
        super(DEF_PROPS.create());
    }

    public GameBlock(Properties props) {
        super(props);
    }

    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return false;
    }
}
