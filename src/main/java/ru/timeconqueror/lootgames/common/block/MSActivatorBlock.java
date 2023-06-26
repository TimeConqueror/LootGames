//package ru.timeconqueror.lootgames.common.block;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.phys.BlockHitResult;
//import ru.timeconqueror.lootgames.api.LootGamesAPI;
//import ru.timeconqueror.lootgames.api.block.GameBlock;
//import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger.ExtraInfo;
//import ru.timeconqueror.lootgames.common.config.LGConfigs;
//import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
//import ru.timeconqueror.lootgames.registry.LGBlocks;
//import ru.timeconqueror.lootgames.registry.LGSounds;
//
//public class MSActivatorBlock extends GameBlock {
//    @Override
//    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
//        if (!worldIn.isClientSide()) {
//            int allocatedSize = LGConfigs.MINESWEEPER.stage4.getBoardSize();
//
//            boolean succeed = LootGamesAPI.getBoardManager()
//                    .trySetupBoard(((ServerLevel) worldIn), pos, allocatedSize, 2, allocatedSize, LGBlocks.MS_MASTER.defaultBlockState(), player)
//                    .isSucceed();
//
//            if (succeed) {
//                worldIn.playSound(null, pos, LGSounds.MS_START_GAME, SoundSource.BLOCKS, 0.6F, 1.0F);
//                LGAdvancementTriggers.USE_BLOCK.trigger(((ServerPlayer) player), new ExtraInfo(state, pos, player.getItemInHand(handIn)));
//            }
//        }
//
//        return InteractionResult.SUCCESS;
//    }
//}
