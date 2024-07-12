package com.zartio.betterendgame;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import com.zartio.betterendgame.BetterEndgame;
import com.zartio.betterendgame.data.registry.BlockEntityTypes;
import com.zartio.betterendgame.data.block.warpstone_block.client.WarpstoneBlockEntityRenderer;
import com.zartio.betterendgame.data.registry.client.EntityRenderer;

public class BetterEndgameClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_WARPSTONE_INACTIVE_LAYER = new EntityModelLayer(Identifier.of(BetterEndgame.MOD_ID, "block_entity/warpstone_inactive"), "main");

	@Override
	public void onInitializeClient() {
		EntityRenderer.init();
//		BlockEntityRendererFactories.register(BlocksRegistry.WARPSTONE_BLOCK_ENTITY, WarpstoneBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypes.WARPSTONE_BLOCK_ENTITY, WarpstoneBlockEntityRenderer::new);
	}
}