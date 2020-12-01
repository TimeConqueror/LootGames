package ru.timeconqueror.lootgames.registry;

import ru.timeconqueror.lootgames.common.advancement.EndGameTrigger;
import ru.timeconqueror.timecore.registry.newreg.AdvancementCriterionRegister;

public class LGAdvancementTriggers {
    //    public static final ActivateBlockTrigger ACTIVATE_BLOCK = AdvancementCriterionRegister.register(new ActivateBlockTrigger());
    public static final EndGameTrigger END_GAME = AdvancementCriterionRegister.register(new EndGameTrigger());
}
