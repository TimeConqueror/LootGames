package ru.timeconqueror.lootgames.common;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import ru.timeconqueror.lootgames.LootGames;

public class LGItemGroup extends ItemGroup {
    public static final LGItemGroup MAIN = new LGItemGroup(LootGames.MODID);

    public LGItemGroup(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.ACACIA_BUTTON);//FIXME CHANGE
    }
}
