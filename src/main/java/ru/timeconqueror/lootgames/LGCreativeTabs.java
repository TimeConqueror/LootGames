package ru.timeconqueror.lootgames;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.registry.ModBlocks;

public class LGCreativeTabs extends CreativeTabs {

    public LGCreativeTabs(int index, String label) {
        super(index, label);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.PUZZLE_MASTER);
    }

}
