package com.zartio.betterendgame;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zartio.betterendgame.data.registry.*;

public class BetterEndgame implements ModInitializer {
	public static final String MOD_ID = "better-endgame";

    public static final Logger LOGGER = LoggerFactory.getLogger("better-endgame");


	@Override
	public void onInitialize() {
		Items.init();
		Blocks.init();
		BlockEntityTypes.init();
		EntityTypes.init();
		StatusEffects.init();
		Potions.init();
		RecipeSerializers.init();
	}
}