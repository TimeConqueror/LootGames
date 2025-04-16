package ru.timeconqueror.lootgames.client;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TimeModelRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.client.render.model.InFileLocation;

public class BlockModels {
    @AutoRegistrable
    private static final TimeModelRegister REGISTER = new TimeModelRegister(LootGames.MODID);
    public static final InFileLocation MICROVERSE_MANIPULATOR = REGISTER.register("models/blockentity/microverse_manipulator.json");
    public static final InFileLocation TRANSFERENCE_CRYSTAL = REGISTER.register("models/blockentity/transference_crystal.json");
}
