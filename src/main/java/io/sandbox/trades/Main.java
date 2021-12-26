package io.sandbox.trades;

import io.sandbox.trades.items.ItemLoader;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		ItemLoader.init();
		System.out.println("Initializing Sandbox Biome Trades");
	}
}
