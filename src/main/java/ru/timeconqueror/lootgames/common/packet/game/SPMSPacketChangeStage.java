package ru.timeconqueror.lootgames.common.packet.game;

import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.NBTGamePacket;
import ru.timeconqueror.lootgames.minigame.minesweeper.GameMineSweeper;

public class SPMSPacketChangeStage extends NBTGamePacket<GameMineSweeper> {
    public SPMSPacketChangeStage(GameMineSweeper game) {
        super(game.getStage().serialize());
    }

    @Override
    public void runOnClient(LootGame<GameMineSweeper> game) {
        game.switchStage(game.createStageFromNBT(getCompound()));
    }
}
