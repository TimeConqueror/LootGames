package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.api.packet.ServerGamePacket;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;

public class SPMSResetFlags implements ServerGamePacket {
    @Override
    public void encode(FriendlyByteBuf bufferTo) {

    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) {

    }

    @Override
    public <S extends Stage> void runOnClient(LootGame<S> game) {
        ((GameMineSweeper) game).getBoard().cSetFlaggedFields(0);
    }
}
