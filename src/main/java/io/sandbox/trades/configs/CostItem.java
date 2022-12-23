package io.sandbox.trades.configs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CostItem {
  public String costProcessor;
  public int count = 1;
  public String item;
  public String itemTierTwo;


  public static ItemStack getItemStack(CostItem costItem) {
    if (costItem == null) {
      return null;
    }

    Item checkedItem = Registry.ITEM.get(new Identifier(costItem.item));
    if (checkedItem.equals(Items.AIR)) {
      return null;
    }

    return new ItemStack(
      checkedItem,
      costItem.count
    );
  }

  public static ItemStack getItemStackTierTwo(CostItem costItem) {
    if (costItem == null || costItem.itemTierTwo == null) {
      return null;
    }

    Item checkedItem = Registry.ITEM.get(new Identifier(costItem.itemTierTwo));
    if (checkedItem.equals(Items.AIR)) {
      return null;
    }

    return new ItemStack(
      checkedItem,
      costItem.count
    );
  }
}
