package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.PacketBuffer;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.minigame.pipes.GamePipes;

public class PacketPipesGameChangeChunks implements IServerGamePacket {

    private int[] ids;
    private int[] chunks;

    public PacketPipesGameChangeChunks(int[] ids, int[] chunks) {
        this.ids = ids;
        this.chunks = chunks;
    }

    /**
     * Only for using via reflection
     */
    public PacketPipesGameChangeChunks() {
    }

    @Override
    public void encode(PacketBuffer bufferTo) {
        bufferTo.writeVarIntArray(ids);
        bufferTo.writeVarIntArray(chunks);
    }

    @Override
    public void decode(PacketBuffer bufferFrom) {
        ids = bufferFrom.readVarIntArray();
        chunks = bufferFrom.readVarIntArray();
    }

    @Override
    public void runOnClient(LootGame<?> game) {
        ((GamePipes) game).getBoard().setChunks(ids, chunks);
    }
}