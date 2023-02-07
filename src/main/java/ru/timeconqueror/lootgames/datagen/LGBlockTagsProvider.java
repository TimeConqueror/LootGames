package ru.timeconqueror.lootgames.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.registry.LGBlocks;

import java.util.concurrent.CompletableFuture;

public class LGBlockTagsProvider extends BlockTagsProvider {

    public LGBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider_) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(LGBlocks.DUNGEON_CEILING)
                .add(LGBlocks.CRACKED_DUNGEON_CEILING)
                .add(LGBlocks.DUNGEON_WALL)
                .add(LGBlocks.CRACKED_DUNGEON_WALL)
                .add(LGBlocks.DUNGEON_FLOOR)
                .add(LGBlocks.CRACKED_DUNGEON_FLOOR)
                .add(LGBlocks.DUNGEON_LAMP)
                .add(LGBlocks.BROKEN_DUNGEON_LAMP)
                .replace(false);
    }
}
