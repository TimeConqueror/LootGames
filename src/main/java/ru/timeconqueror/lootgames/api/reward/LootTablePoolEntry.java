package ru.timeconqueror.lootgames.api.reward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@Builder
@Getter
@AllArgsConstructor
public class LootTablePoolEntry {
    private final int weight;
    private final ResourceLocation key;
}