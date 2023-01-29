package ru.timeconqueror.lootgames.datagen;

import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.api.devtools.gen.loottable.BlockLootTableSet;

public class LGBlockLootTableSet extends BlockLootTableSet {
    private final LootItemCondition.Builder FLIP_COIN_CHANCE = LootItemRandomChanceCondition.randomChance(0.5F);

    @Override
    public void register() {
        //TODO add silk touch to prevent cracking
        registerLootTable(LGBlocks.DUNGEON_CEILING, block -> createSelfDropDispatchTable(block, FLIP_COIN_CHANCE, LootItem.lootTableItem(LGBlocks.CRACKED_DUNGEON_CEILING)));
        registerLootTable(LGBlocks.DUNGEON_FLOOR, block -> createSelfDropDispatchTable(block, FLIP_COIN_CHANCE, LootItem.lootTableItem(LGBlocks.CRACKED_DUNGEON_FLOOR)));
        registerLootTable(LGBlocks.DUNGEON_WALL, block -> createSelfDropDispatchTable(block, FLIP_COIN_CHANCE, LootItem.lootTableItem(LGBlocks.CRACKED_DUNGEON_WALL)));

        registerDropsSelf(LGBlocks.CRACKED_DUNGEON_CEILING);
        registerDropsSelf(LGBlocks.CRACKED_DUNGEON_FLOOR);
        registerDropsSelf(LGBlocks.CRACKED_DUNGEON_WALL);

        //TODO add silk touch to prevent breaking
        registerLootTable(LGBlocks.DUNGEON_LAMP, block -> createSelfDropDispatchTable(block, FLIP_COIN_CHANCE, LootItem.lootTableItem(LGBlocks.BROKEN_DUNGEON_LAMP)));
        registerDropsSelf(LGBlocks.BROKEN_DUNGEON_LAMP);
    }
}
