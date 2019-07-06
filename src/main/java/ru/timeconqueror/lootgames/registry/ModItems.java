package ru.timeconqueror.lootgames.registry;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
//    public static final ItemSonicScrewdriver SONIC_SCREWDRIVER = new ItemSonicScrewdriver();

    public static void register() {
//        registerItem(SONIC_SCREWDRIVER, "sonic_screwdriver");
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderers() {
//        registerItemRender(SONIC_SCREWDRIVER);
    }
}
