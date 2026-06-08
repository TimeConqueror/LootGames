package ru.timeconqueror.lootgames.registry.custom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import ru.timeconqueror.timecore.api.registry.util.Promised;

@Getter
@RequiredArgsConstructor
public class BlockVariations<T extends Block> {
    private final Promised<T> promisedBlock;
    private final Promised<SlabBlock> promisedSlab;
    private final Promised<StairBlock> promisedStairs;

    public T block() {
        return promisedBlock.get();
    }

    public SlabBlock slab() {
        return promisedSlab.get();
    }

    public StairBlock stairs() {
        return promisedStairs.get();
    }
}
