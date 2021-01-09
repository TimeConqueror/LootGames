package ru.timeconqueror.lootgames.client.resource;

import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.api.block.BlockFieldBorder;
import ru.timeconqueror.timecore.api.client.resource.BlockModels;
import ru.timeconqueror.timecore.api.client.resource.BlockStateResource;
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.api.registry.BlockRegister;

import static ru.timeconqueror.timecore.api.client.resource.JSONTimeResource.*;

public class FieldBorderBlockResources {

    public static void fillChain(BlockRegister.BlockRegisterChain<BlockFieldBorder> chain) {
        TextureLocation sideTexture = new TextureLocation(LootGames.MODID, "block/field_border_side");
        TextureLocation cornerTextureTop = new TextureLocation(LootGames.MODID, "block/field_border_corner_top");
        TextureLocation cornerTextureBottom = new TextureLocation(LootGames.MODID, "block/field_border_corner_bottom");

        BlockModelLocation borderModel = new BlockModelLocation(LootGames.MODID, "field_border");
        BlockModelLocation cornerBorderModel = new BlockModelLocation(LootGames.MODID, "field_border_corner");

        chain.genModel(borderModel, BlockModels.cubeAllModel(sideTexture));
        chain.genModel(cornerBorderModel, BlockModels.cubeBottomTopModel(cornerTextureTop, sideTexture, cornerTextureBottom));

        chain.genState(BlockStateResource.fromJson(
                object(null, listOf(
                        object("variants", listOf(
                                object("type=horizontal", listOf(
                                        property("model", borderModel.toString())
                                )),
                                object("type=vertical", listOf(
                                        property("model", borderModel.toString()),
                                        property("y", 90)
                                )),
                                object("type=top_left", listOf(
                                        property("model", cornerBorderModel.toString()),
                                        property("y", 0)
                                )),
                                object("type=top_right", listOf(
                                        property("model", cornerBorderModel.toString()),
                                        property("y", 90)
                                )),
                                object("type=bottom_right", listOf(
                                        property("model", cornerBorderModel.toString()),
                                        property("y", 180)
                                )),
                                object("type=bottom_left", listOf(
                                        property("model", cornerBorderModel.toString()),
                                        property("y", 270)
                                ))
                        ))
                ))
        ));
    }
}
