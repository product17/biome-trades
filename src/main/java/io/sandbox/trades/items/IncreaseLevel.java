package io.sandbox.trades.items;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;

public class IncreaseLevel extends Item {
  public static final String name = "increase_level";
  public static HashMap<UUID, Boolean> waitingForTick = new HashMap<>();

  public IncreaseLevel(Settings settings) {
    super(settings);
  }

  @Override
  public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
    return false;
  }
  
  @Override
  public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
    if (!player.world.isClient) {
      waitingForTick.put(player.getUuid(), true);
    }

    return false;
  }
}
