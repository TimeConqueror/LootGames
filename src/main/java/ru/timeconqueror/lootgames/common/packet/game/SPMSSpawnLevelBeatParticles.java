package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.api.packet.ServerGamePacket;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;

public class SPMSSpawnLevelBeatParticles implements ServerGamePacket {
    @Override
    public void encode(FriendlyByteBuf bufferTo) {

    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) {

    }

    @Override
    public <S extends Stage> void runOnClient(LootGame<S> game) {
        GameMineSweeper ms = (GameMineSweeper) game;
        BlockPos boardOrigin = ms.getBoardOrigin();
        for (int x = 0; x < ms.getBoardSize() + 1; x++) {
            for (int z = 0; z < ms.getBoardSize() + 1; z++) {
                ms.getLevel().addParticle(ParticleTypes.HAPPY_VILLAGER, boardOrigin.getX() + x,
                        boardOrigin.getY() + 1.1F,
                        boardOrigin.getZ() + z, 0.0, 0.2, 0.0);
            }
        }
    }
}
