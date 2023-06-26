package ru.timeconqueror.lootgames.common.packet.game;

import lombok.NoArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import ru.timeconqueror.lootgames.api.minigame.LootGame;
import ru.timeconqueror.lootgames.api.minigame.Stage;
import ru.timeconqueror.lootgames.api.packet.NBTGamePacket;
import ru.timeconqueror.lootgames.room.GameSerializer;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

import java.util.Objects;

@NoArgsConstructor
public class SPChangeStage extends NBTGamePacket {
    public SPChangeStage(LootGame<?> game) {
        super(() -> {
            CompoundTag compoundNBT = new CompoundTag();
            GameSerializer.serializeStage(game, compoundNBT, SerializationType.SYNC);
            return compoundNBT;
        });
    }

    @Override
    public <S extends Stage> void runOnClient(LootGame<S> game) {
        CompoundTag compound = getCompound();
        Objects.requireNonNull(compound);

        S stage = GameSerializer.deserializeStage(game, compound, SerializationType.SYNC);
        game.switchStage(stage);
    }
}
