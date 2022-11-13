package io.sandbox.trades.configs;

public class SandboxTradesConfig {
  public LevelCostConfig[] defaultLevelCost;
  public VillagerConfig[] villagerConfigs;

  public VillagerConfig getVillagerConfig(String profession) {
    for (VillagerConfig config : this.villagerConfigs) {
      if (config.type.equals(profession)) {
        return config;
      }
    }

    return null;
  }
}
