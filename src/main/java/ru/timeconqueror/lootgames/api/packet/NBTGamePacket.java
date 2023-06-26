package ru.timeconqueror.lootgames.api.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@NoArgsConstructor
@AllArgsConstructor
public abstract class NBTGamePacket implements IServerGamePacket {
    @Nullable
    @Getter
    @Setter
    private CompoundTag compound;

    public NBTGamePacket(Supplier<CompoundTag> compoundSupplier) {
        this(compoundSupplier.get());
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
}
