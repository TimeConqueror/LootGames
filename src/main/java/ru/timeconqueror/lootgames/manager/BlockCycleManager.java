package ru.timeconqueror.lootgames.manager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.custom.BlockVariations;
import ru.timeconqueror.lootgames.utils.ItemStackExt;
import ru.timeconqueror.timecore.api.registry.util.Promised;
import ru.timeconqueror.timecore.api.util.EnvironmentUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BlockCycleManager {
    private final Map<ResourceLocation, Supplier<? extends Block>> nextBlock = new HashMap<>();
    private final Map<ResourceLocation, Supplier<? extends Block>> prevBlock = new HashMap<>();

    public void init() {
        addCycledVariations(List.of(
                LGBlocks.Variations.RIBBED_XENOLITH,
                LGBlocks.Variations.RIBBED_XENOLITH_90,
                LGBlocks.Variations.RIBBED_XENOLITH_180,
                LGBlocks.Variations.RIBBED_XENOLITH_270
        ));
        addCycledVariations(List.of(
                LGBlocks.Variations.WIDE_RIBBED_XENOLITH,
                LGBlocks.Variations.WIDE_RIBBED_XENOLITH_90,
                LGBlocks.Variations.WIDE_RIBBED_XENOLITH_180,
                LGBlocks.Variations.WIDE_RIBBED_XENOLITH_270
        ));
        addCycledVariations(List.of(
                LGBlocks.Variations.TILED_XENOLITH,
                LGBlocks.Variations.TILED_XENOLITH_90,
                LGBlocks.Variations.TILED_XENOLITH_180,
                LGBlocks.Variations.TILED_XENOLITH_270
        ));
        addCycledVariations(List.of(
                LGBlocks.Variations.WAVED_XENOLITH,
                LGBlocks.Variations.WAVED_XENOLITH_90
        ));
        addCycledVariations(List.of(
                LGBlocks.Variations.CHEVRON_XENOLITH,
                LGBlocks.Variations.CHEVRON_XENOLITH_90,
                LGBlocks.Variations.CHEVRON_XENOLITH_180,
                LGBlocks.Variations.CHEVRON_XENOLITH_270
        ));
        addCycledVariations(List.of(
                LGBlocks.Variations.FIBROUS_XENOLITH,
                LGBlocks.Variations.FIBROUS_XENOLITH_90,
                LGBlocks.Variations.FIBROUS_XENOLITH_180,
                LGBlocks.Variations.FIBROUS_XENOLITH_270
        ));
    }

    public boolean canBeCycled(Block block) {
        return prev(block) != null && next(block) != null;
    }

    public void addCycledVariations(List<BlockVariations<?>> variations) {
        List<Promised<? extends Block>> blocks = new ArrayList<>(variations.size());
        List<Promised<? extends Block>> slabs = new ArrayList<>(variations.size());
        List<Promised<? extends Block>> stairs = new ArrayList<>(variations.size());

        for (BlockVariations<?> variation : variations) {
            blocks.add(variation.getPromisedBlock());
            slabs.add(variation.getPromisedSlab());
            stairs.add(variation.getPromisedStairs());
        }

        addCycled(blocks);
        addCycled(slabs);
        addCycled(stairs);
    }

    public void addCycled(List<Promised<? extends Block>> cycledBlocks) {
        if(cycledBlocks.size() < 2) {
            throw new IllegalArgumentException("Too few blocks in input list: %d".formatted(cycledBlocks.size()));
        }

        for (int i = 0; i < cycledBlocks.size(); i++) {
            Promised<? extends Block> cycledBlock = cycledBlocks.get(i);
            ResourceLocation id = cycledBlock.getId();
            if (nextBlock.containsKey(id) || prevBlock.containsKey(id)) {
                throw new IllegalArgumentException("Block %s is already in cycle map".formatted(id));
            }

            int next = i != cycledBlocks.size() - 1 ? i + 1 : 0;
            int prev = i != 0 ? i - 1 : cycledBlocks.size() - 1;

            Promised<? extends Block> nextBlock = cycledBlocks.get(next);
            Promised<? extends Block> prevBlock = cycledBlocks.get(prev);

            this.nextBlock.put(id, nextBlock);
            this.prevBlock.put(id, prevBlock);
        }
    }

    @Nullable
    public Block next(Block current) {
        ResourceLocation key = EnvironmentUtils.getKey(current);
        if(!nextBlock.containsKey(key)) {
            return null;
        }

        return nextBlock.get(key).get();
    }

    @Nullable
    public Block prev(Block current) {
        ResourceLocation key = EnvironmentUtils.getKey(current);
        if(!prevBlock.containsKey(key)) {
            return null;
        }
        return prevBlock.get(key).get();
    }
}
