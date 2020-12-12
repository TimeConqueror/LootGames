package ru.timeconqueror.lootgames.registry;

import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.world.gen.GameDungeonPieces.RandomizeBlockProcessor;
import ru.timeconqueror.timecore.registry.AutoRegistrable;
import ru.timeconqueror.timecore.registry.newreg.StructureProcessorTypeRegister;

public class LGStructureProcessorTypes {
    @AutoRegistrable
    private static final StructureProcessorTypeRegister REGISTER = new StructureProcessorTypeRegister(LootGames.MODID);

    public static final IStructureProcessorType<RandomizeBlockProcessor> RANDOMIZE_BLOCK_PROCESSOR = REGISTER.register("randomize_block", RandomizeBlockProcessor.CODEC);
}
