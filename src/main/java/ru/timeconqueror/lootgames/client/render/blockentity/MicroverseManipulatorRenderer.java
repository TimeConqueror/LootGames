package ru.timeconqueror.lootgames.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;
import ru.timeconqueror.lootgames.LootGames;
import ru.timeconqueror.lootgames.client.BlockModels;
import ru.timeconqueror.lootgames.common.block.entity.MicroverseManipulatorBlockEntity;
import ru.timeconqueror.lootgames.registry.LGGames;
import ru.timeconqueror.lootgames.registry.custom.GameInfo;
import ru.timeconqueror.timecore.animation.renderer.AnimatedBlockEntityRenderer;
import ru.timeconqueror.timecore.animation.renderer.ModelConfiguration;
import ru.timeconqueror.timecore.api.util.VecUtils;
import ru.timeconqueror.timecore.client.render.model.TimeModel;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;
import ru.timeconqueror.timecore.client.render.processor.PropertyBasedRotationProcessor;
import ru.timeconqueror.timecore.client.render.processor.RotationProcessor;

import java.util.Map;

@Log4j2
public class MicroverseManipulatorRenderer extends AnimatedBlockEntityRenderer<MicroverseManipulatorBlockEntity> {
    public static final ResourceLocation BASE = LootGames.tl("blockentity/microverse_manipulator_base.png").fullLocation();
    public static final Map<ResourceLocation, ResourceLocation> GAME_TEXTURES = Map.of(
            LGGames.MINESWEEPER.getId(), LootGames.tl("blockentity/microverse_manipulator_activated_blue.png").fullLocation(),
            LGGames.GAME_OF_LIGHT.getId(), LootGames.tl("blockentity/microverse_manipulator_activated_blue.png").fullLocation() // TODO fix texture
    );
    public static final RenderType HOLE_LAYER = RenderType.entityTranslucent(LootGames.tl("blockentity/microverse_manipulator_hole.png").fullLocation());

    public MicroverseManipulatorRenderer(BlockEntityRendererProvider.Context context) {
        super(new TimeModel(ModelConfiguration.builder(BlockModels.MICROVERSE_MANIPULATOR).build()));

        getPuppeteer().addModelProcessor(RotationProcessor.byDegrees(Direction.Axis.Y, 180));
        getPuppeteer().addModelProcessor(PropertyBasedRotationProcessor.horizontalDirectionalBased());
        getPuppeteer().addModelProcessor((entity, model, partialTick) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            float yaw = getLookAngle(entity, player);

            TimeModelPart blackHole = model.getPart("BlackHole");
            blackHole.getRotation().y = yaw;
        });
    }

    private static float getLookAngle(MicroverseManipulatorBlockEntity entity, Entity lookTarget) {
        Vector3f eyePos = VecUtils.vec3f(lookTarget.getX(), lookTarget.getY(), lookTarget.getZ());

        BlockPos entityPos = entity.getBlockPos();
        Vector3f holePos = VecUtils.vec3f(
                entityPos.getX() + 0.5,
                entityPos.getY() + 1 + 11 / 16F,
                entityPos.getZ() + 0.5
        );

        double dx = eyePos.x() - holePos.x();
        double dz = eyePos.z() - holePos.z();
        return (float) Mth.atan2(dx, dz);
    }

    @Override
    protected void postRender(MicroverseManipulatorBlockEntity object, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packetLight, int packedOverlay, int rgbaColor) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        GameInfo gameInfo = object.getGameInfo();
        if (gameInfo == null) {
            return;
        }

        if (!object.isBaseEnlighten()) {
            return;
        }

        ResourceLocation location = GAME_TEXTURES.get(gameInfo.toKey());
        RenderType gameLayer = RenderType.entityTranslucent(location);
        this.model.renderToBuffer(poseStack, buffer.getBuffer(gameLayer), packetLight, packedOverlay, rgbaColor);
        this.model.renderToBuffer(poseStack, buffer.getBuffer(HOLE_LAYER), packetLight, packedOverlay, rgbaColor);
    }

    @Override
    protected ResourceLocation getTexture(MicroverseManipulatorBlockEntity tileEntityIn) {
        return BASE;
    }
}
