package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.api.packet.ServerGamePacket;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard;
import ru.timeconqueror.timecore.api.util.CodecUtils;

import java.io.IOException;

public class SPMSFieldChanged implements ServerGamePacket {
    private Vector2ic pos;
    private MSBoard.MSField field;

    /**
     * Only for using via reflection
     */
    @Deprecated
    public SPMSFieldChanged() {
    }

    public SPMSFieldChanged(Vector2ic pos, MSBoard.MSField field) {
        this.pos = pos;
        this.field = field;
    }

    @Override
    public void encode(FriendlyByteBuf bufferTo) throws IOException {
        bufferTo.writeInt(pos.x());
        bufferTo.writeInt(pos.y());

        bufferTo.writeWithCodec(CodecUtils.NBT_OPS, MSBoard.MSField.SYNC_CODEC, field);
    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) throws IOException {
        pos = new Vector2i(bufferFrom.readInt(), bufferFrom.readInt());

        this.field = bufferFrom.readWithCodec(CodecUtils.NBT_OPS, MSBoard.MSField.SYNC_CODEC);
    }

    @Override
    public <S extends Stage> void runOnClient(LootGame<S> game) {
        ((GameMineSweeper) game).getBoard().cSetField(pos, field);
    }
}
