package ru.timeconqueror.lootgames.api.room;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer;

@Getter
@RequiredArgsConstructor
public enum RoomProgress {
    NOT_GENERATED(0), GENERATED(1), STARTED(2), FINISHED(3);

    private final int index;

    public static final Serializer SERIALIZER = new Serializer();
    public static final RoomProgress[] VALUES = RoomProgress.values();

    public boolean isLess(RoomProgress other) {
        return this.index < other.index;
    }

    public static class Serializer implements IPropertySerializer<RoomProgress> {

        @Override
        public RoomProgress deserialize(@NotNull String name, @NotNull CompoundTag compoundTag) {
             return RoomProgress.valueOf(compoundTag.getString(name));
        }

        @Override
        public void serialize(@NotNull String name, RoomProgress gameProgress, @NotNull CompoundTag compoundTag) {
            compoundTag.putString(name, gameProgress.name());
        }
    }
}
