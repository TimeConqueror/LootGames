package ru.timeconqueror.lootgames.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.lootgames.client.debug.RoomDebugRenderer;

@Mixin(DebugRenderer.class)
public class DebugRenderMixin {

    @Inject(method = "render", at = @At("TAIL"))
    public void renderCustomDebugFeatures(PoseStack poseStack_, MultiBufferSource.BufferSource bufferSource_, double camX_, double camY_, double camZ_, CallbackInfo ci) {
        RoomDebugRenderer.render(poseStack_, bufferSource_, camX_, camY_, camZ_);
    }
}
