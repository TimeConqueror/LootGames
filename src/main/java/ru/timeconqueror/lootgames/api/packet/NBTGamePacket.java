package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.api.minigame.LootGame;

import java.util.function.Supplier;

public abstract class NBTGamePacket<T extends LootGame<T>> implements IServerGamePacket<T> {
    private CompoundNBT compound;

    public NBTGamePacket(@NotNull Supplier<CompoundNBT> compoundSupplier) {
        this(compoundSupplier.get());
    }

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

    public CompoundNBT getCompound() {
        return compound;
    }

    public void setCompound(CompoundNBT compound) {
        this.compound = compound;
    }

    public void addNBT(String key, INBT nbt) {
        compound.put(key, nbt);
    }
}
