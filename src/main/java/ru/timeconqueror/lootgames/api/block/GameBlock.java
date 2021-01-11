package ru.timeconqueror.lootgames.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.Nullable;
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
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return false;
    }
}
