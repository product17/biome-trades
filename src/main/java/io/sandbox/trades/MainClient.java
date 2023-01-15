package io.sandbox.trades;

import java.util.HashMap;
import java.util.Map;

import io.sandbox.trades.items.ItemLoader;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class MainClient implements ClientModInitializer {
  private static Map<String, Float> modelMap = new HashMap<>() {{
    put(Items.DIAMOND_AXE.toString(), 0.1F);
    put(Items.DIAMOND_BOOTS.toString(), 0.2F);
    put(Items.BOW.toString(), 0.3F);
    put(Items.DIAMOND_CHESTPLATE.toString(), 0.4F);
    put(Items.CROSSBOW.toString(), 0.5F);
    put(Items.ELYTRA.toString(), 0.6F);
    put(Items.FISHING_ROD.toString(), 0.7F);
    put(Items.DIAMOND_HELMET.toString(), 0.8F);
    put(Items.DIAMOND_HOE.toString(), 0.9F);
    put(Items.DIAMOND_LEGGINGS.toString(), 0.11F);
    put(Items.DIAMOND_PICKAXE.toString(), 0.12F);
    put(Items.SHEARS.toString(), 0.13F);
    put(Items.DIAMOND_SHOVEL.toString(), 0.14F);
    put(Items.DIAMOND_SWORD.toString(), 0.15F);
    put(Items.TRIDENT.toString(), 0.16F);
  }};
  
  @Override
  public void onInitializeClient() {
    ModelPredicateProviderRegistry.register(ItemLoader.BLUEPRINT, new Identifier("enchant_item"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int current) -> {
      if (itemStack.hasNbt()) {
        String itemType = itemStack.getNbt().getString("item_type");
        if (modelMap.containsKey(itemType)) {
          return modelMap.get(itemType);
        }
      }

      return 0.0F;
    });
  }
}