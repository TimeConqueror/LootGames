package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.IClientGamePacket;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;

import java.io.IOException;

public class CPGOLSymbolsShown implements IClientGamePacket {
    @Override
    public void encode(FriendlyByteBuf bufferTo) throws IOException {
    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) throws IOException {
    }

    @Override
    public <STAGE extends LootGame.Stage, G extends LootGame<STAGE, G>> void runOnServer(ServerPlayer sender, LootGame<STAGE, G> game) {
        if (game instanceof GameOfLight && game.getStage() instanceof GameOfLight.StageShowSequence) {
            ((GameOfLight.StageShowSequence) game.getStage()).onSequenceShown();
        }
    }
}
