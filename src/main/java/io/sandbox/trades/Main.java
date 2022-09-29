package io.sandbox.trades;

import io.sandbox.lib.SandboxLogger;
import io.sandbox.trades.items.ItemLoader;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
	private static final SandboxLogger LOGGER = new SandboxLogger("SandboxTrades");

	@Override
	public void onInitialize() {
		ItemLoader.init();
		LOGGER.info("Initialized");
	}
}
