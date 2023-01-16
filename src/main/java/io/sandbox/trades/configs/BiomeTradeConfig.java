package io.sandbox.trades.configs;

import java.util.stream.Stream;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class BiomeTradeConfig {
  public String type;
  public String[] enchantments;

  public Enchantment[] getEnchantments() {
    return Stream.of(this.enchantments).map(
      enchant -> Registries.ENCHANTMENT.get(new Identifier(enchant)))
      .toArray(Enchantment[]::new);
  }
}
