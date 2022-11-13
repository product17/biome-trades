package io.sandbox.trades.configs;

import java.util.stream.Stream;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BiomeTradeConfig {
  public String type;
  public String[] enchantments;

  public Enchantment[] getEnchantments() {
    return Stream.of(this.enchantments).map(
      enchant -> Registry.ENCHANTMENT.get(new Identifier(enchant)))
      .toArray(Enchantment[]::new);
  }
}
