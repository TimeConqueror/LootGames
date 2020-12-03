package ru.timeconqueror.lootgames.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.TickTrigger;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.advancement.EndGameTrigger;
import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.devtools.gen.advancement.IAdvancementSet;
import ru.timeconqueror.timecore.devtools.gen.advancement.ISaveFunction;

import static net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;

public class LGAdvancementSet implements IAdvancementSet {
    @Override
    public void fill(ISaveFunction saveFunction) {
        Advancement root = saveFunction.process(LootGames.rl("root"),
                Advancement.Builder.advancement()
                        .display(LGBlocks.PUZZLE_MASTER,
                                new TranslationTextComponent("advancement.lootgames.root"),
                                new TranslationTextComponent("advancement.lootgames.root.desc"),
                                LootGames.rl("textures/block/dungeon_floor.png"),
                                FrameType.GOAL,
                                false, /*Whether or not to show the toast pop up after completing this advancement*/
                                false, /*Whether or not to announce in the chat when this advancement has been completed*/
                                false)
                        .addCriterion("tick", new TickTrigger.Instance(AndPredicate.ANY)));

        Advancement findDungeon = saveFunction.process(LootGames.rl("find_dungeon"),
                Advancement.Builder.advancement()
                        .parent(root)
                        .display(LGBlocks.DUNGEON_LAMP,
                                new TranslationTextComponent("advancement.lootgames.find_dungeon"),
                                new TranslationTextComponent("advancement.lootgames.find_dungeon.desc"),
                                null,
                                FrameType.TASK,
                                true,
                                true,
                                false)
                        .addCriterion("click", UseBlockTrigger.Instance.forBlock(LGBlocks.PUZZLE_MASTER)));

        Advancement winGame = saveFunction.process(LootGames.rl("win_game"),
                Advancement.Builder.advancement()
                        .parent(findDungeon)
                        .display(Items.NETHER_STAR,
                                new TranslationTextComponent("advancement.lootgames.win_game"),
                                new TranslationTextComponent("advancement.lootgames.win_game.desc"),
                                null,
                                FrameType.TASK,
                                true,
                                true,
                                false)
                        .addCriterion("win", new EndGameTrigger.Instance(EndGameTrigger.TYPE_WIN, AndPredicate.ANY)));

        Advancement loseGame = saveFunction.process(LootGames.rl("lose_game"),
                Advancement.Builder.advancement()
                        .parent(findDungeon)
                        .display(Items.SKELETON_SKULL,
                                new TranslationTextComponent("advancement.lootgames.lose_game"),
                                new TranslationTextComponent("advancement.lootgames.lose_game.desc"),
                                null,
                                FrameType.TASK,
                                true,
                                true,
                                true)
                        .addCriterion("lose", new EndGameTrigger.Instance(EndGameTrigger.TYPE_LOSE, AndPredicate.ANY)));

        Advancement startMinesweeper = saveFunction.process(LootGames.rl("minesweeper/start"),
                Advancement.Builder.advancement()
                        .parent(findDungeon)
                        .display(LGBlocks.MS_ACTIVATOR,
                                new TranslationTextComponent("advancement.lootgames.ms.start"),
                                new TranslationTextComponent("advancement.lootgames.ms.start.desc"),
                                null,
                                FrameType.TASK,
                                true,
                                true,
                                true)
                        .addCriterion("click", UseBlockTrigger.Instance.forBlock(LGBlocks.MS_ACTIVATOR)));

        Advancement msBeatLevel4 = saveFunction.process(LootGames.rl("minesweeper/beat_level4"),
                Advancement.Builder.advancement()
                        .parent(startMinesweeper)
                        .display(Items.CREEPER_HEAD,
                                new TranslationTextComponent("advancement.lootgames.ms.beat_level4"),
                                new TranslationTextComponent("advancement.lootgames.ms.beat_level4.desc"),
                                null,
                                FrameType.CHALLENGE,
                                true,
                                true,
                                false)
                        .addCriterion("end_level4", new EndGameTrigger.Instance(GameMineSweeper.ADV_TYPE_BEAT_LEVEL4, AndPredicate.ANY)));
    }
}
