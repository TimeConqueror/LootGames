package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.common.advancement.EndGameTrigger;
import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger;
import ru.timeconqueror.timecore.registry.newreg.AdvancementCriterionRegister;

public class LGAdvancementTriggers {
    public static final UseBlockTrigger USE_BLOCK = AdvancementCriterionRegister.register(new UseBlockTrigger());
    public static final EndGameTrigger END_GAME = AdvancementCriterionRegister.register(new EndGameTrigger());
}
