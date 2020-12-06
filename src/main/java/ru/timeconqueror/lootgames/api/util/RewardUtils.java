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
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.timecore.util.DirectionTetra;
import ru.timeconqueror.timecore.util.RandHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RewardUtils {

    /**
     * Spawns chest with provided {@link SpawnChestData} in 1 block offset from central position.
     *
     * @param world      world where to spawn chest
     * @param centralPos position around which chest will be spawned
     * @param offset     in what direction of {@code centralPos} chest should be placed.
     *                   For example, if you set the offset to {@link DirectionTetra#NORTH},
     *                   chest will be spawned 1 block north of the {@code centralPos} with south facing.
     * @param chestData  data which contains the rules of setting chest content
     */
    public static void spawnLootChest(World world, BlockPos centralPos, DirectionTetra offset, SpawnChestData chestData) {
        if (world.isClientSide()) {
            return;
        }

        BlockState chest = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING,
                offset == DirectionTetra.WEST ? Direction.EAST :
                        offset == DirectionTetra.EAST ? Direction.WEST :
                                offset == DirectionTetra.NORTH ? Direction.SOUTH :
                                        Direction.NORTH);

        BlockPos placePos = centralPos.offset(offset.getOffsetX(), 0, offset.getOffsetZ());

        world.setBlockAndUpdate(placePos, chest);

        ChestTileEntity teChest = (ChestTileEntity) Objects.requireNonNull((world.getBlockEntity(placePos)));

        LockableLootTileEntity.setLootTable(world, world.random, placePos, chestData.getLootTableRL());
        teChest.setLootTable(chestData.getLootTableRL(), 0);
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
                stack.setTag(JsonToNBT.parseTag(String.format("{display:{Name:\"{\\\"text\\\":\\\"The Sorry Stone\\\", \\\"color\\\":\\\"blue\\\", \\\"bold\\\": \\\"true\\\"}\", Lore: [\"{\\\"text\\\":\\\"Modpack creator failed to configure the LootTables properly.\\\\nPlease report that Loot Table [%s] for %s stage is broken, thank you!\\\"}\"]}}", chestData.getLootTableRL(), chestData.getGameName())));//TODO when copying back to 1.12.2 - this tag don't work, only old one
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

        public ResourceLocation getLootTableRL() {
            return lootTableRL;
        }
    }
}