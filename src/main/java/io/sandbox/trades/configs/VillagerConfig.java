package io.sandbox.trades.configs;

public class VillagerConfig {
  public String type;
  public BiomeTradeConfig[] biomes;
  public CostConfig[] levelCost;
  public CostConfig researcherItemSelectionPrice;
  public CostConfig researcherResearchItemPrice;
  public CostConfig researcherTomePrice;

  public BiomeTradeConfig getBiomeTradeConfig(String biome) {
    for (BiomeTradeConfig config : this.biomes) {
      if (config.type.equals(biome)) {
        return config;
      }
    }

    return null;
  }
}
