package ru.timeconqueror.lootgames.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.GameBlock;
import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger.ExtraInfo;
import ru.timeconqueror.lootgames.common.block.tile.PuzzleMasterTile;
import ru.timeconqueror.lootgames.common.config.LGConfigs;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.lootgames.registry.LGBlockEntities;
import ru.timeconqueror.timecore.api.util.NetworkUtils;
import ru.timeconqueror.timecorex.api.util.ITickableBlockEntity;

import javax.annotation.Nullable;
import java.util.Optional;

//TODO check surface before placing game block
//TODO add animation, which places block below
public class PuzzleMasterBlock extends GameBlock implements EntityBlock {

    public PuzzleMasterBlock() {
        super(GameBlock.DEF_PROPS.create().lightLevel(value -> 15));
    }

    @Override
    public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
        int particleCount = rand.nextInt(30);
        for (int i = 0; i <= particleCount; i++) {
            level.addParticle(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5D + rand.nextGaussian() * 0.8D,
                    pos.getY() + rand.nextFloat(),
                    pos.getZ() + 0.5D + rand.nextGaussian() * 0.8D,
                    rand.nextGaussian() * 0.02D,
                    0.5D + rand.nextGaussian() * 0.02D,
                    rand.nextGaussian() * 0.02D);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide()) {
            try {
                if (LGConfigs.GENERAL.disableMinigames.get()) {
                    NetworkUtils.sendMessage(player, Component.translatable("msg.lootgames.puzzle_master.turned_off"));
                    return InteractionResult.SUCCESS;
                }

                worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

                Optional<String> error = LootGamesAPI.getGameManager().generateRandomGame(worldIn, pos);
                if (error.isEmpty()) {
                    LGAdvancementTriggers.USE_BLOCK.trigger((ServerPlayer) player, new ExtraInfo(state, pos, player.getItemInHand(handIn)));
                } else {
                    NetworkUtils.sendMessage(player, Component.literal(error.get()));//TODO move error to lang file
                    worldIn.setBlock(pos, state, 2);//rollback
                }
            } catch (Throwable e) {
                NetworkUtils.sendMessage(player, Component.translatable("msg.lootgames.puzzle_master.broken"));
                LootGames.LOGGER.error(e);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PuzzleMasterTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level_, BlockState state_, BlockEntityType<T> type) {
        return ITickableBlockEntity.makeTicker(type, LGBlockEntities.PUZZLE_MASTER);
    }
}
