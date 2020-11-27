package ru.timeconqueror.lootgames.common;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;

public class LGItemGroup extends ItemGroup {
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
