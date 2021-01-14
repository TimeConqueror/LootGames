package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.nbt.CompoundNBT;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.NBTGamePacket;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

//TODO why I every have this class, if I can just call tile sync?
public class SPMSGenBoard extends NBTGamePacket {

    /**
     * Only for using via reflection
     */
    public SPMSGenBoard() {
    }

    public SPMSGenBoard(GameMineSweeper game) {
        super(() -> {
            CompoundNBT nbt = new CompoundNBT();
            game.writeNBT(nbt, SerializationType.SYNC);

            return nbt;
        });
    }

    @Override
    public <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game) {
        game.readNBT(getCompound(), SerializationType.SYNC);
    }
}
