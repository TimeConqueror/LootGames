package ru.timeconqueror.lootgames.registry;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationAPI;

import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LGAnimations {
    public static Animation microverseManipulatorPlace;
    public static Animation microverseManipulatorIdle;

    @SubscribeEvent
    public static void registerAnimations(FMLCommonSetupEvent event) {
        Map<String, Animation> microverseManipulatorAnimations = AnimationAPI
                .loadAndRegisterAnimations(LootGames.rl("animations/blockentity/microverse_manipulator.json"));
        microverseManipulatorPlace = Objects.requireNonNull(microverseManipulatorAnimations.get("place_box"));
        microverseManipulatorIdle = Objects.requireNonNull(microverseManipulatorAnimations.get("idle"));
    }
}
