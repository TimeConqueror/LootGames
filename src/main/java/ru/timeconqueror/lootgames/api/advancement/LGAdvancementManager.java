package ru.timeconqueror.lootgames.api.advancement;

import ru.timeconqueror.lootgames.common.advancement.ActivateBlockTrigger;
import ru.timeconqueror.lootgames.common.advancement.EndGameTrigger;
import ru.timeconqueror.timecore.api.registry.AdvancementTimeRegistry;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable
public class LGAdvancementManager extends AdvancementTimeRegistry {
    public static final ActivateBlockTrigger ACTIVATE_BLOCK = registerCriterionTrigger(new ActivateBlockTrigger());
    public static final EndGameTrigger END_GAME = registerCriterionTrigger(new EndGameTrigger());
}
