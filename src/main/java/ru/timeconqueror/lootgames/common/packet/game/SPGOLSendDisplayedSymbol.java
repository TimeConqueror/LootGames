package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.api.packet.ServerGamePacket;
import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
import ru.timeconqueror.lootgames.minigame.gol.Symbol;

import java.io.IOException;

public class SPGOLSendDisplayedSymbol implements ServerGamePacket {
    private Symbol symbol;

    public SPGOLSendDisplayedSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    /**
     * Only for using via reflection
     */
    @Deprecated
    public SPGOLSendDisplayedSymbol() {
    }

    @Override
    public void encode(FriendlyByteBuf bufferTo) throws IOException {
        bufferTo.writeInt(symbol.getIndex());
    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) throws IOException {
        symbol = Symbol.byIndex(bufferFrom.readInt());
    }

    @Override
    public <S extends Stage> void runOnClient(LootGame<S> game) {
        ((GameOfLight) game).addDisplayedSymbol(symbol);
    }
}
