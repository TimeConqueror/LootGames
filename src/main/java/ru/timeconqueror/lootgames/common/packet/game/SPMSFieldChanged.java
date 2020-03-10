package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.network.PacketBuffer;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.IServerGamePacket;
import ru.timeconqueror.lootgames.api.util.Pos2i;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.lootgames.minigame.minesweeper.MSBoard;

public class SPMSFieldChanged implements IServerGamePacket<GameMineSweeper> {
    private Pos2i pos;
    private int type;
    private MSBoard.Mark mark;
    private boolean hidden;

    public SPMSFieldChanged(Pos2i pos, int type, MSBoard.Mark mark, boolean hidden) {
        this.pos = pos;
        this.type = type;
        this.mark = mark;
        this.hidden = hidden;
    }

    @Override
    public void encode(PacketBuffer bufferTo) {
        bufferTo.writeInt(pos.getX());
        bufferTo.writeInt(pos.getY());
        bufferTo.writeInt(type);
        bufferTo.writeInt(mark.getID());
        bufferTo.writeBoolean(hidden);
    }

    @Override
    public void decode(PacketBuffer bufferFrom) {
        pos = new Pos2i(bufferFrom.readInt(), bufferFrom.readInt());
        type = bufferFrom.readInt();
        mark = MSBoard.Mark.byID(bufferFrom.readInt());
        hidden = bufferFrom.readBoolean();
    }

    @Override
    public void runOnClient(LootGame<GameMineSweeper> game) {
        game.typed().getBoard().cSetField(pos, type, mark, hidden);
    }
}
