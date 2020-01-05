package ru.timeconqueror.lootgames.api.util;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameUtils {

    //TODO comment
    public static void spawnLootChest(World world, BlockPos centralPos, DirectionTetra offset, SpawnChestInfo chestInfo) {
        if (world.isRemote) {
            return;
        }

        IBlockState chest = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, offset == DirectionTetra.NORTH ? EnumFacing.SOUTH : offset == DirectionTetra.SOUTH ? EnumFacing.NORTH : offset == DirectionTetra.EAST ? EnumFacing.WEST : EnumFacing.EAST);

        BlockPos placePos = centralPos.add(offset.getOffsetX(), 0, offset.getOffsetZ());

        System.out.println(world.getBlockState(placePos));
        world.setBlockState(placePos, chest);

        System.out.println(world.getBlockState(placePos));

        TileEntityChest teChest = (TileEntityChest) Objects.requireNonNull((world.getTileEntity(placePos)));

        teChest.setLootTable(chestInfo.getLootTableRL(), 0);
        teChest.fillWithLoot(null);

        List<Integer> notEmptyIndexes = new ArrayList<>();
        for (int i = 0; i < teChest.getSizeInventory(); i++) {
            if (teChest.getStackInSlot(i) != ItemStack.EMPTY) {
                notEmptyIndexes.add(i);
            }
        }

        if (notEmptyIndexes.size() == 0) {
            ItemStack stack = new ItemStack(Blocks.STONE);
            try {
                stack.setTagCompound(JsonToNBT.getTagFromJson(String.format("{display:{Name:\"The Sorry Stone\",Lore:[\"Modpack creator failed to configure the LootTables properly.\",\"Please report that LootList [%s] for %s stage is broken, thank you!\"]}}", chestInfo.getLootTableRL(), chestInfo.getGameName())));
            } catch (NBTException e) {
                e.printStackTrace();
            }

            teChest.setInventorySlotContents(0, stack);

            return;
        }

        int minItems = chestInfo.getMinItems();
        int maxItems = chestInfo.getMaxItems();

        //will shrink loot in chest if option is enabled
        if (minItems != -1 || maxItems != -1) {
            int min = minItems == -1 ? 0 : minItems;
            int extra = (maxItems == -1 ? notEmptyIndexes.size() : maxItems) - minItems;

            int itemCount = extra < 1 ? min : min + LootGames.RAND.nextInt(extra);
            if (itemCount < notEmptyIndexes.size()) {
                int[] itemsRemain = new int[itemCount];
                for (int i = 0; i < itemsRemain.length; i++) {
                    int itemRemainArrIndex = LootGames.RAND.nextInt(notEmptyIndexes.size());
                    int itemRemainChestIndex = notEmptyIndexes.get(itemRemainArrIndex);
                    notEmptyIndexes.remove(itemRemainArrIndex);

                    itemsRemain[i] = itemRemainChestIndex;
                }

                for (int i = 0; i < teChest.getSizeInventory(); i++) {
                    boolean toDelete = true;

                    for (int i1 : itemsRemain) {
                        if (i == i1) {
                            toDelete = false;
                            break;
                        }
                    }

                    if (toDelete) {
                        teChest.removeStackFromSlot(i);
                    }
                }
            }
        }
    }

    public static class SpawnChestInfo {
        private ResourceLocation lootTableRL;
        private Class<? extends LootGame> gameClass;
        private int minItems;
        private int maxItems;

        public SpawnChestInfo(Class<? extends LootGame> gameClass, ResourceLocation lootTableRL, int minItems, int maxItems) {
            this.lootTableRL = lootTableRL;
            this.minItems = minItems;
            this.maxItems = maxItems;
            this.gameClass = gameClass;
        }

        public String getGameName() {
            return gameClass.getSimpleName();
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
