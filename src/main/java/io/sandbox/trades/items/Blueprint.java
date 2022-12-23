package io.sandbox.trades.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import io.sandbox.equipment.Main;
import io.sandbox.trades.configs.CostConfig;
import io.sandbox.trades.configs.CostItem;
import io.sandbox.trades.configs.VillagerConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

public class Blueprint extends Item {
  public static final String name = "blueprint";
  public static final String itemType = "item_type";
  public static final Random random = Random.create();

  public Blueprint(Settings settings) {
    super(settings);
  }

  public static ItemStack createBlueprint(Item item) {
    ItemStack stack = new ItemStack(ItemLoader.BLUEPRINT);
    NbtCompound nbt = stack.getOrCreateNbt();
    nbt.putString(Blueprint.itemType, item.toString());
    stack.setNbt(nbt);
    return stack;
  }

  public static List<Enchantment> getEnchantmentList(ItemStack itemStack, Map<Enchantment, Integer> mainBookEnchants) {
    List<Enchantment> enchantments = new ArrayList<>();
    Iterator<Enchantment> enchanIterator = Registry.ENCHANTMENT.iterator();
    Collection<Enchantment> existingEnchants = mainBookEnchants.keySet();
    while (enchanIterator.hasNext()) {
      Enchantment enchant = enchanIterator.next();
      if (
        enchant.isAcceptableItem(itemStack) &&
        !enchant.isCursed() &&
        enchant.isAvailableForEnchantedBookOffer() &&
        (!mainBookEnchants.containsKey(enchant) || mainBookEnchants.get(enchant) < enchant.getMaxLevel()) &&
        isEnchantCompatible(existingEnchants, enchant)
      ) {
        enchantments.add(enchant);
      }
    }

    return enchantments;
  }

  private static Boolean isEnchantCompatible(Collection<Enchantment> existingEnchants, Enchantment enchantToAdd) {
    Iterator var2 = existingEnchants.iterator();

    Enchantment enchantment;
    do {
        if (!var2.hasNext()) {
          return true;
        }

        enchantment = (Enchantment)var2.next();
    } while(canCombineWith(enchantment, enchantToAdd));

    return false;
  }

  private static Boolean canCombineWith(Enchantment enchantment, Enchantment enchantToAdd) {
    if (enchantment.getClass().equals(enchantToAdd.getClass())) {
      if (enchantToAdd instanceof DamageEnchantment) {
        if (((DamageEnchantment)enchantment).typeIndex == ((DamageEnchantment)enchantToAdd).typeIndex) {
          return true;
        }
      } else if (enchantToAdd instanceof ProtectionEnchantment) {
        if (((ProtectionEnchantment)enchantment).protectionType == ((ProtectionEnchantment)enchantToAdd).protectionType) {
          return true;
        }
      } else {
        return true;
      }
    }

    return enchantment.canCombine(enchantToAdd);
  }

  public static void addEnchantedBookList(TradeOfferList offerList, ItemStack itemStack, ItemStack mainEnchantBook, int merchantLevel, VillagerConfig config) {
    Map<Enchantment, Integer> mainBookEnchants = EnchantmentHelper.get(mainEnchantBook);
    List<Enchantment> enchants = Blueprint.getEnchantmentList(itemStack, mainBookEnchants);
    for(Enchantment enchantment : enchants) {
      TradeOffer trade = buildEnchantedBookOffer(enchantment, merchantLevel, mainBookEnchants, config);
      if (trade != null) {
        offerList.add(trade);
      }
    }
  }

  public static TradeOffer buildEnchantedBookOffer(Enchantment enchant, int merchantLevel, Map<Enchantment, Integer> mainBookEnchants, VillagerConfig config) {
    Integer requiredMerchantLevel = 6 - enchant.getMaxLevel();
    Integer enchantLevel = merchantLevel - requiredMerchantLevel + 1;

    if (mainBookEnchants.containsKey(enchant) && mainBookEnchants.get(enchant) >= enchantLevel) {
      return null;
    }

    // Have to make a new itemStack and add the enchant to it
    ItemStack researchItemBook = new ItemStack(Items.BOOK);
    researchItemBook.setCustomName(Text.translatable("item.sandbox-trades.research_enchant"));
    EnchantmentHelper.set(new HashMap<Enchantment, Integer>(){{
      put(enchant, enchantLevel > 0 ? enchantLevel : 1); // default to level 1
    }}, researchItemBook);
    researchItemBook.getOrCreateNbt().putInt("CustomModelData", 1); // adds the texture

    if (merchantLevel < requiredMerchantLevel) {
      ItemStack item = new ItemStack(Items.FLOWER_BANNER_PATTERN);
      item.setCustomName(Text.translatable("item.sandbox-trades.required_level." + requiredMerchantLevel));
      return new TradeOffer(item, researchItemBook, 0, 0, 0.0F);
    }

    ItemStack enchantBookBuyItem = new ItemStack(Items.ENCHANTED_BOOK);
    EnchantmentHelper.set(new HashMap<Enchantment, Integer>(){{
      put(enchant, enchantLevel > 0 ? enchantLevel : 1); // default to level 1
    }}, enchantBookBuyItem);

    ItemStack buyItemOne = new ItemStack(Items.EMERALD, calcMultiplierPrice(enchant, enchantLevel, mainBookEnchants));
    if (config.researcherResearchItemPrice != null && config.researcherResearchItemPrice.itemOne != null) {
      CostItem costItem = config.researcherResearchItemPrice.itemOne;
      if (costItem.item != null) {
        Item item = Registry.ITEM.get(new Identifier(config.researcherResearchItemPrice.itemOne.item));
        int cost = costItem.count;
        if (costItem.costProcessor != null) {
          switch (costItem.costProcessor) {
            case "enchantLevel":
              cost = calcEnchantMultiplier(enchant, enchantLevel, mainBookEnchants);
              break;
            case "enchantLevelMultiplier":
              cost = calcMultiplierPrice(enchant, enchantLevel, mainBookEnchants);
              break;
          }
        }
        buyItemOne = new ItemStack(item, cost);
      }
    }

    return new TradeOffer(
      buyItemOne,
      enchantBookBuyItem,
      researchItemBook,
      1, // Max uses
      0, // Merchant xp
      0.2F // Multiplier (hero of the village/zombify)
    );
  }

  public static int calcEnchantMultiplier(Enchantment enchant, int level, Map<Enchantment, Integer> mainBookEnchants) {
    int enchantCost = 5 - enchant.getMaxLevel() + level;
    if (!mainBookEnchants.containsKey(enchant)) {
      return enchantCost;
    }

    // Reduce cost by currentLevel cost to prevent previous purchases from be a waste
    return enchantCost - calcEnchantMultiplier(enchant, mainBookEnchants.get(enchant), new HashMap<>());
  }

  public static int calcMultiplierPrice(Enchantment enchant, int level, Map<Enchantment, Integer> mainBookEnchants) {
    int multiplier = calcEnchantMultiplier(enchant, level, mainBookEnchants);
    return oneFifthOf64(multiplier); // get 1/5th of 64 (with some random/rounding) based on enchant level
  }

  public static int oneFifthOf64(int multiplier) {
    return (multiplier - 1) * 12 + random.nextInt(17); // 60/5 = 12; add 4 for total of 64 (add another 1, nextInt is not inclusive)
  }

  public static int calcTomeEnchantMultiplier(ItemStack researchTome, int multiplier) {
    int price = 0;
    Map<Enchantment, Integer> researchedEnchants = EnchantmentHelper.get(researchTome);
    for (Enchantment enchant : researchedEnchants.keySet()) {
      price += Math.round(5 / enchant.getMaxLevel() * researchedEnchants.get(enchant) * multiplier);
    }

    return price;
  }

  public static ItemStack getTomeEnchantPriceItem(CostItem costItem, ItemStack researchTome) {
    int price = 56; // default price
    ItemStack buyItem = CostItem.getItemStack(costItem);
    if (
      costItem.costProcessor != null &&
      costItem.costProcessor.equals("enchantLevelMultiplier")
    ) {
      price = Blueprint.calcTomeEnchantMultiplier(researchTome, costItem.count);
    } else {
      price = costItem.count;
    }

    int maxCount = buyItem.getMaxCount();
    if (price > buyItem.getMaxCount()) { // normally 64
      if (costItem.itemTierTwo != null) {
        buyItem = CostItem.getItemStackTierTwo(costItem);
        maxCount = buyItem.getMaxCount(); // reset maxCount with new buyItem
        price = Math.round(price / 9);
      }
      
      if (price > maxCount) {
        price = maxCount; // max at max count usually 64
      }
    }

    buyItem.setCount(price);
    return buyItem;
  }

  public boolean hasGlint(ItemStack stack) {
    return false;
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    if(stack.hasNbt()) {
      String blueprintItem = stack.getNbt().getString(itemType);
      tooltip.add(Text.translatable("item.minecraft." + blueprintItem));
    }
  }
}
