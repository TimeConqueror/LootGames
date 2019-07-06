package ru.timeconqueror.lootgames;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModCreativeTab extends CreativeTabs {
    public static CreativeTabs lootGames = new ModCreativeTab(CreativeTabs.getNextID(), LootGames.MODID);
    //TODO change to Main Loot Game Block
    private static ItemStack tabIcon = new ItemStack(Items.ENCHANTED_BOOK);

    public ModCreativeTab(final int index, final String label) {
        super(index, label);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        return tabIcon;
    }
}
