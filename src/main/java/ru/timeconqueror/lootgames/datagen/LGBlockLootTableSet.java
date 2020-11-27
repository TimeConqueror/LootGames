package ru.timeconqueror.lootgames.datagen;

import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.RandomChance;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.devtools.gen.loottable.BlockLootTableSet;

public class LGBlockLootTableSet extends BlockLootTableSet {
    private final ILootCondition.IBuilder FLIP_COIN_CHANCE = RandomChance.randomChance(0.5F);

    @Override
    public void register() {
        //TODO add silk touch to prevent cracking
        registerLootTable(LGBlocks.DUNGEON_CEILING, block -> createSelfDropDispatchTable(block, FLIP_COIN_CHANCE, ItemLootEntry.lootTableItem(LGBlocks.CRACKED_DUNGEON_CEILING)));
        registerLootTable(LGBlocks.DUNGEON_FLOOR, block -> createSelfDropDispatchTable(block, FLIP_COIN_CHANCE, ItemLootEntry.lootTableItem(LGBlocks.CRACKED_DUNGEON_FLOOR)));
        registerLootTable(LGBlocks.DUNGEON_WALL, block -> createSelfDropDispatchTable(block, FLIP_COIN_CHANCE, ItemLootEntry.lootTableItem(LGBlocks.CRACKED_DUNGEON_WALL)));

        registerDropsSelf(LGBlocks.CRACKED_DUNGEON_CEILING);
        registerDropsSelf(LGBlocks.CRACKED_DUNGEON_FLOOR);
        registerDropsSelf(LGBlocks.CRACKED_DUNGEON_WALL);

        //TODO add silk touch to prevent breaking
        registerLootTable(LGBlocks.DUNGEON_LAMP, block -> createSelfDropDispatchTable(block, FLIP_COIN_CHANCE, ItemLootEntry.lootTableItem(LGBlocks.BROKEN_DUNGEON_LAMP)));
        registerDropsSelf(LGBlocks.BROKEN_DUNGEON_LAMP);
    }
}
