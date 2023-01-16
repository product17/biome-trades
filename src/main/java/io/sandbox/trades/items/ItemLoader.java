package io.sandbox.trades.items;

import io.sandbox.trades.Util;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public class ItemLoader {
  public static Blueprint BLUEPRINT = new Blueprint(
    new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(1)
  );

  // Increase_Level does not need to be added to any groups
  public static IncreaseLevel INCREASE_LEVEL = new IncreaseLevel(
		new Item.Settings().rarity(Rarity.UNCOMMON).maxCount(1)
	);

  public static void init() {
    Registry.register(Registries.ITEM, Util.id(Blueprint.name), BLUEPRINT);
    Registry.register(Registries.ITEM, Util.id(IncreaseLevel.name), INCREASE_LEVEL);

    ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(BLUEPRINT));
  }
}
