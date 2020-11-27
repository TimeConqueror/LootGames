/*
package ru.timeconqueror.lootgames.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.api.common.advancement.criteria.TimeSimpleTrigger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.timeconqueror.lootgames.common.advancement.ActivateBlockTrigger.ExtraInfo;
import static ru.timeconqueror.lootgames.common.advancement.ActivateBlockTrigger.Instance;

public class ActivateBlockTrigger extends TimeSimpleTrigger<ExtraInfo, Instance> {
    private static final ResourceLocation ID = LootGames.rl("activate_block");

    @Override
    public PerPlayerListenerSet<ExtraInfo, Instance> createListenerSet(PlayerAdvancements advancements) {
        return new PerPlayerListenerSet<>(advancements);
    }

    @Override
    public Instance createInstance(JsonObject jsonObject, ConditionArrayParser conditionArrayParser) {
        return null;
    }

    public EnterBlockTrigger.Instance createInstance(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        Block block = deserializeBlock(json);
        StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.fromJson(json.get("state"));
        if (block != null) {
            statepropertiespredicate.checkState(block.getStateDefinition(), (propertyIn) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + propertyIn);
            });
        }

        return new EnterBlockTrigger.Instance(entityPredicate, block, statepropertiespredicate);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        Block block = null;
        if (json.has("block")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(json, "block"));
            block = ForgeRegistries.BLOCKS.getValue(resourcelocation);
            if (block == null) {
                throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
            }
        }

        Map<Property<?>, Object> map = null;
        if (json.has("state")) {
            if (block == null) {
                throw new JsonSyntaxException("Can't define block state without a specific block type");
            }

            StateContainer<Block, BlockState> stateContainer = block.getStateContainer();

            for (Map.Entry<String, JsonElement> entry : JSONUtils.getAsJsonObject(json, "state").entrySet()) {
                Property<?> property = stateContainer.getProperty(entry.getKey());
                if (property == null) {
                    throw new JsonSyntaxException("Unknown block state property '" + entry.getKey() + "' for block '" + ForgeRegistries.BLOCKS.getKey(block) + "'");
                }

                String s = JSONUtils.getAsString(entry.getValue(), entry.getKey());
                Optional<?> optional = property.getValue(s);
                if (!optional.isPresent()) {
                    throw new JsonSyntaxException("Invalid block state value '" + s + "' for property '" + entry.getKey() + "' on block '" + ForgeRegistries.BLOCKS.getKey(block) + "'");
                }

                if (map == null) {
                    map = new HashMap<>();
                }

                map.put(property, optional.get());
            }
        }

        LocationPredicate locationpredicate = LocationPredicate.fromJson(json.get("location"));
        ItemPredicate itempredicate = ItemPredicate.fromJson(json.get("item"));
        return new Instance(block, map, locationpredicate, itempredicate);
    }

    public static class ExtraInfo {
        private BlockPos pos;
        private ItemStack item;

        public ExtraInfo(BlockPos pos, ItemStack item) {
            this.pos = pos;
            this.item = item;
        }
    }

    public static class Instance extends TimeSimpleTrigger.TimeCriterionInstance<ExtraInfo> {
        private final Block block;
        private final Map<Property<?>, Object> properties;
        private final LocationPredicate location;
        private final ItemPredicate item;

        Instance(@Nullable Block block, @Nullable Map<Property<?>, Object> propertiesIn, LocationPredicate locationIn, ItemPredicate itemIn) {
            super(ID);
            this.block = block;
            this.properties = propertiesIn;
            this.location = locationIn;
            this.item = itemIn;
        }

        @Override
        public boolean test(ServerPlayerEntity player, ExtraInfo info) {
            ServerWorld world = player.getServerWorld();

            BlockState state = world.getBlockState(info.pos);
            if (this.block != null && state.getBlock() != this.block) {
                return false;
            } else {
                if (this.properties != null) {
                    for (Map.Entry<Property<?>, Object> entry : this.properties.entrySet()) {
                        if (state.get(entry.getKey()) != entry.getValue()) {
                            return false;
                        }
                    }
                }

                if (!this.location.test(world, (float) info.pos.getX(), (float) info.pos.getY(), (float) info.pos.getZ())) {
                    return false;
                } else {
                    return this.item.test(info.item);
                }
            }
        }

        @Override
        public JsonElement serialize() {
            JsonObject root = new JsonObject();
            if (this.block != null) {
                root.addProperty("block", ForgeRegistries.BLOCKS.getKey(this.block).toString());
            }

            if (this.properties != null) {
                JsonObject jsonProperties = new JsonObject();

                for (Map.Entry<Property<?>, Object> entry : this.properties.entrySet()) {
                    jsonProperties.addProperty(entry.getKey().getName(), Util.getValueName(entry.getKey(), entry.getValue()));
                }

                root.add("state", jsonProperties);
            }

            root.add("location", this.location.serialize());
            root.add("item", this.item.serialize());
            return root;
        }
    }
}
*/
