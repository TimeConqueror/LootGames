package ru.timeconqueror.lootgames.api.task;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.api.exception.NotExistsException;
import ru.timeconqueror.timecore.api.registry.Initable;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistrable;

import java.util.HashMap;

/**
 * Used for task factory registering. You may extend it and do your stuff in {@link #register()} method.<br>
 * <p>
 * Any your registry that extends it should be annotated with {@link TimeAutoRegistrable}
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 */
public abstract class TaskRegistry implements Initable {
    private static final HashMap<Class<? extends ITask>, ITask.ITaskFactory<?>> TASK_FACTORIES = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends ITask> T createTask(Class<T> taskClass) throws NotExistsException {
        ITask.ITaskFactory<T> factory = (ITask.ITaskFactory<T>) TASK_FACTORIES.get(taskClass);
        if (factory == null) throw new NotExistsException("There is no factory for provided task class.");
        return factory.create();
    }

    public static <T extends ITask> void registerTaskFactory(Class<T> taskClass, ITask.ITaskFactory<T> factory) {
        if (TASK_FACTORIES.put(taskClass, factory) != null) {
            throw new IllegalArgumentException("The factory for task class " + taskClass + " has been already registered.");
        }
    }

    @Override
    public final void onInit(FMLCommonSetupEvent fmlCommonSetupEvent) {
        register();
    }

    /**
     * Method, where you should do all register stuff.
     */
    protected abstract void register();
}
