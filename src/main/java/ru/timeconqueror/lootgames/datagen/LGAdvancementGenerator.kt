package ru.timeconqueror.lootgames.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EntityPredicate.Composite;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.common.advancement.EndGameTrigger;
import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.registry.LGBlocks;

import java.util.function.Consumer;

public class LGAdvancementSet implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        Advancement root = Advancement.Builder.advancement()
                .display(LGBlocks.PUZZLE_MASTER,
                        Component.translatable("advancement.lootgames.root"),
                        Component.translatable("advancement.lootgames.root.desc"),
                        LootGames.rl("textures/block/dungeon_floor.png"),
                        FrameType.GOAL,
                        false, /*Whether to show the toast pop up after completing this advancement*/
                        false, /*Whether to announce in the chat when this advancement has been completed*/
                        false)
                .addCriterion("tick", new PlayerTrigger.TriggerInstance(CriteriaTriggers.TICK.getId(), Composite.ANY))
                .build(LootGames.rl("root"));

        Advancement findDungeon = Advancement.Builder.advancement()
                .parent(root)
                .display(LGBlocks.DUNGEON_LAMP,
                        Component.translatable("advancement.lootgames.find_dungeon"),
                        Component.translatable("advancement.lootgames.find_dungeon.desc"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("click", UseBlockTrigger.Instance.forBlock(LGBlocks.PUZZLE_MASTER))
                .build(LootGames.rl("find_dungeon"));

        Advancement winGame = Advancement.Builder.advancement()
                .parent(findDungeon)
                .display(Items.NETHER_STAR,
                        Component.translatable("advancement.lootgames.win_game"),
                        Component.translatable("advancement.lootgames.win_game.desc"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false)
                .addCriterion("win", new EndGameTrigger.Instance(EndGameTrigger.TYPE_WIN, Composite.ANY))
                .build(LootGames.rl("win_game"));

        Advancement loseGame = Advancement.Builder.advancement()
                .parent(findDungeon)
                .display(Items.SKELETON_SKULL,
                        Component.translatable("advancement.lootgames.lose_game"),
                        Component.translatable("advancement.lootgames.lose_game.desc"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        true)
                .addCriterion("lose", new EndGameTrigger.Instance(EndGameTrigger.TYPE_LOSE, Composite.ANY))
                .build(LootGames.rl("lose_game"));

        Advancement startMinesweeper = Advancement.Builder.advancement()
                .parent(findDungeon)
                .display(LGBlocks.MS_ACTIVATOR,
                        Component.translatable("advancement.lootgames.ms.start"),
                        Component.translatable("advancement.lootgames.ms.start.desc"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        true)
                .addCriterion("click", UseBlockTrigger.Instance.forBlock(LGBlocks.MS_ACTIVATOR))
                .build(LootGames.rl("minesweeper/start"));

        Advancement msBeatLevel4 = Advancement.Builder.advancement()
                .parent(startMinesweeper)
                .display(Items.CREEPER_HEAD,
                        Component.translatable("advancement.lootgames.ms.beat_level_4"),
                        Component.translatable("advancement.lootgames.ms.beat_level_4.desc"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .addCriterion("end_level4", new EndGameTrigger.Instance(GameMineSweeper.ADV_BEAT_LEVEL4, Composite.ANY))
                .build(LootGames.rl("minesweeper/beat_level_4"));

        Advancement startGameOfLight = Advancement.Builder.advancement()
                .parent(findDungeon)
                .display(LGBlocks.GOL_ACTIVATOR,
                        Component.translatable("advancement.lootgames.gol.start"),
                        Component.translatable("advancement.lootgames.gol.start.desc"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        true)
                .addCriterion("click", UseBlockTrigger.Instance.forBlock(LGBlocks.GOL_ACTIVATOR))
                .build(LootGames.rl("gameoflight/start"));

        Advancement golBeatLevel3 = Advancement.Builder.advancement()
                .parent(startGameOfLight)
                .display(Items.DIAMOND,
                        Component.translatable("advancement.lootgames.gol.beat_level_3"),
                        Component.translatable("advancement.lootgames.gol.beat_level_3.desc"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .addCriterion("end_level3", new EndGameTrigger.Instance(GameOfLight.ADV_BEAT_LEVEL3, Composite.ANY))
                .build(LootGames.rl("gameoflight/beat_level_3"));

        Advancement golBeatLevel4 = Advancement.Builder.advancement()
                .parent(startGameOfLight)
                .display(Items.EMERALD,
                        Component.translatable("advancement.lootgames.gol.beat_level_4"),
                        Component.translatable("advancement.lootgames.gol.beat_level_4.desc"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .addCriterion("end_level4", new EndGameTrigger.Instance(GameOfLight.ADV_BEAT_LEVEL4, Composite.ANY))
                .build(LootGames.rl("gameoflight/beat_level_4"));
    }
}
