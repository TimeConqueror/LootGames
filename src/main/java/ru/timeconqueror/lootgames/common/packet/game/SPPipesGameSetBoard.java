package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.PacketBuffer;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;

public class SPPipesGameSetBoard implements IServerGamePacket {

    private int size;
    private int[] chunks;

    public SPPipesGameSetBoard(int size, int[] chunks) {
        this.size = size;
        this.chunks = chunks;
    }

    /**
     * Only for using via reflection
     */
    @Deprecated
    public SPPipesGameSetBoard() {
    }

    @Override
    public void encode(PacketBuffer bufferTo) {
        bufferTo.writeInt(size);
        bufferTo.writeVarIntArray(chunks);
    }

    @Override
    public void decode(PacketBuffer bufferFrom) {
        size = bufferFrom.readInt();
        chunks = bufferFrom.readVarIntArray();
    }

    @Override
    public <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game) {
        ((GamePipes) game).setBoardData(size, chunks);
    }
}