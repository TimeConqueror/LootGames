package ru.timeconqueror.lootgames.datagen;

import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.api.datagen.loottable.BlockLootTableSet;

public class LGBlockLootTableSet extends BlockLootTableSet {
    private ILootCondition.IBuilder FLIP_COIN_CHANCE = RandomChance.builder(0.5F);

    @Override
    public void register() {
        //TODO add silk touch to prevent cracking
        registerLootTable(LGBlocks.DUNGEON_CEILING, block -> dropping(FLIP_COIN_CHANCE, block, ItemLootEntry.builder(LGBlocks.DUNGEON_CEILING_CRACKED)));
        registerLootTable(LGBlocks.DUNGEON_FLOOR, block -> dropping(FLIP_COIN_CHANCE, block, ItemLootEntry.builder(LGBlocks.DUNGEON_FLOOR_CRACKED)));
        registerLootTable(LGBlocks.DUNGEON_WALL, block -> dropping(FLIP_COIN_CHANCE, block, ItemLootEntry.builder(LGBlocks.DUNGEON_WALL_CRACKED)));

        registerDropSelfLootTable(LGBlocks.DUNGEON_CEILING_CRACKED);
        registerDropSelfLootTable(LGBlocks.DUNGEON_FLOOR_CRACKED);
        registerDropSelfLootTable(LGBlocks.DUNGEON_WALL_CRACKED);

        //TODO add silk touch to prevent breaking
        registerLootTable(LGBlocks.DUNGEON_LAMP, block -> dropping(FLIP_COIN_CHANCE, block, ItemLootEntry.builder(LGBlocks.DUNGEON_LAMP_BROKEN)));
        registerDropSelfLootTable(LGBlocks.DUNGEON_LAMP_BROKEN);
    }
}
