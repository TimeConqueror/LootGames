package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;

public class SPMSSpawnLevelBeatParticles implements IServerGamePacket {
    @Override
    public void encode(PacketBuffer bufferTo) {

    }

    @Override
    public void decode(PacketBuffer bufferFrom) {

    }

    @Override
    public <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game) {
        GameMineSweeper ms = (GameMineSweeper) game;
        BlockPos boardOrigin = ms.getBoardOrigin();
        for (int x = 0; x < ms.getCurrentBoardSize() + 1; x++) {
            for (int z = 0; z < ms.getCurrentBoardSize() + 1; z++) {
                ms.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, boardOrigin.getX() + x,
                        boardOrigin.getY() + 1.1F,
                        boardOrigin.getZ() + z, 0.0, 0.2, 0.0);
            }
        }
    }
}
