package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.LGItemGroup;
import ru.timeconqueror.lootgames.common.block.BlockDungeonBricks;
import ru.timeconqueror.timecore.api.common.registry.TimeAutoRegistry;
import ru.timeconqueror.timecore.api.common.registry.block.BlockTimeRegistry;

@TimeAutoRegistry
public class LGBlocks extends BlockTimeRegistry {
    public static final BlockDungeonBricks DUNGEON_WALL = new BlockDungeonBricks();
    public static final BlockDungeonBricks DUNGEON_WALL_CRACKED = new BlockDungeonBricks();
    public static final BlockDungeonBricks DUNGEON_CEILING = new BlockDungeonBricks();
    public static final BlockDungeonBricks DUNGEON_CEILING_CRACKED = new BlockDungeonBricks();
    public static final BlockDungeonBricks DUNGEON_FLOOR = new BlockDungeonBricks();
    public static final BlockDungeonBricks DUNGEON_FLOOR_CRACKED = new BlockDungeonBricks();
    public static final BlockDungeonBricks DUNGEON_FLOOR_SHIELDED = new BlockDungeonBricks(BlockDungeonBricks.defPropsCreator.createProps().hardnessAndResistance(-1F, 3600000F));

    public LGBlocks() {
        super(LootGames.INSTANCE);
    }

    @Override
    public void register() {
        regBlock(DUNGEON_WALL, "dungeon_wall").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_WALL_CRACKED, "dungeon_wall_cracked").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_CEILING, "dungeon_ceiling").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_CEILING_CRACKED, "dungeon_ceiling_cracked").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_FLOOR, "dungeon_floor").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_FLOOR_CRACKED, "dungeon_floor_cracked").regDefaults(LGItemGroup.MAIN);
        regBlock(DUNGEON_FLOOR_SHIELDED, "dungeon_floor_shielded").regDefaults(LGItemGroup.MAIN);
    }
}
