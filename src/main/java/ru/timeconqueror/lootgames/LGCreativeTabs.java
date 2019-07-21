package ru.timeconqueror.lootgames;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.registry.ModBlocks;

public class LGCreativeTabs extends CreativeTabs {
    private ItemStack tabIcon = ItemStack.EMPTY;

    public LGCreativeTabs(int index, String label) {
        super(index, label);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        if (tabIcon == ItemStack.EMPTY) {
            tabIcon = new ItemStack(ModBlocks.PUZZLE_MASTER);
        }

        return tabIcon;
    }

}
