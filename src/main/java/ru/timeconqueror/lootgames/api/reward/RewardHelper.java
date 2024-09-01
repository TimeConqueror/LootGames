package ru.timeconqueror.lootgames.api.reward;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
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
import ru.timeconqueror.lootgames.common.config.base.RewardConfig;
import ru.timeconqueror.lootgames.common.config.base.StagedRewardConfig.FourStagedRewardConfig;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;
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
    public static void spawnFourStagedReward(@Nullable ServerPlayer player, ServerLevel level, GameInfo gameInfo, BlockPos centralPos, int rewardLevel, FourStagedRewardConfig rewardConfig) {
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

                spawnLootChest(player, level, centralPos, direction, SpawnChestData.fromRewardConfig(gameInfo, rewardConfig.getStageByIndex(counter)));
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
        Direction direction = horizontalDirection.toVanilla();

        BlockState chest = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, direction.getOpposite());

        BlockPos placePos = centralPos.offset(direction.getStepX(), 0, direction.getStepZ());

        level.setBlockAndUpdate(placePos, chest);

        ChestBlockEntity chestEntity = (ChestBlockEntity) Objects.requireNonNull((level.getBlockEntity(placePos)));

        WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0));
        random.setDecorationSeed(level.getSeed(), placePos.getX(), placePos.getZ());

        ObjectArrayList<ItemStack> loot = generateRewards(player, level, placePos, random, chestData, chestEntity.getContainerSize());

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
        ResourceLocation lootTableId = data.getLootTableId();

        LootTable lootTable = Objects.requireNonNull(level.getServer())
                .getLootData()
                .getLootTable(lootTableId);

        ObjectArrayList<ItemStack> stacks;
        if(lootTable != LootTable.EMPTY) {
            LootParams lootParams = makeLootParams(player, level, pos);
            stacks = lootTable.getRandomItems(lootParams, randomSource.nextLong());
        } else {
            stacks = new ObjectArrayList<>();
            stacks.add(makeSorryStone(data.getGameInfo(), lootTableId));
        }

        int freeSlots = inventorySize - stacks.size();
        for (int i = 0; i < freeSlots; i++) {
            stacks.add(ItemStack.EMPTY);
        }

        Util.shuffle(stacks, randomSource);
        return stacks;
    }

    private static ItemStack makeSorryStone(GameInfo gameInfo, ResourceLocation lootTableId) {
        ItemStack stack = new ItemStack(Blocks.STONE);

        stack.setHoverName(Component.literal("The Sorry Stone").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD));
        ItemStackExt.addLore(stack, List.of(
                Component.literal("Modpack creator linked reward to unknown loot table: %s.".formatted(lootTableId)),
                Component.literal("Please report that it is used in %s game, thank you!".formatted(I18n.get(gameInfo.getLangKey())))));
        return stack;
    }

    @Getter
    public static class SpawnChestData {
        private final ResourceLocation lootTableId;
        private final GameInfo gameInfo;

        public static SpawnChestData fromRewardConfig(GameInfo gameInfo, RewardConfig rewardConfig) {
            return new SpawnChestData(gameInfo, rewardConfig.getLootTableId());
        }

        /**
         * @param gameInfo                id of game, which calls this method
         * @param lootTableId id of loot table, from which items will be set in spawned chest.
         *                            If the resulting loot table is empty, the game will place "sorry-stone" duct-tape item in the chest.
         */
        public SpawnChestData(GameInfo gameInfo, ResourceLocation lootTableId) {
            this.gameInfo = gameInfo;
            this.lootTableId = lootTableId;
        }
    }
}