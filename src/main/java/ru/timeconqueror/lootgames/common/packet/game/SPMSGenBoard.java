package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.nbt.CompoundNBT;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.NBTGamePacket;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;

public class SPMSGenBoard extends NBTGamePacket {

    /**
     * Only for using via reflection
     */
    public SPMSGenBoard() {
    }

    public SPMSGenBoard(GameMineSweeper game) {
        super(() -> {
            CompoundNBT nbt = new CompoundNBT();
            game.writeNBTForClient(nbt);

            return nbt;
        });
    }

    @Override
    public void runOnClient(LootGame<?> game) {
        game.readNBTAtClient(getCompound());
    }
}
