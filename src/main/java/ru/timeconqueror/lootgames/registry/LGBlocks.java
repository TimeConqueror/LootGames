package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.api.block.BlockSmartSubordinate;
import ru.timeconqueror.lootgames.common.LGItemGroup;
import ru.timeconqueror.lootgames.common.block.BlockDungeon;
import ru.timeconqueror.lootgames.common.block.BlockMSActivator;
import ru.timeconqueror.lootgames.common.block.BlockMSMaster;
import ru.timeconqueror.lootgames.common.block.BlockPuzzleMaster;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.api.registry.block.BlockTimeRegistry;

import static ru.timeconqueror.lootgames.common.block.BlockDungeon.BRICKS_PROPS_CREATOR;
import static ru.timeconqueror.lootgames.common.block.BlockDungeon.LAMP_PROPS_CREATOR;
import static ru.timeconqueror.timecore.api.registry.block.BlockPropsFactory.setUnbreakableAndInexplosive;

@TimeAutoRegistrable
public class LGBlocks extends BlockTimeRegistry {
    public static final BlockDungeon DUNGEON_WALL = new BlockDungeon(BRICKS_PROPS_CREATOR.create());
    public static final BlockDungeon DUNGEON_WALL_CRACKED = new BlockDungeon(BRICKS_PROPS_CREATOR.create());
    public static final BlockDungeon DUNGEON_CEILING = new BlockDungeon(BRICKS_PROPS_CREATOR.create());
    public static final BlockDungeon DUNGEON_CEILING_CRACKED = new BlockDungeon(BRICKS_PROPS_CREATOR.create());
    public static final BlockDungeon DUNGEON_FLOOR = new BlockDungeon(BRICKS_PROPS_CREATOR.create());
    public static final BlockDungeon DUNGEON_FLOOR_CRACKED = new BlockDungeon(BRICKS_PROPS_CREATOR.create());
    public static final BlockDungeon DUNGEON_FLOOR_SHIELDED = new BlockDungeon(setUnbreakableAndInexplosive(BRICKS_PROPS_CREATOR.create()));

    public static final BlockDungeon DUNGEON_LAMP = new BlockDungeon(LAMP_PROPS_CREATOR.create());
    public static final BlockDungeon DUNGEON_LAMP_BROKEN = new BlockDungeon(LAMP_PROPS_CREATOR.create().lightValue(0));

    public static final BlockPuzzleMaster PUZZLE_MASTER = new BlockPuzzleMaster();
    public static final BlockSmartSubordinate SMART_SUBORDINATE = new BlockSmartSubordinate();

    public static final BlockMSActivator MS_ACTIVATOR = new BlockMSActivator();
    public static final BlockMSMaster MS_MASTER = new BlockMSMaster();
    public static final BlockMSMaster PIPES_MASTER = new BlockMSMaster();

    @Override
    public void register() {
        regBlock(DUNGEON_CEILING, "dungeon_ceiling").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_CEILING_CRACKED, "dungeon_ceiling_cracked").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_WALL, "dungeon_wall").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_WALL_CRACKED, "dungeon_wall_cracked").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_FLOOR, "dungeon_floor").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_FLOOR_CRACKED, "dungeon_floor_cracked").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_FLOOR_SHIELDED, "dungeon_floor_shielded").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_LAMP, "dungeon_lamp").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_LAMP_BROKEN, "dungeon_lamp_broken").regDefaults(LGItemGroup.MAIN);
        regBlock(PUZZLE_MASTER, "puzzle_master").regDefaults(LGItemGroup.MAIN);
        regBlock(SMART_SUBORDINATE, "smart_subordinate").regDefaultStateAndModel(new TextureLocation(getModID(), "block/dungeon_floor_shielded"));
        regBlock(MS_ACTIVATOR, "ms_activator").regDefaults(LGItemGroup.MAIN);
        regBlock(MS_MASTER, "ms_master").regDefaultStateAndModel(new TextureLocation(getModID(), "block/dungeon_floor_shielded"));
        regBlock(PIPES_MASTER, "pipes_master").regDefaultStateAndModel(new TextureLocation(getModID(), "block/dungeon_floor_shielded"));
    }
}
