package ru.timeconqueror.lootgames.api.minigame;

import net.minecraft.nbt.CompoundTag;
import ru.timeconqueror.lootgames.utils.EventBus;
import ru.timeconqueror.timecore.api.common.tile.SerializationType;

public abstract class Stage {

    /**
     * Allows to register custom event handlers to the provided bus.
     * Called before {@link #preInit()}.
     */
    public void subscribeToEvents(EventBus bus) {

    }

    /**
     * Server side only.
     * Called right after the game switched to this stage {@link LootGame#setupInitialStage()} or {@link LootGame#switchStage(Stage)},
     * but BEFORE it will be synced.
     * <p>
     * It is not called upon deserializing.
     */
    public void preInit() {

    }

    /**
     * Server side only.
     * Called right after the game switched to this stage {@link LootGame#setupInitialStage()} or {@link LootGame#switchStage(Stage)},
     * and AFTER it will be synced.
     * <p>
     * It is not called upon deserializing.
     */
    public void postInit() {

    }

    /**
     * Called for both logical sides when the game was switched to this stage:
     * <ol>
     *     <li>by changing stage via {@link LootGame#setupInitialStage()} or {@link LootGame#switchStage(Stage)}</li>
     *     <li>by deserializing and syncing</li>
     * </ol>
     * <p>
     * Will allways be called after {@link #preInit()} & {@link #postInit()}
     */
    protected void onStart(boolean clientSide) {

    }

    /**
     * Called on every tick for both logical sides.
     */
    protected void onTick() {

    }

    /**
     * Called for both logical sides when the game was switched from this stage to another one.
     */
    protected void onEnd() {

    }

    /**
     * Serializes stage according to the provided serialization type.
     * If you have some sensitive data you can check here for type before adding it to nbt or not.
     *
     * @param serializationType defines for which purpose stage is serializing.
     */
    public CompoundTag serialize(SerializationType serializationType) {
        return new CompoundTag();
    }

    public abstract String getID();

    @Override
    public String toString() {
        return getID();
    }
}