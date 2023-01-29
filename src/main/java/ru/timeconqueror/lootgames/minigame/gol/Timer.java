package ru.timeconqueror.lootgames.minigame.gol;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.Tag;
import ru.timeconqueror.timecore.api.util.CodecUtils;
import ru.timeconqueror.timecore.api.util.Requirements;

public class Timer {
    private static final Codec<Timer> CODEC = Codec.INT.xmap(Timer::new, Timer::ticksLeft);
    private int ticks;

    public Timer(int ticks) {
        Requirements.greaterOrEquals(ticks, 0);
        this.ticks = ticks;
    }

    public void update() {
        if (!ended()) {
            ticks--;
        }
    }

    public void reset(int ticks) {
        Requirements.greaterOrEquals(ticks, 0);
        this.ticks = ticks;
    }

    public int ticksLeft() {
        return ticks;
    }

    public boolean ended() {
        return ticks == 0;
    }

    public static Timer fromNBT(Tag nbt) {
        return CodecUtils.decodeStrictly(CODEC, CodecUtils.NBT_OPS, nbt);
    }

    public static Tag toNBT(Timer timer) {
        return CodecUtils.encodeStrictly(CODEC, CodecUtils.NBT_OPS, timer);
    }
}
