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
        super(() -> {
            CompoundNBT compoundNBT = new CompoundNBT();
            LootGame.serializeStage(game, compoundNBT);
            return compoundNBT;
        });
    }

    @Override
    public <T extends LootGame<T>> void runOnClient(LootGame<T> game) {
        CompoundNBT compound = getCompound();

        LootGame.Stage<T> stage = LootGame.deserializeStage(game, compound);
        game.switchStage(stage);
    }
}
