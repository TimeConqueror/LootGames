package ru.timeconqueror.lootgames.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isClientSide()) {

            boolean succeed = LootGamesAPI.getFieldManager()
                    .trySetupBoard(((ServerWorld) worldIn), pos, GameOfLight.BOARD_SIZE, 2, GameOfLight.BOARD_SIZE, LGBlocks.GOL_MASTER.defaultBlockState(), player).isSucceed();

            if (succeed) {
                NetworkUtils.sendMessage(player, ChatUtils.format(new TranslationTextComponent("msg.lootgames.gol.start"), NotifyColor.NOTIFY.getColor()));
                worldIn.playSound(null, pos, LGSounds.GOL_START_GAME, SoundCategory.MASTER, 0.75F, 1.0F);
                LGAdvancementTriggers.USE_BLOCK.trigger(((ServerPlayerEntity) player), new ExtraInfo(state, pos, player.getItemInHand(handIn)));
            }
        }

        return ActionResultType.SUCCESS;
    }
}
