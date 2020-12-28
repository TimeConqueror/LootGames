package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.advancement.EndGameTrigger;
import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger;
import ru.timeconqueror.timecore.api.registry.AdvancementCriterionRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class LGAdvancementTriggers {
    @AutoRegistrable
    private static final AdvancementCriterionRegister REGISTER = new AdvancementCriterionRegister(LootGames.MODID);

    public static final UseBlockTrigger USE_BLOCK = REGISTER.register(new UseBlockTrigger());
    public static final EndGameTrigger END_GAME = REGISTER.register(new EndGameTrigger());
}
