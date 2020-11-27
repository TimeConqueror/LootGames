package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.BlockGame;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPuzzleMaster;
import ru.timeconqueror.timecore.util.NetworkUtils;

import javax.annotation.Nullable;
import java.util.Random;

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
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
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
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {

            try {
//                if (!LootGamesConfig.areMinigamesEnabled) {
//                    player.sendMessage(new TranslationTextComponent("msg.lootgames.puzzle_master.turned_off"));
//                    return true;
//                }//fixme uncomment
                //fixme uncomment
//                BlockPos bottomPos = new BlockPos(pos.offset(-GameDungeonStructure.ROOM_WIDTH / 2 + 1, -GameDungeonStructure.MASTER_BLOCK_OFFSET, -GameDungeonStructure.ROOM_WIDTH / 2 + 1));
//                BlockPos topPos = bottomPos.offset(GameDungeonStructure.ROOM_WIDTH, -GameDungeonStructure.ROOM_HEIGHT, GameDungeonStructure.ROOM_WIDTH);

                worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

//                GameManager.GenResult genResult = LootGamesAPI.getGameManager().generateRandomGame(worldIn, pos, bottomPos, topPos);
//                if (genResult.wasGenerated()) {
////                    LGAdvancementTriggers.ACTIVATE_BLOCK.trigger((ServerPlayerEntity) player, new ExtraInfo(pos, player.getHeldItem(handIn)));TODO restore
//                } else {
//                    NetworkUtils.sendMessage(player, new StringTextComponent(genResult.getError()));//TODO move error to lang file
//                    worldIn.setBlock(pos, state, 2);
//                }
            } catch (Throwable e) {
                NetworkUtils.sendMessage(player, new TranslationTextComponent("msg.lootgames.puzzle_master.broken"));
                LootGames.LOGGER.error(e);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityPuzzleMaster();
    }
}
