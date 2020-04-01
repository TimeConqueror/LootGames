package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class NBTGamePacket implements IServerGamePacket {
    @Nullable
    private CompoundNBT compound;

    /**
     * Only for using in constructor on reception side.
     */
    public NBTGamePacket() {
    }

    public NBTGamePacket(Supplier<CompoundNBT> compoundSupplier) {
        this(compoundSupplier.get());
    }

    public NBTGamePacket(@Nullable CompoundNBT compound) {
        this.compound = compound;
    }

    @Override
    public void encode(PacketBuffer bufferTo) {
        bufferTo.writeBoolean(compound != null);

        if (compound != null) {
            bufferTo.writeCompoundTag(compound);
        }
    }

    @Override
    public void decode(PacketBuffer bufferFrom) {
        boolean isNotNull = bufferFrom.readBoolean();
        if (isNotNull) {
            compound = bufferFrom.readCompoundTag();
        } else compound = null;
    }

    @Nullable
    public CompoundNBT getCompound() {
        return compound;
    }

    public void setCompound(@Nullable CompoundNBT compound) {
        this.compound = compound;
    }
}
