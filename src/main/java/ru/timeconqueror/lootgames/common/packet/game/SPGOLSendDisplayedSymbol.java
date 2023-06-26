//package ru.timeconqueror.lootgames.common.packet.game;
//
//import net.minecraft.network.FriendlyByteBuf;
//import ru.timeconqueror.lootgames.api.minigame.LootGame;
//import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
//import ru.timeconqueror.lootgames.minigame.gol.GameOfLight;
//import ru.timeconqueror.lootgames.minigame.gol.Symbol;
//
//import java.io.IOException;
//
//public class SPGOLSendDisplayedSymbol implements IServerGamePacket {
//    private Symbol symbol;
//
//    public SPGOLSendDisplayedSymbol(Symbol symbol) {
//        this.symbol = symbol;
//    }
//
//    /**
//     * Only for using via reflection
//     */
//    @Deprecated
//    public SPGOLSendDisplayedSymbol() {
//    }
//
//    @Override
//    public void encode(FriendlyByteBuf bufferTo) throws IOException {
//        bufferTo.writeInt(symbol.getIndex());
//    }
//
//    @Override
//    public void decode(FriendlyByteBuf bufferFrom) throws IOException {
//        symbol = Symbol.byIndex(bufferFrom.readInt());
//    }
//
//    @Override
//    public <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game) {
//        ((GameOfLight) game).addDisplayedSymbol(symbol);
//    }
//}
