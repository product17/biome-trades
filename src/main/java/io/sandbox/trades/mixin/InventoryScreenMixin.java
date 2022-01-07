package io.sandbox.trades.mixin;

import java.util.UUID;

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
    // Leave if we're a client.
    if (this.player.world.isClient) { return; }

    // Leave if we have nothing in the keyset for IncreaseLevel
    if (IncreaseLevel.waitingForTick.size() == 0) { return; }

    // Store the player's UUID.
    UUID uuid = this.player.getUuid();

    // Check if the player is even in the UUID set.
    if (!IncreaseLevel.waitingForTick.containsKey(uuid)) { return; }

    // Check if the player's UUID is set to FALSE in the key set.
    if (!IncreaseLevel.waitingForTick.get(uuid)) { return; }
  
    // OK, now we can handle our logic.
    ItemStack itemStack = this.player.currentScreenHandler.getCursorStack();

    // It should only be an IncreaseLevel item... but double checking
    if (itemStack.getItem() instanceof IncreaseLevel) {
      itemStack.setCount(0);
    }

    // just always clear the player to prevent leaving this open
    IncreaseLevel.waitingForTick.put(uuid, false);
	}
}
