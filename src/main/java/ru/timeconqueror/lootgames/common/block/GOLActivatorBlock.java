package ru.timeconqueror.lootgames.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ru.timeconqueror.lootgames.api.LootGamesAPI;
import ru.timeconqueror.lootgames.api.block.GameBlock;
import ru.timeconqueror.lootgames.api.minigame.NotifyColor;
import ru.timeconqueror.lootgames.common.advancement.UseBlockTrigger.ExtraInfo;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.registry.LGAdvancementTriggers;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.lootgames.registry.LGSounds;
import ru.timeconqueror.timecore.api.util.ChatUtils;
import ru.timeconqueror.timecore.api.util.NetworkUtils;

public class GOLActivatorBlock extends GameBlock {
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide()) {

            boolean succeed = LootGamesAPI.getFieldManager()
                    .trySetupBoard(((ServerLevel) worldIn), pos, GameOfLight.BOARD_SIZE, 2, GameOfLight.BOARD_SIZE, LGBlocks.GOL_MASTER.defaultBlockState(), player).isSucceed();

            if (succeed) {
                NetworkUtils.sendMessage(player, ChatUtils.format(new TranslatableComponent("msg.lootgames.gol.start"), NotifyColor.NOTIFY.getColor()));
                worldIn.playSound(null, pos, LGSounds.GOL_START_GAME, SoundSource.MASTER, 0.75F, 1.0F);
                LGAdvancementTriggers.USE_BLOCK.trigger(((ServerPlayer) player), new ExtraInfo(state, pos, player.getItemInHand(handIn)));
            }
        }

        return InteractionResult.SUCCESS;
    }
}
