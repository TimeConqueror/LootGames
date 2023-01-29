package ru.timeconqueror.lootgames.common;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;

public class LGItemGroup extends CreativeModeTab {
    public static final LGItemGroup MAIN = new LGItemGroup(LootGames.MODID);

    public LGItemGroup(String label) {
        super(label);
    }

    @NotNull
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(LGBlocks.PUZZLE_MASTER);
    }
}
