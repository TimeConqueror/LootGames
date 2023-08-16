package ru.timeconqueror.lootgames.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.timeconqueror.lootgames.room.RoomUtils;
import ru.timeconqueror.lootgames.room.client.ClientRoom;

@Mixin(Frustum.class)
public class FrustrumMixin {
    @Inject(method = "isVisible", at = @At(value = "HEAD"), cancellable = true)
    public void disableRenderInOtherRooms(AABB box_, CallbackInfoReturnable<Boolean> cir) {
        if (!RoomUtils.inRoomWorld(Minecraft.getInstance().level)) {
            return;
        }

        ClientRoom room = ClientRoom.getInstance();
        if (room != null && !room.getRoomBox().intersects(box_)) {
            cir.setReturnValue(false);
        }
    }
}
