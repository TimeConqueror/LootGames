package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
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
import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger.ExtraInfo;
import ru.timeconqueror.lootgames.common.block.tile.TileEntityPuzzleMaster;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.timecore.api.util.NetworkUtils;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class BlockPuzzleMaster extends BlockGame {

    public BlockPuzzleMaster() {
        super(BlockGame.DEF_PROPS.create().lightLevel(value -> 15));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        int particleCount = rand.nextInt(30);
        for (int i = 0; i <= particleCount; i++) {
            worldIn.addParticle(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5D + rand.nextGaussian() * 0.8D,
                    pos.getY() + rand.nextFloat(),
                    pos.getZ() + 0.5D + rand.nextGaussian() * 0.8D,
                    rand.nextGaussian() * 0.02D,
                    0.5D + rand.nextGaussian() * 0.02D,
                    rand.nextGaussian() * 0.02D);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {
            try {
                if (LGConfigs.GENERAL.disableMinigames.get()) {
                    NetworkUtils.sendMessage(player, new TranslationTextComponent("msg.lootgames.puzzle_master.turned_off"));
                    return ActionResultType.SUCCESS;
                }

                worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

                Optional<String> error = LootGamesAPI.getGameManager().generateRandomGame(worldIn, pos);
                if (!error.isPresent()) {
                    LGAdvancementTriggers.USE_BLOCK.trigger((ServerPlayerEntity) player, new ExtraInfo(state, pos, player.getItemInHand(handIn)));
                } else {
                    NetworkUtils.sendMessage(player, new StringTextComponent(error.get()));//TODO move error to lang file
                    worldIn.setBlock(pos, state, 2);
                }
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
