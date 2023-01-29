package ru.timeconqueror.lootgames.datagen;

public clasnet.minecraft.advancements.critereon.EntityPredicate @Override
    public void fill(ISaveFunction saveFunction){
        Advancement root=saveFunction.process(LootGames.rl("root"),
        Advancement.Builder.advancement()
        .display(LGBlocks.PUZZLE_MASTER,
        Component.translatable("advancement.lootgames.root"),
        Component.translatable("advancement.lootgames.root.desc"),
        LootGames.rl("textures/block/dungeon_floor.png"),
        FrameType.GOAL,
        false, /*Whether or not to show the toast pop up after completing this advancement*/
        false, /*Whether or not to announce in the chat when this advancement has been completed*/
        false)
        .addCriterion("tick",new TickTrigger.TriggerInstance(Composite.ANY)));

        Advancement findDungeon=saveFunction.process(LootGames.rl("find_dungeon"),
        Advancement.Builder.advancement()
        .parent(root)
        .display(LGBlocks.DUNGEON_LAMP,
        Component.translatable("advancement.lootgames.find_dungeon"),
        Component.translatable("advancement.lootgames.find_dungeon.desc"),
        null,
        FrameType.TASK,
        true,
        true,
        false)
        .addCriterion("click",UseBlockTrigger.Instance.forBlock(LGBlocks.PUZZLE_MASTER)));

        Advancement winGame=saveFunction.process(LootGames.rl("win_game"),
        Advancement.Builder.advancement()
        .parent(findDungeon)
        .display(Items.NETHER_STAR,
        Component.translatable("advancement.lootgames.win_game"),
        Component.translatable("advancement.lootgames.win_game.desc"),
        null,
        FrameType.TASK,
        true,
        true,
        false)
        .addCriterion("win",new EndGameTrigger.Instance(EndGameTrigger.TYPE_WIN,Composite.ANY)));

        Advancement loseGame=saveFunction.process(LootGames.rl("lose_game"),
        Advancement.Builder.advancement()
        .parent(findDungeon)
        .display(Items.SKELETON_SKULL,
        Component.translatable("advancement.lootgames.lose_game"),
        Component.translatable("advancement.lootgames.lose_game.desc"),
        null,
        FrameType.TASK,
        true,
        true,
        true)
        .addCriterion("lose",new EndGameTrigger.Instance(EndGameTrigger.TYPE_LOSE,Composite.ANY)));

        Advancement startMinesweeper=saveFunction.process(LootGames.rl("minesweeper/start"),
        Advancement.Builder.advancement()
        .parent(findDungeon)
        .display(LGBlocks.MS_ACTIVATOR,
        Component.translatable("advancement.lootgames.ms.start"),
        Component.translatable("advancement.lootgames.ms.start.desc"),
        null,
        FrameType.TASK,
        true,
        true,
        true)
        .addCriterion("click",UseBlockTrigger.Instance.forBlock(LGBlocks.MS_ACTIVATOR)));

        Advancement msBeatLevel4=saveFunction.process(LootGames.rl("minesweeper/beat_level_4"),
        Advancement.Builder.advancement()
        .parent(startMinesweeper)
        .display(Items.CREEPER_HEAD,
        Component.translatable("advancement.lootgames.ms.beat_level_4"),
        Component.translatable("advancement.lootgames.ms.beat_level_4.desc"),
        null,
        FrameType.CHALLENGE,
        true,
        true,
        false)
        .addCriterion("end_level4",new EndGameTrigger.Instance(GameMineSweeper.ADV_BEAT_LEVEL4,Composite.ANY)));

        Advancement startGameOfLight=saveFunction.process(LootGames.rl("gameoflight/start"),
        Advancement.Builder.advancement()
        .parent(findDungeon)
        .display(LGBlocks.GOL_ACTIVATOR,
        Component.translatable("advancement.lootgames.gol.start"),
        Component.translatable("advancement.lootgames.gol.start.desc"),
        null,
        FrameType.TASK,
        true,
        true,
        true)
        .addCriterion("click",UseBlockTrigger.Instance.forBlock(LGBlocks.GOL_ACTIVATOR)));

        Advancement golBeatLevel3=saveFunction.process(LootGames.rl("gameoflight/beat_level_3"),
        Advancement.Builder.advancement()
        .parent(startGameOfLight)
        .display(Items.DIAMOND,
        Component.translatable("advancement.lootgames.gol.beat_level_3"),
        Component.translatable("advancement.lootgames.gol.beat_level_3.desc"),
        null,
        FrameType.CHALLENGE,
        true,
        true,
        false)
        .addCriterion("end_level3",new EndGameTrigger.Instance(GameOfLight.ADV_BEAT_LEVEL3,Composite.ANY)));

        Advancement golBeatLevel4=saveFunction.process(LootGames.rl("gameoflight/beat_level_4"),
        Advancement.Builder.advancement()
        .parent(startGameOfLight)
        .display(Items.EMERALD,
        Component.translatable("advancement.lootgames.gol.beat_level_4"),
        Component.translatable("advancement.lootgames.gol.beat_level_4.desc"),
        null,
        FrameType.CHALLENGE,
        true,
        true,
        false)
        .addCriterion("end_level4",new EndGameTrigger.Instance(GameOfLight.ADV_BEAT_LEVEL4,Composite.ANY)));
        }
        }
