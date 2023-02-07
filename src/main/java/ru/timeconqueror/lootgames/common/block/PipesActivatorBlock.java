package ru.timeconqueror.lootgames.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.GameBlock;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PipesActivatorBlock extends GameBlock {
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide()) {
            boolean fieldPlaced = LootGamesAPI.getBoardManager().trySetupBoard(
                    (ServerLevel) worldIn,
                    pos, 15, 2, 15,
                    LGBlocks.PIPES_MASTER.defaultBlockState(),
                    player
            ).isSucceed();

            if (fieldPlaced) {
                worldIn.playSound(null, pos, LGSounds.MS_START_GAME, SoundSource.BLOCKS, 0.6F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        } else {
            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }
}
