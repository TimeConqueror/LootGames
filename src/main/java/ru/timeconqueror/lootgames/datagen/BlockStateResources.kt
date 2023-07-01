package ru.timeconqueror.lootgames.datagen

import ru.timeconqueror.timecore.api.client.resource.BlockStateResource
import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation
import ru.timeconqueror.timecore.api.util.json.json

//FIXME move to TimeCore
object BlockStateResources {
    @JvmStatic
    fun singleVariantWithSingleModel(model: BlockModelLocation): BlockStateResource {
        return BlockStateResource.fromJson(json {
            "variants" {
                "" {
                    "model" set model
                }
            }
        })
    }
}
