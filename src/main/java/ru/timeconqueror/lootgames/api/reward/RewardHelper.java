package ru.timeconqueror.lootgames.api.reward;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.common.config.base.RewardConfig;
import ru.timeconqueror.lootgames.common.config.base.StagedRewardConfig.FourStagedRewardConfig;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.utils.ItemStackExt;
import ru.timeconqueror.timecore.api.util.HorizontalDirection;
import ru.timeconqueror.timecore.api.util.MathUtils;

import java.util.List;
import java.util.Objects;

public class RewardHelper {
    /**
     * Spawns lamp decoration and up to four chests depending on provided {@code rewardLevel}
     *
     * @param level        level where to spawn chest
     * @param centralPos   position around which chests will be spawned
     * @param rewardLevel  from 0 to 4 inclusive. Zero means no chests will be generated.
     * @param rewardConfig reward part of your game's config
     */
    public static void spawnFourStagedReward(@Nullable ServerPlayer player, ServerLevel level, LootGame<?> game, BlockPos centralPos, int rewardLevel, FourStagedRewardConfig rewardConfig) {
        BlockState state = LGBlocks.DUNGEON_LAMP.defaultBlockState();
        level.setBlockAndUpdate(centralPos.offset(1, 0, 1), state);
        level.setBlockAndUpdate(centralPos.offset(1, 0, -1), state);
        level.setBlockAndUpdate(centralPos.offset(-1, 0, 1), state);
        level.setBlockAndUpdate(centralPos.offset(-1, 0, -1), state);

        rewardLevel = MathUtils.coerceInRange(rewardLevel, 0, 4);

        if (rewardLevel > 0) {
            int counter = 0;
            for (HorizontalDirection direction : HorizontalDirection.values()) {
                if (counter >= rewardLevel) break;

                spawnLootChest(player, level, centralPos, direction, SpawnChestData.fromRewardConfig(game, rewardConfig.getStageByIndex(counter)));
                counter++;
            }
        }
    }

    /**
     * Spawns chest with provided {@link SpawnChestData} in 1 block direction from central position.
     *
     * @param level               level where to spawn chest
     * @param centralPos          position around which chest will be spawned
     * @param horizontalDirection in what direction of {@code centralPos} chest should be placed.
     *                            For example, if you set the direction to {@link HorizontalDirection#NORTH},
     *                            chest will be spawned 1 block north of the {@code centralPos} with south facing.
     * @param chestData           data which contains the rules of setting chest content
     */
    public static void spawnLootChest(@Nullable ServerPlayer player, ServerLevel level, BlockPos centralPos, HorizontalDirection horizontalDirection, SpawnChestData chestData) {
        Direction direction = horizontalDirection.get();

        BlockState chest = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, direction.getOpposite());

        BlockPos placePos = centralPos.offset(direction.getStepX(), 0, direction.getStepZ());

        level.setBlockAndUpdate(placePos, chest);

        ChestBlockEntity chestEntity = (ChestBlockEntity) Objects.requireNonNull((level.getBlockEntity(placePos)));

        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0));
        random.setDecorationSeed(level.getSeed(), placePos.getX(), placePos.getZ());

        ObjectArrayList<ItemStack> loot = generateRewards(player, level, placePos, random, chestData, chestEntity.getContainerSize());
        if (loot.isEmpty()) {
            loot.add(makeSorryStone(chestData.getGameId()));
        }


        for (int i = 0; i < loot.size(); i++) {
            chestEntity.setItem(i, loot.get(i));
        }
    }

    private static LootParams makeLootParams(@Nullable ServerPlayer player, ServerLevel level, BlockPos pos) {
        LootParams.Builder lootParamsBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos));
        if (player != null) {
            lootParamsBuilder
                    .withLuck(player.getLuck())
                    .withParameter(LootContextParams.THIS_ENTITY, player);
        }
        return lootParamsBuilder.create(LootContextParamSets.CHEST);
    }

    private static ObjectArrayList<ItemStack> generateRewards(@Nullable ServerPlayer player, ServerLevel level, BlockPos pos, RandomSource randomSource, SpawnChestData data, int inventorySize) {
        int maxItemCount = inventorySize;
        if (data.getMaxItems() > 0) {
            maxItemCount = Math.min(maxItemCount, data.getMaxItems());
        }

        int minItemCount = maxItemCount;
        if (data.getMinItems() > 0) {
            minItemCount = data.getMinItems();
        }

        int itemCount = minItemCount + (maxItemCount - minItemCount > 0 ? randomSource.nextInt(maxItemCount - minItemCount) : 0);

        Object2IntOpenHashMap<LootTable> tableMaxItems = new Object2IntOpenHashMap<>();
        LootTablePool lootTablePool = data.getLootTablePool();
        for (int i = 0; i < itemCount; i++) {
            LootTable lootTable = lootTablePool.chooseRandom(level, randomSource);
            int prevCount = tableMaxItems.getInt(lootTable);
            tableMaxItems.put(lootTable, prevCount + 1);
        }

        LootParams lootParams = makeLootParams(player, level, pos);
        ObjectArrayList<ItemStack> outStacks = new ObjectArrayList<>(inventorySize);
        for (Object2IntMap.Entry<LootTable> entry : tableMaxItems.object2IntEntrySet()) {
            LootTable lootTable = entry.getKey();
            ObjectArrayList<ItemStack> stacks = lootTable.getRandomItems(lootParams, randomSource.nextLong());

            for (int i = 0; i < entry.getIntValue(); i++) {
                if (i < stacks.size()) {
                    outStacks.add(stacks.get(i));
                }
            }
        }

        int freeSlots = inventorySize - outStacks.size();
        for (int i = 0; i < freeSlots; i++) {
            outStacks.add(ItemStack.EMPTY);
        }

        Util.shuffle(outStacks, randomSource);
        return outStacks;
    }

    private static ItemStack makeSorryStone(ResourceLocation gameId) {
        ItemStack stack = new ItemStack(Blocks.STONE);

        stack.setHoverName(Component.literal("The Sorry Stone").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD));
        ItemStackExt.setLore(stack, List.of(
                Component.literal("Modpack creator failed to configure the Loot Tables properly."),
                Component.literal(String.format("Please report that one of Loot Table Pool for %s is broken, thank you!", gameId))));
        return stack;
    }

    @Getter
    public static class SpawnChestData {
        private final LootTablePool lootTablePool;
        private final ResourceLocation gameId;
        private final int minItems;
        private final int maxItems;

        public static SpawnChestData fromRewardConfig(LootGame<?> game, RewardConfig rewardConfig) {
            return new SpawnChestData(game, rewardConfig.getLootTablePool(), rewardConfig.minItems.get(), rewardConfig.maxItems.get());
        }

        /**
         * @param game          game, which calls this method
         * @param lootTablePool pool of loot table, from which items will be set in spawned chest.
         *                      If no loot table is in the pool, the game will place "sorry-stone" duct-tape item in the chest.
         * @param minItems      minimum amount of item stacks to be generated in chest.
         *                      Won't be applied, if count of items in bound loot table are less than it.
         *                      If min and max are set to -1, the limits will be disabled.
         * @param maxItems      maximum amount of item stacks to be generated in chest.
         *                      If this is set to -1, max limit will be disabled.
         */
        public SpawnChestData(LootGame<?> game, LootTablePool lootTablePool, int minItems, int maxItems) {
            this.gameId = game.getId();
            this.lootTablePool = lootTablePool;
            this.minItems = minItems;
            this.maxItems = maxItems;
        }
    }
}