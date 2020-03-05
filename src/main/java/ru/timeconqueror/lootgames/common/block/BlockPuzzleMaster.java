package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.BlockGame;
import ru.timeconqueror.lootgames.api.minigame.GameManager;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPuzzleMaster;

import javax.annotation.Nullable;
import java.util.Random;

import static ru.timeconqueror.lootgames.common.world.gen.DungeonGenerator.*;

public class BlockPuzzleMaster extends BlockGame {
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, EntitySpawnPlacementRegistry.PlacementType type, @Nullable EntityType<?> entityType) {
        return false;
    }

    @Override
    public void randomTick(BlockState state, World worldIn, BlockPos pos, Random random) {
        int particleCount = random.nextInt(30);
        for (int i = 0; i <= particleCount; i++) {
            worldIn.addParticle(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5D + random.nextGaussian() * 0.8D,
                    pos.getY() + random.nextFloat(),
                    pos.getZ() + 0.5D + random.nextGaussian() * 0.8D,
                    random.nextGaussian() * 0.02D,
                    0.5D + random.nextGaussian() * 0.02D,
                    random.nextGaussian() * 0.02D);
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {

            try {//TODO 1.7.10 -> Change inactive msg
//                if (!LootGamesConfig.areMinigamesEnabled) {
//                    player.sendMessage(new TranslationTextComponent("msg.lootgames.puzzle_master.turned_off"));
//                    return true;
//                }//fixme uncomment

                BlockPos bottomPos = new BlockPos(pos.add(-PUZZLEROOM_CENTER_TO_BORDER, -PUZZLEROOM_MASTER_TE_OFFSET, -PUZZLEROOM_CENTER_TO_BORDER));
                BlockPos topPos = bottomPos.add(PUZZLEROOM_CENTER_TO_BORDER * 2 + 1, PUZZLEROOM_HEIGHT, PUZZLEROOM_CENTER_TO_BORDER * 2 + 1);

                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());

                GameManager.GenResult genResult = LootGamesAPI.getGameManager().generateRandomGame(worldIn, pos, bottomPos, topPos);
                if (genResult.wasGenerated()) {
                    //                AdvancementManager.BLOCK_ACTIVATED.trigger(player, pos, player.getHeldItem(handIn));//fixme uncomment
                } else {
                    player.sendMessage(new StringTextComponent(genResult.getError()));//TODO move error to lang file
                    worldIn.setBlockState(pos, state, 2);
                }
            } catch (Throwable e) {
                player.sendMessage(new TranslationTextComponent("msg.lootgames.puzzle_master.broken"));
                LootGames.LOGGER.error(e);
            }
        }

        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityPuzzleMaster();
    }
}
