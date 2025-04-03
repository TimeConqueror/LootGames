package ru.timeconqueror.lootgames.room;

import com.mojang.serialization.DynamicOps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.lootgames.utils.Utils;
import ru.timeconqueror.timecore.api.util.CodecUtils;
import ru.timeconqueror.timecore.common.capability.property.serializer.CodecPropertySerializer;
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer;

public record EntryPoint(ResourceKey<Level> entryDimension, BlockPos entryPos) {
    public static final Serializer SERIALIZER = new Serializer();

    public static class Serializer implements IPropertySerializer<EntryPoint> {

        @Override
        public EntryPoint deserialize(@NotNull String s, @NotNull CompoundTag compoundTag) {
            var entryCompound = compoundTag.getCompound(s);
            return new EntryPoint(
                    CodecUtils.decodeStrictly(Level.RESOURCE_KEY_CODEC, CodecUtils.NBT_OPS, entryCompound.get("dimension")),
                    BlockPos.of(entryCompound.getLong("pos"))
            );
        }

        @Override
        public void serialize(@NotNull String s, EntryPoint entryPoint, @NotNull CompoundTag compoundTag) {
            var entryCompound = new CompoundTag();
            var dimensionTag = CodecUtils.encodeStrictly(Level.RESOURCE_KEY_CODEC, CodecUtils.NBT_OPS, entryPoint.entryDimension());
            entryCompound.put("dimension", dimensionTag);
            entryCompound.putLong("pos", entryPoint.entryPos().asLong());
        }
    }
}
