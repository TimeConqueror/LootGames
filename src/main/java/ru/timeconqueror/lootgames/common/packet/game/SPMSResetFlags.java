package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;

public class SPMSResetFlags implements IServerGamePacket {
    @Override
    public void encode(FriendlyByteBuf bufferTo) {

    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) {

    }

    @Override
    public <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game) {
        ((GameMineSweeper) game).getBoard().cSetFlaggedFields(0);
    }
}
