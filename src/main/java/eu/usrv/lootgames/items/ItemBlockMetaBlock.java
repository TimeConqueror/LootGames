package eu.usrv.lootgames.items;


import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;


public class ItemBlockMetaBlock extends ItemBlockWithMetadata {

    public ItemBlockMetaBlock(Block pBlock) {
        super(pBlock, pBlock);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + "_" + stack.getItemDamage();
    }
}
