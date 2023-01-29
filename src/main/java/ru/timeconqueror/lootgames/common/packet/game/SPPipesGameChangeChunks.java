package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;

public class SPPipesGameChangeChunks implements IServerGamePacket {

    private int[] ids;
    private int[] chunks;

    public SPPipesGameChangeChunks(int[] ids, int[] chunks) {
        this.ids = ids;
        this.chunks = chunks;
    }

    /**
     * Only for using via reflection
     */
    @Deprecated
    public SPPipesGameChangeChunks() {
    }

    @Override
    public void encode(FriendlyByteBuf bufferTo) {
        bufferTo.writeVarIntArray(ids);
        bufferTo.writeVarIntArray(chunks);
    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) {
        ids = bufferFrom.readVarIntArray();
        chunks = bufferFrom.readVarIntArray();
    }

    @Override
    public <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game) {
        ((GamePipes) game).getBoard().setChunks(ids, chunks);
    }
}