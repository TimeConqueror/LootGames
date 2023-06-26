package ru.timeconqueror.lootgames.datagen

import net.minecraft.advancements.Advancement
import net.minecraft.core.HolderLookup
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.common.data.ForgeAdvancementProvider.AdvancementGenerator
import java.util.function.Consumer

class LGAdvancementGenerator : AdvancementGenerator {
    override fun generate(registries: HolderLookup.Provider, saver: Consumer<Advancement>, existingFileHelper: ExistingFileHelper) {
//        saverAwareAdvancementProvider(LootGames.MODID, saver, existingFileHelper) {
//            val root = make("root") {
//                display(
//                    LGBlocks.PUZZLE_MASTER,
//                    Component.translatable("advancement.lootgames.root"),
//                    Component.translatable("advancement.lootgames.root.desc"),
//                    LootGames.rl("textures/block/dungeon_floor.png"),
//                    FrameType.GOAL,
//                    false,  /*Whether to show the toast pop up after completing this advancement*/
//                    false,  /*Whether to announce in the chat when this advancement has been completed*/
//                    false
//                )
//                addCriterion(
//                    "tick",
//                    PlayerTrigger.TriggerInstance(CriteriaTriggers.TICK.id, ContextAwarePredicate.ANY)
//                )
//            }
//
//            val findDungeon = make("find_dungeon") {
//                parent(root)
//                display(
//                    LGBlocks.DUNGEON_LAMP,
//                    Component.translatable("advancement.lootgames.find_dungeon"),
//                    Component.translatable("advancement.lootgames.find_dungeon.desc"),
//                    null,
//                    FrameType.TASK,
//                    true,
//                    true,
//                    false
//                )
//                addCriterion("click", UseBlockTrigger.Instance.forBlock(LGBlocks.PUZZLE_MASTER))
//            }
//
//            val winGame = make("win_game") {
//                parent(findDungeon)
//                display(
//                    Items.NETHER_STAR,
//                    Component.translatable("advancement.lootgames.win_game"),
//                    Component.translatable("advancement.lootgames.win_game.desc"),
//                    null,
//                    FrameType.TASK,
//                    true,
//                    true,
//                    false
//                )
//                addCriterion("win", EndGameTrigger.Instance(EndGameTrigger.TYPE_WIN, ContextAwarePredicate.ANY))
//            }
//
//            val loseGame = make("lose_game") {
//                parent(findDungeon)
//                display(
//                    Items.SKELETON_SKULL,
//                    Component.translatable("advancement.lootgames.lose_game"),
//                    Component.translatable("advancement.lootgames.lose_game.desc"),
//                    null,
//                    FrameType.TASK,
//                    true,
//                    true,
//                    true
//                )
//                addCriterion("lose", EndGameTrigger.Instance(EndGameTrigger.TYPE_LOSE, ContextAwarePredicate.ANY))
//            }
//
//            val startMinesweeper = make("minesweeper/start") {
//                parent(findDungeon)
//                display(
//                    LGBlocks.MS_ACTIVATOR,
//                    Component.translatable("advancement.lootgames.ms.start"),
//                    Component.translatable("advancement.lootgames.ms.start.desc"),
//                    null,
//                    FrameType.TASK,
//                    true,
//                    true,
//                    true
//                )
//                addCriterion("click", UseBlockTrigger.Instance.forBlock(LGBlocks.MS_ACTIVATOR))
//            }
//
//            val msBeatLevel4 = make("minesweeper/beat_level_4") {
//                parent(startMinesweeper)
//                display(
//                    Items.CREEPER_HEAD,
//                    Component.translatable("advancement.lootgames.ms.beat_level_4"),
//                    Component.translatable("advancement.lootgames.ms.beat_level_4.desc"),
//                    null,
//                    FrameType.CHALLENGE,
//                    true,
//                    true,
//                    false
//                )
//                addCriterion(
//                    "end_level4",
//                    EndGameTrigger.Instance(GameMineSweeper.ADV_BEAT_LEVEL4, ContextAwarePredicate.ANY)
//                )
//            }
//
//            val startGameOfLight = make("gameoflight/start") {
//                parent(findDungeon)
//                display(
//                    LGBlocks.GOL_ACTIVATOR,
//                    Component.translatable("advancement.lootgames.gol.start"),
//                    Component.translatable("advancement.lootgames.gol.start.desc"),
//                    null,
//                    FrameType.TASK,
//                    true,
//                    true,
//                    true
//                )
//                addCriterion("click", UseBlockTrigger.Instance.forBlock(LGBlocks.GOL_ACTIVATOR))
//            }
//
//            val golBeatLevel3 = make("gameoflight/beat_level_3") {
//                parent(startGameOfLight)
//                display(
//                    Items.DIAMOND,
//                    Component.translatable("advancement.lootgames.gol.beat_level_3"),
//                    Component.translatable("advancement.lootgames.gol.beat_level_3.desc"),
//                    null,
//                    FrameType.CHALLENGE,
//                    true,
//                    true,
//                    false
//                )
//                addCriterion(
//                    "end_level3",
//                    EndGameTrigger.Instance(GameOfLight.ADV_BEAT_LEVEL3, ContextAwarePredicate.ANY)
//                )
//            }
//
//            val golBeatLevel4 = make("gameoflight/beat_level_4") {
//                parent(startGameOfLight)
//                display(
//                    Items.EMERALD,
//                    Component.translatable("advancement.lootgames.gol.beat_level_4"),
//                    Component.translatable("advancement.lootgames.gol.beat_level_4.desc"),
//                    null,
//                    FrameType.CHALLENGE,
//                    true,
//                    true,
//                    false
//                )
//                addCriterion(
//                    "end_level4",
//                    EndGameTrigger.Instance(GameOfLight.ADV_BEAT_LEVEL4, ContextAwarePredicate.ANY)
//                )
//            }
//        }
    }
}