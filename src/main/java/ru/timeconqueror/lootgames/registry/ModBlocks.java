package ru.timeconqueror.lootgames.registry;

import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.timeconqueror.lootgames.block.*;
import ru.timeconqueror.lootgames.minigame.gameoflight.TESRGOLMaster;
import ru.timeconqueror.lootgames.minigame.gameoflight.TileEntityGOLMaster;
import ru.timeconqueror.lootgames.proxy.ClientProxy;
import ru.timeconqueror.lootgames.proxy.CommonProxy;
import ru.timeconqueror.lootgames.tileentity.TileEntityMSField;
import ru.timeconqueror.lootgames.tileentity.TileEntityPuzzleMaster;

public class ModBlocks {
    public static final BlockDungeonBricks DUNGEON_BRICKS = new BlockDungeonBricks();
    public static final BlockDungeonLamp DUNGEON_LAMP = new BlockDungeonLamp();
    public static final BlockPuzzleMaster PUZZLE_MASTER = new BlockPuzzleMaster();
    public static final BlockGOLSubordinate GOL_SUBORDINATE = new BlockGOLSubordinate();
    public static final BlockGOLMaster GOL_MASTER = new BlockGOLMaster();
    public static final BlockMSField MS_FIELD = new BlockMSField();

    public static void register() {
        CommonProxy.REGISTRY.registerBlockWithItem(DUNGEON_BRICKS, "dungeon_bricks",
                new ItemMultiTexture(DUNGEON_BRICKS, DUNGEON_BRICKS, itemStack -> Integer.toString(itemStack.getMetadata())));
        CommonProxy.REGISTRY.registerBlockWithItem(DUNGEON_LAMP, "dungeon_lamp",
                new ItemMultiTexture(DUNGEON_LAMP, DUNGEON_LAMP, itemStack -> Integer.toString(itemStack.getMetadata())));

        CommonProxy.REGISTRY.registerBlockWithItem(PUZZLE_MASTER, "puzzle_master", new ItemBlock(PUZZLE_MASTER));
        CommonProxy.REGISTRY.registerBlock(GOL_SUBORDINATE, "gol_subordinate");
        CommonProxy.REGISTRY.registerBlockWithItem(GOL_MASTER, "gol_master", new ItemBlock(GOL_MASTER));
        CommonProxy.REGISTRY.registerBlock(MS_FIELD, "ms_field");
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers() {
        for (int i = 0; i < BlockDungeonBricks.EnumType.values().length; i++) {
            ClientProxy.REGISTRY.registerItemRender(Item.getItemFromBlock(DUNGEON_BRICKS), i,
                    BlockDungeonBricks.EnumType.byMetadata(i).getName());
        }

        ClientProxy.REGISTRY.registerItemRender(Item.getItemFromBlock(DUNGEON_LAMP), 0, "dungeon_lamp");
        ClientProxy.REGISTRY.registerItemRender(Item.getItemFromBlock(DUNGEON_LAMP), 1, "dungeon_lamp_broken");

        ClientProxy.REGISTRY.registerBlockRender(PUZZLE_MASTER);
        ModelLoader.setCustomStateMapper(GOL_SUBORDINATE, new StateMap.Builder().ignore(BlockGOLSubordinate.OFFSET).build());
        ClientProxy.REGISTRY.registerBlockRender(GOL_MASTER);
    }

    public static void registerTileEntities() {
        CommonProxy.REGISTRY.registerTileEntity(TileEntityPuzzleMaster.class, "puzzle_master");
        CommonProxy.REGISTRY.registerTileEntity(TileEntityGOLMaster.class, "gol_master");
        CommonProxy.REGISTRY.registerTileEntity(TileEntityMSField.class, "ms_field");
    }

    @SideOnly(Side.CLIENT)
    public static void registerTESRS() {
        ClientProxy.REGISTRY.registerTESR(TileEntityGOLMaster.class, new TESRGOLMaster());
    }
}
