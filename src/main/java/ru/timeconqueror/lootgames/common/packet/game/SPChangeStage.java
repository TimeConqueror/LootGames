package ru.timeconqueror.lootgames.common.packet.game;

import net.minecraft.nbt.CompoundNBT;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.StageSerializationType;
import ru.timeconqueror.lootgames.api.packet.NBTGamePacket;

public class SPChangeStage extends NBTGamePacket {
    /**
     * Only for using via reflection
     */
    public SPChangeStage() {
    }

    public SPChangeStage(LootGame<?, ?> game) {
        super(() -> {
            CompoundNBT compoundNBT = new CompoundNBT();
            LootGame.serializeStage(game, compoundNBT, StageSerializationType.SYNC);
            return compoundNBT;
        });
    }

    @Override
    public <S extends LootGame.Stage, T extends LootGame<S, T>> void runOnClient(LootGame<S, T> game) {
        CompoundNBT compound = getCompound();

        S stage = LootGame.deserializeStage(game, compound, StageSerializationType.SYNC);
        game.switchStage(stage);
    }
}
