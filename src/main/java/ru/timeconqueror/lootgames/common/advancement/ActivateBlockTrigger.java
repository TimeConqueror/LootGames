package ru.timeconqueror.lootgames.common.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
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

import static ru.timeconqueror.lootgames.common.advancement.ActivateBlockTrigger.BlockActivatedInfo;
import static ru.timeconqueror.lootgames.common.advancement.ActivateBlockTrigger.Instance;

public class ActivateBlockTrigger extends TimeSimpleTrigger<BlockActivatedInfo, Instance> {
    private static final ResourceLocation ID = LootGames.INSTANCE.createRl("activate_block");

    @Override
    public PerPlayerListenerSet<BlockActivatedInfo, Instance> createListenerSet(PlayerAdvancements advancements) {
        return new PerPlayerListenerSet<>(advancements);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        Block block = null;
        if (json.has("block")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "block"));
            block = ForgeRegistries.BLOCKS.getValue(resourcelocation);
            if (block == null) {
                throw new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
            }
        }

        Map<IProperty<?>, Object> map = null;
        if (json.has("state")) {
            if (block == null) {
                throw new JsonSyntaxException("Can't define block state without a specific block type");
            }

            StateContainer<Block, BlockState> stateContainer = block.getStateContainer();

            for (Map.Entry<String, JsonElement> entry : JSONUtils.getJsonObject(json, "state").entrySet()) {
                IProperty<?> property = stateContainer.getProperty(entry.getKey());
                if (property == null) {
                    throw new JsonSyntaxException("Unknown block state property '" + entry.getKey() + "' for block '" + ForgeRegistries.BLOCKS.getKey(block) + "'");
                }

                String s = JSONUtils.getString(entry.getValue(), entry.getKey());
                Optional<?> optional = property.parseValue(s);
                if (!optional.isPresent()) {
                    throw new JsonSyntaxException("Invalid block state value '" + s + "' for property '" + entry.getKey() + "' on block '" + ForgeRegistries.BLOCKS.getKey(block) + "'");
                }

                if (map == null) {
                    map = new HashMap<>();
                }

                map.put(property, optional.get());
            }
        }

        LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("location"));
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        return new Instance(block, map, locationpredicate, itempredicate);
    }

    public static class BlockActivatedInfo {
        private BlockPos pos;
        private ItemStack item;

        public BlockActivatedInfo(BlockPos pos, ItemStack item) {
            this.pos = pos;
            this.item = item;
        }
    }

    public static class Instance extends TimeSimpleTrigger.TimeCriterionInstance<BlockActivatedInfo> {
        private final Block block;
        private final Map<IProperty<?>, Object> properties;
        private final LocationPredicate location;
        private final ItemPredicate item;

        Instance(@Nullable Block block, @Nullable Map<IProperty<?>, Object> propertiesIn, LocationPredicate locationIn, ItemPredicate itemIn) {
            super(ID);
            this.block = block;
            this.properties = propertiesIn;
            this.location = locationIn;
            this.item = itemIn;
        }

        @Override
        public boolean test(ServerPlayerEntity player, BlockActivatedInfo info) {
            ServerWorld world = player.getServerWorld();

            BlockState state = world.getBlockState(info.pos);
            if (this.block != null && state.getBlock() != this.block) {
                return false;
            } else {
                if (this.properties != null) {
                    for (Map.Entry<IProperty<?>, Object> entry : this.properties.entrySet()) {
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

                for (Map.Entry<IProperty<?>, Object> entry : this.properties.entrySet()) {
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
