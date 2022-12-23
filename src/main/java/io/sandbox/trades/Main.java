package io.sandbox.trades;

import io.sandbox.lib.Config;
import io.sandbox.lib.SandboxLogger;
import io.sandbox.trades.configs.SandboxTradesConfig;
import io.sandbox.trades.items.ItemLoader;
import io.sandbox.trades.professions.ProfessionLoader;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
	public static final String modId = "sandbox-trades";
	public static final SandboxLogger LOGGER = new SandboxLogger("SandboxTrades");
	public static SandboxTradesConfig tradesConfig = new Config<SandboxTradesConfig>(SandboxTradesConfig.class, Main.modId).getConfig();

	@Override
	public void onInitialize() {
		ItemLoader.init();
		ProfessionLoader.init();
		LOGGER.info("Initialized");
	}
}
