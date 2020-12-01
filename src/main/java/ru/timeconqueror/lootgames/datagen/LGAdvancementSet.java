package ru.timeconqueror.lootgames.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.TickTrigger;
import net.minecraft.util.text.TranslationTextComponent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.devtools.gen.advancement.IAdvancementSet;
import ru.timeconqueror.timecore.devtools.gen.advancement.ISaveFunction;

public class LGAdvancementSet implements IAdvancementSet {
    @Override
    public void fill(ISaveFunction saveFunction) {
        Advancement root = saveFunction.process(Advancement.Builder.advancement()
                        .display(LGBlocks.PUZZLE_MASTER,
                                new TranslationTextComponent("advancement.lootgames.root"),
                                new TranslationTextComponent("advancement.lootgames.root.desc"),
                                LootGames.rl("textures/blocks/dungeon_floor.png"),
                                FrameType.GOAL,
                                false, /*Whether or not to show the toast pop up after completing this advancement*/
                                false, /*Whether or not to announce in the chat when this advancement has been completed*/
                                false)
                        .addCriterion("tick", new TickTrigger.Instance(EntityPredicate.AndPredicate.ANY)),
                LootGames.rl("root"));

//        Advancement gameLost = saveFunction.process(
//                , LootGames.rl("game_lost"));
    }
}
