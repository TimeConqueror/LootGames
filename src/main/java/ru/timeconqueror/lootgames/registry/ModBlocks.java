package ru.timeconqueror.lootgames.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.block.BlockDungeonBricks;
import ru.timeconqueror.lootgames.proxy.ClientProxy;
import ru.timeconqueror.lootgames.proxy.CommonProxy;

public class ModBlocks {
    public static final BlockDungeonBricks DUNGEON_BRICKS = new BlockDungeonBricks();

    public static void register() {
        CommonProxy.registry.registerBlockWithItem(DUNGEON_BRICKS, "dungeon_bricks",
                new ItemMultiTexture(DUNGEON_BRICKS, DUNGEON_BRICKS, itemStack -> Integer.toString(itemStack.getMetadata())));
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers() {
        for (int i = 0; i < BlockDungeonBricks.EnumType.values().length; i++) {
            ClientProxy.registry.registerItemRender(Item.getItemFromBlock(DUNGEON_BRICKS), i,
                    BlockDungeonBricks.EnumType.byMetadata(i).getName());
        }
    }


//    private static void registerBlock(Block block, String name) {
//        ForgeRegistries.BLOCKS.register(block.setTranslationKey(LootGames.MODID + ":" + name).setRegistryName(LootGames.MODID, name));
//    }
//
//    private static void registerBlock(Block block, String name, ItemBlock itemBlock) {
//        ForgeRegistries.BLOCKS.register(block.setTranslationKey(LootGames.MODID + ":" + name).setRegistryName(LootGames.MODID, name).setCreativeTab(ModCreativeTab.lootGames));
//        ForgeRegistries.ITEMS.register(itemBlock.setRegistryName(LootGames.MODID, name));
//    }
//
//    private static void registerBlock(Block block, String name, ItemMultiTexture.Mapper mapper) {
//        registerBlock(block, name, new ItemMultiTexture(block, block, mapper));
//    }
//
//    private static void registerBlock(Block block, String name, ItemMultiTexture item) {
//        ForgeRegistries.BLOCKS.register(block.setTranslationKey(LootGames.MODID + ":" + name).setRegistryName(LootGames.MODID, name).setCreativeTab(ModCreativeTab.lootGames));
//        ForgeRegistries.ITEMS.register(item.setRegistryName(LootGames.MODID, name).setTranslationKey(block.getTranslationKey().substring(5)));
//    }

//    @SideOnly(Side.CLIENT)
//    private static void registerBlockRender(Block block) {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(new ResourceLocation(LootGames.MODID, block.getTranslationKey().substring(5).substring(LootGames.MODID.length() + 1)), "inventory"));
//    }

//    //TODO move to ModItems
//    private static void registerItem(Item item, String name) {
//        ForgeRegistries.ITEMS.register(item.setTranslationKey(LootGames.MODID + "." + name).setRegistryName(LootGames.MODID, name).setCreativeTab(ModCreativeTab.lootGames));
//    }
}
