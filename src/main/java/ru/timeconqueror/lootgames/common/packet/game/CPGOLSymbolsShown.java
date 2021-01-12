package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import org.apache.commons.lang3.NotImplementedException;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.IClientGamePacket;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;

import java.io.IOException;

public class CPGOLSymbolsShown implements IClientGamePacket {
    @Override
    public void encode(PacketBuffer bufferTo) throws IOException {

    }

    @Override
    public void decode(PacketBuffer bufferFrom) throws IOException {

    }

    @Override
    public <STAGE extends LootGame.Stage, G extends LootGame<STAGE, G>> void runOnServer(ServerPlayerEntity sender, LootGame<STAGE, G> game) {
        GameMineSweeper game1 = (GameMineSweeper) game;
        throw new NotImplementedException("");
    }
}
