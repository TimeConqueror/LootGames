package ru.timeconqueror.lootgames.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.registry.LGBlocks;
import ru.timeconqueror.timecore.api.util.holder.Late;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LGCreativeTabs {
    public static final Late<CreativeModeTab> MAIN = new Late.Insertable<>();

    @SubscribeEvent
    public static void onRegisterCreativeTabs(CreativeModeTabEvent.Register event) {
        Late.Insertable.cast(MAIN).set(
                event.registerCreativeModeTab(LootGames.rl("main"), builder ->
                        builder.title(Component.translatable("itemGroup." + LootGames.MODID))
                                .icon(() -> new ItemStack(LGBlocks.PUZZLE_MASTER))
                                .displayItems((enabledFeatures_, output_, displayOperatorCreativeTab_) -> {
                                    output_.accept(new ItemStack(Items.FEATHER));
                                })
                ));
    }
}
