package ru.timeconqueror.lootgames.api.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class NBTGamePacket implements IServerGamePacket {
    @Nullable
    private CompoundTag compound;

    /**
     * Only for using in constructor on reception side.
     */
    public NBTGamePacket() {
    }

    public NBTGamePacket(Supplier<CompoundTag> compoundSupplier) {
        this(compoundSupplier.get());
    }

    public NBTGamePacket(@Nullable CompoundTag compound) {
        this.compound = compound;
    }

    @Override
    public void encode(FriendlyByteBuf bufferTo) {
        bufferTo.writeBoolean(compound != null);

        if (compound != null) {
            bufferTo.writeNbt(compound);
        }
    }

    @Override
    public void decode(FriendlyByteBuf bufferFrom) {
        boolean isNotNull = bufferFrom.readBoolean();
        if (isNotNull) {
            compound = bufferFrom.readNbt();
        } else compound = null;
    }

    @Nullable
    public CompoundTag getCompound() {
        return compound;
    }

    public void setCompound(@Nullable CompoundTag compound) {
        this.compound = compound;
    }
}
