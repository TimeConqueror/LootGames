package ru.timeconqueror.lootgames;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.registry.ModBlocks;

public class ModCreativeTab extends CreativeTabs {
    public static CreativeTabs lootGames = new ModCreativeTab(CreativeTabs.getNextID(), LootGames.MODID);
    private static ItemStack tabIcon = new ItemStack(ModBlocks.PUZZLE_MASTER);

    public ModCreativeTab(final int index, final String label) {
        super(index, label);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        return tabIcon;
    }
}
