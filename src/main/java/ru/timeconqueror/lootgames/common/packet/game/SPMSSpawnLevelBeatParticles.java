package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
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
    public <T extends LootGame<T>> void runOnClient(LootGame<T> game) {
        GameMineSweeper ms = (GameMineSweeper) game;
        for (int x = 0; x < ms.getBoardSize() + 1; x++) {
            for (int z = 0; z < ms.getBoardSize() + 1; z++) {
                ms.getWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, ms.getMasterPos().getX() + x,
                        ms.getGameCenter().getY() + 1.1F,
                        ms.getMasterPos().getZ() + z, 0.0, 0.2, 0.0);
            }
        }
    }
}
