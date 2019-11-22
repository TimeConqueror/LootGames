package ru.timeconqueror.lootgames.achievement.criteria;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import ru.timeconqueror.lootgames.LootGames;

import javax.annotation.Nullable;
import java.util.*;

public class BlockActivatedTrigger implements ICriterionTrigger<BlockActivatedTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(LootGames.MOD_ID, "activate_block");
    private final Map<PlayerAdvancements, BlockActivatedTrigger.Listeners> listeners = new HashMap<>();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<Instance> listener) {
        Listeners listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners == null) {
            listeners = new Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, listeners);
        }

        listeners.add(listener);
    }

    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<Instance> listener) {
        Listeners listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners != null) {
            listeners.remove(listener);

            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     */
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        Block block = null;

        if (json.has("block")) {
            ResourceLocation rl = new ResourceLocation(JsonUtils.getString(json, "block"));

            if (!Block.REGISTRY.containsKey(rl)) {
                throw new JsonSyntaxException("Unknown block type '" + rl + "'");
            }

            block = Block.REGISTRY.getObject(rl);
        }

        Map<IProperty<?>, Object> map = null;

        if (json.has("state")) {
            if (block == null) {
                throw new JsonSyntaxException("Can't define block state without a specific block type");
            }

            BlockStateContainer blockstatecontainer = block.getBlockState();

            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "state").entrySet()) {
                IProperty<?> iproperty = blockstatecontainer.getProperty(entry.getKey());

                if (iproperty == null) {
                    throw new JsonSyntaxException("Unknown block state property '" + entry.getKey() + "' for block '" + Block.REGISTRY.getNameForObject(block) + "'");
                }

                String s = JsonUtils.getString(entry.getValue(), entry.getKey());
                Optional<?> optional = iproperty.parseValue(s);

                if (!optional.isPresent()) {
                    throw new JsonSyntaxException("Invalid block state value '" + s + "' for property '" + entry.getKey() + "' on block '" + Block.REGISTRY.getNameForObject(block) + "'");
                }

                if (map == null) {
                    map = Maps.newHashMap();
                }

                map.put(iproperty, optional.get());
            }
        }

        LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("location"));
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        return new Instance(block, map, locationpredicate, itempredicate);
    }

    public void trigger(EntityPlayerMP player, BlockPos pos, ItemStack item) {
        IBlockState iblockstate = player.world.getBlockState(pos);
        Listeners listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger(iblockstate, pos, player.getServerWorld(), item);
        }
    }

    public static class Instance extends AbstractCriterionInstance {
        private final Block block;
        private final Map<IProperty<?>, Object> properties;
        private final LocationPredicate location;
        private final ItemPredicate item;

        Instance(@Nullable Block block, @Nullable Map<IProperty<?>, Object> propertiesIn, LocationPredicate locationIn, ItemPredicate itemIn) {
            super(BlockActivatedTrigger.ID);
            this.block = block;
            this.properties = propertiesIn;
            this.location = locationIn;
            this.item = itemIn;
        }

        boolean test(IBlockState state, BlockPos pos, WorldServer world, ItemStack item) {
            if (this.block != null && state.getBlock() != this.block) {
                return false;
            } else {
                if (this.properties != null) {
                    for (Map.Entry<IProperty<?>, Object> entry : this.properties.entrySet()) {
                        if (state.getValue(entry.getKey()) != entry.getValue()) {
                            return false;
                        }
                    }
                }

                if (!this.location.test(world, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ())) {
                    return false;
                } else {
                    return this.item.test(item);
                }
            }
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<Instance>> listeners = new HashSet<>();

        Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<Instance> listener) {
            this.listeners.add(listener);
        }

        void remove(ICriterionTrigger.Listener<Instance> listener) {
            this.listeners.remove(listener);
        }

        void trigger(IBlockState state, BlockPos pos, WorldServer world, ItemStack item) {
            List<ICriterionTrigger.Listener<Instance>> list = null;

            for (ICriterionTrigger.Listener<Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(state, pos, world, item)) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
