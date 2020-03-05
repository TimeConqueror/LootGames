package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

public abstract class NBTGamePacket<T extends LootGame> implements IGamePacket<T> {
    private CompoundNBT compound;

    public NBTGamePacket(@NotNull CompoundNBT compound) {
        this.compound = compound;
    }

    @Override
    public void encode(PacketBuffer bufferTo) {
        bufferTo.writeCompoundTag(compound);
    }

    @Override
    public void decode(PacketBuffer bufferFrom) {
        compound = bufferFrom.readCompoundTag();
    }
}
