package io.sandbox.trades.items;

import io.sandbox.trades.Util;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ItemLoader {
  public static Blueprint BLUEPRINT = new Blueprint(
    new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON).maxCount(1)
  );
  public static IncreaseLevel INCREASE_LEVEL = new IncreaseLevel(
		new Item.Settings().group(ItemGroup.MISC).rarity(Rarity.UNCOMMON).maxCount(1)
	);

  public static void init() {
    Registry.register(Registry.ITEM, Util.id(Blueprint.name), BLUEPRINT);
    Registry.register(Registry.ITEM, Util.id(IncreaseLevel.name), INCREASE_LEVEL);
  }
}
