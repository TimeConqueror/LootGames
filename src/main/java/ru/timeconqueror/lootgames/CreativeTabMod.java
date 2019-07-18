package ru.timeconqueror.lootgames;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.registry.ModBlocks;

public class CreativeTabMod extends CreativeTabs {
    public static final CreativeTabMod TAB_LOOTGAMES = new CreativeTabMod(getNextID(), LootGames.MODID, Item.getItemFromBlock(ModBlocks.PUZZLE_MASTER));
    private Item tabItem;
    private ItemStack tabIcon;

    //TODO move creative tabs in TimeCore
    private CreativeTabMod(int index, String label, Item iconItem) {
        super(index, label);
        this.tabItem = iconItem;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.PUZZLE_MASTER);
    }

}
