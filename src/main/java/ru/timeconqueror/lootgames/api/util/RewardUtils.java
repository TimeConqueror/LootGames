package ru.timeconqueror.lootgames.api.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.common.config.base.RewardConfig;
import ru.timeconqueror.lootgames.common.config.base.StagedRewardConfig.FourStagedRewardConfig;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.api.util.HorizontalDirection;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.RandHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RewardUtils {
    /**
     * Spawns lamp decoration and up to four chests depending on provided {@code rewardLevel}
     *
     * @param world        world where to spawn chest
     * @param centralPos   position around which chests will be spawned
     * @param rewardLevel  from 0 to 4 inclusive. Zero means no chests will be generated.
     * @param rewardConfig reward part of your game's config
     */
    public static void spawnFourStagedReward(ServerWorld world, LootGame<?> game, BlockPos centralPos, int rewardLevel, FourStagedRewardConfig rewardConfig) {
        BlockState state = LGBlocks.DUNGEON_LAMP.defaultBlockState();
        world.setBlockAndUpdate(centralPos.offset(1, 0, 1), state);
        world.setBlockAndUpdate(centralPos.offset(1, 0, -1), state);
        world.setBlockAndUpdate(centralPos.offset(-1, 0, 1), state);
        world.setBlockAndUpdate(centralPos.offset(-1, 0, -1), state);

        rewardLevel = MathUtils.coerceInRange(rewardLevel, 0, 4);

        if (rewardLevel > 0) {
            int counter = 0;
            for (HorizontalDirection direction : HorizontalDirection.values()) {
                if (counter >= rewardLevel) break;

                spawnLootChest(world, centralPos, direction, SpawnChestData.fromRewardConfig(game, rewardConfig.getStageByIndex(rewardLevel - 1)));
                counter++;
            }
        }
    }

    /**
     * Spawns chest with provided {@link SpawnChestData} in 1 block direction from central position.
     *
     * @param world               world where to spawn chest
     * @param centralPos          position around which chest will be spawned
     * @param horizontalDirection in what direction of {@code centralPos} chest should be placed.
     *                            For example, if you set the direction to {@link HorizontalDirection#NORTH},
     *                            chest will be spawned 1 block north of the {@code centralPos} with south facing.
     * @param chestData           data which contains the rules of setting chest content
     */
    public static void spawnLootChest(ServerWorld world, BlockPos centralPos, HorizontalDirection horizontalDirection, SpawnChestData chestData) {
        System.out.println("Spawning... " + chestData.lootTableRL);
        Direction direction = horizontalDirection.get();

        BlockState chest = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, direction.getOpposite());

        BlockPos placePos = centralPos.offset(direction.getStepX(), 0, direction.getStepZ());

        world.setBlockAndUpdate(placePos, chest);

        ChestTileEntity teChest = (ChestTileEntity) Objects.requireNonNull((world.getBlockEntity(placePos)));

        LockableLootTileEntity.setLootTable(world, world.random, placePos, chestData.getLootTableKey());
        teChest.setLootTable(chestData.getLootTableKey(), 0);
        teChest.unpackLootTable(null);

        List<Integer> notEmptyIndexes = new ArrayList<>();
        for (int i = 0; i < teChest.getContainerSize(); i++) {
            if (teChest.getItem(i) != ItemStack.EMPTY) {
                notEmptyIndexes.add(i);
            }
        }

        if (notEmptyIndexes.size() == 0) {
            ItemStack stack = new ItemStack(Blocks.STONE);
            try {
                stack.setTag(JsonToNBT.parseTag(String.format("{display:{Name:\"{\\\"text\\\":\\\"The Sorry Stone\\\", \\\"color\\\":\\\"blue\\\", \\\"bold\\\": \\\"true\\\"}\", Lore: [\"{\\\"text\\\":\\\"Modpack creator failed to configure the LootTables properly.\\\\nPlease report that Loot Table [%s] for %s stage is broken, thank you!\\\"}\"]}}", chestData.getLootTableKey(), chestData.getGameName())));//TODO when copying back to 1.12.2 - this tag don't work, only old one
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }

            teChest.setItem(teChest.getContainerSize() / 2, stack);

            return;
        }

        int minItems = chestData.getMinItems();
        int maxItems = chestData.getMaxItems();

        //will shrink loot in chest if option is enabled
        if (minItems != -1 || maxItems != -1) {
            int min = minItems == -1 ? 0 : minItems;
            int extra = (maxItems == -1 ? notEmptyIndexes.size() : maxItems) - minItems;

            int itemCount = extra < 1 ? min : min + RandHelper.RAND.nextInt(extra);
            if (itemCount < notEmptyIndexes.size()) {
                int[] itemsRemain = new int[itemCount];
                for (int i = 0; i < itemsRemain.length; i++) {
                    int itemRemainArrIndex = RandHelper.RAND.nextInt(notEmptyIndexes.size());
                    int itemRemainChestIndex = notEmptyIndexes.get(itemRemainArrIndex);
                    notEmptyIndexes.remove(itemRemainArrIndex);

                    itemsRemain[i] = itemRemainChestIndex;
                }

                for (int i = 0; i < teChest.getContainerSize(); i++) {
                    boolean toDelete = true;

                    for (int i1 : itemsRemain) {
                        if (i == i1) {
                            toDelete = false;
                            break;
                        }
                    }

                    if (toDelete) {
                        teChest.removeItemNoUpdate(i);
                    }
                }
            }
        }
    }

    public static class SpawnChestData {
        private final ResourceLocation lootTableRL;
        private final String gameName;
        private final int minItems;
        private final int maxItems;

        public static SpawnChestData fromRewardConfig(LootGame<?> game, RewardConfig rewardConfig) {
            return new SpawnChestData(game, rewardConfig.getLootTable(game.getWorld()), rewardConfig.minItems.get(), rewardConfig.maxItems.get());
        }

        /**
         * @param game        game, which calls this method
         * @param lootTableRL loot table, from which items will be set in spawned chest.
         *                    If loot table won't be found, the game will place "sorry-stone" in the chest.
         * @param minItems    minimum amount of item stacks to be generated in chest.
         *                    Won't be applied, if count of items in bound loot table are less than it.
         *                    If min and max are set to -1, the limits will be disabled.
         * @param maxItems    maximum amount of item stacks to be generated in chest.
         *                    If this is set to -1, max limit will be disabled.
         */
        public SpawnChestData(LootGame<?> game, ResourceLocation lootTableRL, int minItems, int maxItems) {
            this.lootTableRL = lootTableRL;
            this.minItems = minItems;
            this.maxItems = maxItems;
            this.gameName = game.getClass().getSimpleName();
        }

        public String getGameName() {
            return gameName;
        }

        public int getMaxItems() {
            return maxItems;
        }

        public int getMinItems() {
            return minItems;
        }

        public ResourceLocation getLootTableKey() {
            return lootTableRL;
        }
    }
}