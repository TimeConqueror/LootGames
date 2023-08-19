package ru.timeconqueror.lootgames.api.room;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.common.capability.property.serializer.IPropertySerializer;

public enum GameProgress {
    NOT_STARTED, STARTED, FINISHED;

    public static final Serializer SERIALIZER = new Serializer();
    public static final GameProgress[] VALUES = GameProgress.values();

    public static class Serializer implements IPropertySerializer<GameProgress> {

        @Override
        public GameProgress deserialize(@NotNull String name, @NotNull CompoundTag compoundTag) {
            return GameProgress.valueOf(compoundTag.getString(name));
        }

        @Override
        public void serialize(@NotNull String name, GameProgress gameProgress, @NotNull CompoundTag compoundTag) {
            compoundTag.putString(name, gameProgress.name());
        }
    }
}
