package ru.timeconqueror.lootgames.utils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import ru.timeconqueror.lootgames.utils.future.BlockPos;
import ru.timeconqueror.timecore.api.util.RandHelper;

import java.util.Random;

public class VanillaStyler {
    public static final Random RAND = new Random();

    public static IIcon getIcon(int x, int y, int z, int side) {
        RAND.setSeed(BlockPos.asLong(x, y, z));
        Block mimic = RandHelper.chance(RAND, 25, Blocks.mossy_cobblestone, Blocks.cobblestone);
        return mimic.getIcon(side, 0);
    }
}
