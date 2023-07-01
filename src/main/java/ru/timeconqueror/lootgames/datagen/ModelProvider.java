package ru.timeconqueror.lootgames.datagen;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import ru.timeconqueror.timecore.api.client.resource.BlockModel;
import ru.timeconqueror.timecore.api.client.resource.JSONTimeResource;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//FIXME move to TimeCore
public abstract class ModelProvider implements DataProvider {
    private final Logger log = LogManager.getLogger();

    public static final String BLOCK_FOLDER = "block";
    public static final String ITEM_FOLDER = "item";

    protected static final ResourceType TEXTURE = new ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");
    protected static final ResourceType MODEL = new ResourceType(PackType.CLIENT_RESOURCES, ".json", "models");
    protected static final ResourceType MODEL_WITH_EXTENSION = new ResourceType(PackType.CLIENT_RESOURCES, "", "models");

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final PackOutput output;
    protected final String modid;
    protected final String folder;
    @VisibleForTesting
    public final Map<ResourceLocation, JSONTimeResource> generatedModels = new HashMap<>();

    public ModelProvider(PackOutput output, String modid, String folder) {
        this.output = output;
        this.modid = modid;
        this.folder = folder;
    }

    protected abstract void registerAll();

    protected BlockModelLocation addBlockModel(String path, BlockModel resource) {
        Preconditions.checkNotNull(path, "Path must not be null");
        ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path));
        if (generatedModels.containsKey(outputLoc)) {
            log.warn("Block model with path {} already exists, skipping...", outputLoc);
        }

        generatedModels.putIfAbsent(outputLoc, resource);
        return new BlockModelLocation(outputLoc.getNamespace(), outputLoc.getPath());
    }

    protected BlockModelLocation addBlockModel(Block block, BlockModel resource) {
        return addBlockModel(getId(block).toString(), resource);
    }

    public TextureLocation defaultTextureLocation(Block block) {
        ResourceLocation id = getId(block);
        return new TextureLocation(id.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + id.getPath());
    }

    public ResourceLocation getId(Block block) {
        //noinspection deprecation
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private ResourceLocation extendWithFolder(ResourceLocation rl) {
        if (rl.getPath().contains("/")) {
            return rl;
        }
        return new ResourceLocation(rl.getNamespace(), folder + "/" + rl.getPath());
    }

    protected void clear() {
        generatedModels.clear();
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        clear();
        registerAll();
        return generateAll(cache);
    }

    protected CompletableFuture<?> generateAll(CachedOutput cache) {
        CompletableFuture<?>[] futures = new CompletableFuture<?>[this.generatedModels.size()];
        int i = 0;

        for (Map.Entry<ResourceLocation, JSONTimeResource> e : generatedModels.entrySet()) {
            Path target = getPath(e.getKey());
            String json = e.getValue().toJson();

            futures[i++] = DataProvider.saveStable(cache, GSON.fromJson(json, JsonElement.class), target);
        }

        return CompletableFuture.allOf(futures);
    }

    protected Path getPath(ResourceLocation loc) {
        return this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(loc.getNamespace())
                .resolve("models")
                .resolve(loc.getPath() + ".json");
    }
}
