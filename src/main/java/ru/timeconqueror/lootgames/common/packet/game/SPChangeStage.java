package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.nbt.CompoundNBT;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.packet.NBTGamePacket;

public class SPChangeStage extends NBTGamePacket {
    /**
     * Only for using via reflection
     */
    public SPChangeStage() {
    }

    public SPChangeStage(LootGame<?> game) {
        super(game.getStage() != null ? game.getStage().serialize() : null);
    }


    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void runOnClient(LootGame<?> game) {
        CompoundNBT compound = getCompound();

        LootGame.Stage stage = compound != null ? game.createStageFromNBT(compound) : null;
        game.switchStage(stage);
    }
}
