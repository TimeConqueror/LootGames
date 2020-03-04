package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.LGItemGroup;
import ru.timeconqueror.lootgames.common.block.BlockDungeon;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.api.registry.block.BlockTimeRegistry;

import static ru.timeconqueror.lootgames.common.block.BlockDungeon.BRICKS_PROPS_CREATOR;
import static ru.timeconqueror.lootgames.common.block.BlockDungeon.LAMP_PROPS_CREATOR;

@TimeAutoRegistry
public class LGBlocks extends BlockTimeRegistry {
    public static final BlockDungeon DUNGEON_WALL = new BlockDungeon(BRICKS_PROPS_CREATOR.createProps());
    public static final BlockDungeon DUNGEON_WALL_CRACKED = new BlockDungeon(BRICKS_PROPS_CREATOR.createProps());
    public static final BlockDungeon DUNGEON_CEILING = new BlockDungeon(BRICKS_PROPS_CREATOR.createProps());
    public static final BlockDungeon DUNGEON_CEILING_CRACKED = new BlockDungeon(BRICKS_PROPS_CREATOR.createProps());
    public static final BlockDungeon DUNGEON_FLOOR = new BlockDungeon(BRICKS_PROPS_CREATOR.createProps());
    public static final BlockDungeon DUNGEON_FLOOR_CRACKED = new BlockDungeon(BRICKS_PROPS_CREATOR.createProps());
    public static final BlockDungeon DUNGEON_FLOOR_SHIELDED = new BlockDungeon(BRICKS_PROPS_CREATOR.createProps().hardnessAndResistance(-1F, 3600000F));

    public static final BlockDungeon DUNGEON_LAMP = new BlockDungeon(LAMP_PROPS_CREATOR.createProps());
    public static final BlockDungeon DUNGEON_LAMP_BROKEN = new BlockDungeon(LAMP_PROPS_CREATOR.createProps().lightValue(0));

    public LGBlocks() {
        super(LootGames.INSTANCE);
    }

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
    }
}
