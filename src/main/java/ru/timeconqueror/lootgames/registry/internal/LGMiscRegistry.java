package ru.timeconqueror.lootgames.registry.internal;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.lootgames.api.task.TaskCreateExplosion;
import ru.timeconqueror.lootgames.api.task.TaskRegistry;
import ru.timeconqueror.timecore.api.registry.Initable;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

@TimeAutoRegistrable
public class LGMiscRegistry implements Initable {
    @Override
    public void onInit(FMLCommonSetupEvent fmlCommonSetupEvent) {
        TaskRegistry.registerTaskFactory(TaskCreateExplosion.class, TaskCreateExplosion::new);
    }
}
