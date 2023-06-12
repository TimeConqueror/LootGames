package ru.timeconqueror.lootgames.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.api.registry.SimpleVanillaRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;

public class LGCreativeTabs {
    public static final ResourceKey<CreativeModeTab> MAIN = ResourceKey.create(Registries.CREATIVE_MODE_TAB, LootGames.rl("main"));

    @AutoRegistrable
    private static final SimpleVanillaRegister<CreativeModeTab> REGISTER = new SimpleVanillaRegister<>(Registries.CREATIVE_MODE_TAB, LootGames.MODID);

    @AutoRegistrable.Init
    private static void setup() {
        REGISTER.register(MAIN.location().getPath(), () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + LootGames.MODID))
                .icon(() -> new ItemStack(LGBlocks.PUZZLE_MASTER))
                .build());
    }
}
