package io.sandbox.trades.professions;

import com.google.common.collect.ImmutableSet;

import io.sandbox.trades.Main;
import io.sandbox.trades.Util;
import io.sandbox.trades.configs.CostConfig;
import io.sandbox.trades.configs.CostItem;
import io.sandbox.trades.configs.VillagerConfig;
import io.sandbox.trades.items.Blueprint;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class ProfessionLoader {
  public static final PointOfInterestType RESEARCHER_POI = registerPoi("researcher_poi", Blocks.ENCHANTING_TABLE);
  public static final VillagerProfession RESEARCHER = registerProfession("researcher",
      RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, Util.id("researcher_poi")));
  
  public static final Item[] blueprintItems = {
    Items.DIAMOND_HELMET,
    Items.DIAMOND_CHESTPLATE,
    Items.DIAMOND_LEGGINGS,
    Items.DIAMOND_BOOTS,
    Items.DIAMOND_SWORD,
    Items.DIAMOND_AXE,
    Items.DIAMOND_PICKAXE,
    Items.DIAMOND_SHOVEL,
    Items.DIAMOND_HOE,
    Items.BOW,
    Items.CROSSBOW,
    Items.ELYTRA,
    Items.TRIDENT,
    Items.SHEARS,
    Items.FISHING_ROD,
  };

  public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
    return Registry.register(Registry.VILLAGER_PROFESSION, Util.id(name),
        VillagerProfessionBuilder.create().id(Util.id(name)).workstation(type)
            .workSound(SoundEvents.ENTITY_VILLAGER_WORK_ARMORER).build());
  }

  public static PointOfInterestType registerPoi(String name, Block block) {
    return PointOfInterestHelper.register(
        Util.id(name),
        1,
        16,
        ImmutableSet.copyOf(block.getStateManager().getStates()));
  }

  public static void init() {
    ProfessionLoader.registerTrades();
  }

  public static void registerTrades() {
    
    TradeOfferHelper.registerVillagerOffers(RESEARCHER, 1,
    factories -> {
        VillagerConfig villagerConfig = Main.tradesConfig.getVillagerConfig(RESEARCHER.toString());
        CostConfig cost = null;
        if (villagerConfig != null) {
          cost = villagerConfig.researcherItemSelectionPrice;
        }

        if (cost != null) {
          for (Item blueprintItem : blueprintItems) {
            ItemStack priceItemOne = CostItem.getItemStack(cost.itemOne);
            ItemStack priceItemTwo = CostItem.getItemStack(cost.itemTwo);
            if (cost.itemTwo != null) {
              factories.add(((entity, random) -> new TradeOffer(
                priceItemOne,
                priceItemTwo,
                Blueprint.createBlueprint(blueprintItem),
                1, 0, 0.0f)));
            } else {
              factories.add(((entity, random) -> new TradeOffer(
                priceItemOne,
                Blueprint.createBlueprint(blueprintItem),
                1, 0, 0.0f)));
            }
          }

          return;
        }

        // Fallback is just emeralds
        for (Item blueprintItem : blueprintItems) {
          factories.add(((entity, random) -> new TradeOffer(
            new ItemStack(Items.EMERALD, 15),
            Blueprint.createBlueprint(blueprintItem),
            1, 0, 0.0f)));
        }
      }
    );

    // This is filler to prevent empty lists
    // This offer will be ignored, but is need to prevent null pointers
    for (int i = 0; i < 4; i++) {
      TradeOfferHelper.registerVillagerOffers(RESEARCHER, i + 2,
        factories -> {
          factories.add(((entity, random) -> new TradeOffer(
            new ItemStack(Items.EMERALD, 3),
            Blueprint.createBlueprint(Items.ACACIA_BOAT),
            1, 0, 0.0f)));
        }
      );
    }
  }
}
