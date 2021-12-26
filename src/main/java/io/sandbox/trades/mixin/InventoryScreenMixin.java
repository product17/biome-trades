package io.sandbox.trades.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.sandbox.trades.items.IncreaseLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

@Mixin(PlayerInventory.class)
public class InventoryScreenMixin {
  @Shadow
	@Final
	private PlayerEntity player;

	@Inject(at = @At("TAIL"), method = "updateItems")
	private void updateItems(CallbackInfo info) {
    if (!player.world.isClient && IncreaseLevel.waitingForTick.size() > 0 && IncreaseLevel.waitingForTick.get(player.getUuid())) {
      ItemStack itemStack = this.player.currentScreenHandler.getCursorStack();

      if (itemStack.getItem() instanceof IncreaseLevel) {
        System.out.println("Items to remove slot: " + itemStack.getItem());
        itemStack.setCount(0);
      }

      IncreaseLevel.waitingForTick.put(player.getUuid(), false);
    }
	}
}
