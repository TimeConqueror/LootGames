package ru.timeconqueror.lootgames.achievement;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import ru.timeconqueror.lootgames.achievement.criteria.BlockActivatedTrigger;
import ru.timeconqueror.lootgames.achievement.criteria.WinGameTrigger;

public class AdvancementManager {
    public static final BlockActivatedTrigger BLOCK_ACTIVATED = new BlockActivatedTrigger();
    public static final WinGameTrigger WIN_GAME = new WinGameTrigger();

    public static void registerCriteria() {
        register(BLOCK_ACTIVATED);
        register(WIN_GAME);
    }

    private static <T extends ICriterionTrigger> void register(T criterion) {
        CriteriaTriggers.register(criterion);
    }
}
