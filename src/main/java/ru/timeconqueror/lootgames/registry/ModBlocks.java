package ru.timeconqueror.lootgames.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.block.BlockDungeonBricks;
import ru.timeconqueror.lootgames.block.BlockDungeonLamp;
import ru.timeconqueror.lootgames.block.BlockPuzzleMaster;
import ru.timeconqueror.lootgames.proxy.ClientProxy;
import ru.timeconqueror.lootgames.proxy.CommonProxy;

public class ModBlocks {
    public static final BlockDungeonBricks DUNGEON_BRICKS = new BlockDungeonBricks();
    public static final BlockDungeonLamp DUNGEON_LAMP = new BlockDungeonLamp();
    public static final BlockPuzzleMaster PUZZLE_MASTER = new BlockPuzzleMaster();

    public static void register() {
        CommonProxy.registry.registerBlockWithItem(DUNGEON_BRICKS, "dungeon_bricks",
                new ItemMultiTexture(DUNGEON_BRICKS, DUNGEON_BRICKS, itemStack -> Integer.toString(itemStack.getMetadata())));
        CommonProxy.registry.registerBlockWithItem(DUNGEON_LAMP, "dungeon_lamp",
                new ItemMultiTexture(DUNGEON_LAMP, DUNGEON_LAMP, itemStack -> Integer.toString(itemStack.getMetadata())));
        CommonProxy.registry.registerBlockWithItem(PUZZLE_MASTER, "puzzle_master", new ItemBlock(PUZZLE_MASTER));
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers() {
        for (int i = 0; i < BlockDungeonBricks.EnumType.values().length; i++) {
            ClientProxy.registry.registerItemRender(Item.getItemFromBlock(DUNGEON_BRICKS), i,
                    BlockDungeonBricks.EnumType.byMetadata(i).getName());
        }
        for (int i = 0; i < BlockDungeonLamp.EnumType.values().length; i++) {
            ClientProxy.registry.registerItemRender(Item.getItemFromBlock(DUNGEON_LAMP), i,
                    BlockDungeonLamp.EnumType.byMetadata(i).getName());
        }

        ClientProxy.registry.registerBlockRender(PUZZLE_MASTER);
    }
}
