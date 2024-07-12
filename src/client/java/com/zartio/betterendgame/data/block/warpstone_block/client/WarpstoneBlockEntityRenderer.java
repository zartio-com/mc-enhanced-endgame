package com.zartio.betterendgame.data.block.warpstone_block.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WindChargeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.block.warpstone_block.WarpstoneBlockEntity;

public class WarpstoneBlockEntityRenderer implements BlockEntityRenderer<WarpstoneBlockEntity> {
    private static final Identifier TEXTURE = Identifier.of(BetterEndgame.MOD_ID, "textures/models/uncharged_black_hole.png");

    private final Random random = Random.create();
    private final WindChargeEntityModel modelPart;

    public WarpstoneBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        modelPart = new WindChargeEntityModel(ctx.getLayerModelPart(EntityModelLayers.WIND_CHARGE));
    }

    @Override
    public void render(
            WarpstoneBlockEntity entity,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        if (entity.getWorld() == null) {
            return;
        }

        matrices.push();

        double yOffset = Math.sin((entity.getWorld().getTime() + tickDelta) / 10.0) / 10.0;

        if (entity.isCharged()) {
            if (entity.isDestinationRandom()) {
                matrices.translate(
                        0.5 + random.nextGaussian() * 0.08,
                        1.75 + yOffset + random.nextGaussian() * 0.08,
                        0.5 + random.nextGaussian() * 0.08
                );
            } else {
                matrices.translate(0.5, 1.75 + yOffset, 0.5);
            }

            float scale = 1.0f + (3.0f * (entity.getCharge() / 20.0f));
            matrices.scale(scale, scale, scale);
        } else {
            if (entity.isDestinationRandom()) {
                matrices.translate(
                        0.5 + random.nextGaussian() * 0.02,
                        1.5 + yOffset + random.nextGaussian() * 0.02,
                        0.5 + random.nextGaussian() * 0.02);
            } else {
                matrices.translate(
                        0.5,
                        1.5 + yOffset,
                        0.5);
            }
            matrices.scale(0.5f, 0.5f, 0.5f);
        }

        double xOffset = (entity.getWorld().getTime() + tickDelta) / 20.0F % 1;

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getBreezeWind(TEXTURE, (float)xOffset, 0.0F));
        modelPart.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
