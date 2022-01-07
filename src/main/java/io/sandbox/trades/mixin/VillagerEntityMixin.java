package io.sandbox.trades.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.sandbox.trades.TradeFactories;
import io.sandbox.trades.items.IncreaseLevel;
import io.sandbox.trades.items.ItemLoader;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradeOffers.Factory;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements VillagerDataContainer {
  @Shadow public abstract VillagerData getVillagerData();
  @Shadow private void sayNo(){};

  public BlockPos spawnPos;
  private static int[] MerchExpRanges = {
    10,
    70,
    150,
    250,
  };

  public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
    super(entityType, world);
    throw new IllegalStateException("VillagerEntityMixin's dummy constructor called!");
  }

  @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
  private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo cbi) {
    if (this.spawnPos == null) {
      System.out.println("Villager Spawn Position was NULL on NBT write, backfilling with current position...");
      this.spawnPos = this.getBlockPos();
    }

    nbt.putInt("spawnX", this.spawnPos.getX());
    nbt.putInt("spawnY", this.spawnPos.getY());
    nbt.putInt("spawnZ", this.spawnPos.getZ());
  }

  @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
  private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo cbi) {
    if (!nbt.contains("spawnX") || !nbt.contains("spawnY") || !nbt.contains("spawnZ")) {
      System.out.println("Villager Spawn Position was missing or incomplete on NBT read, backfilling with current position...");
      this.spawnPos = this.getBlockPos();
      return;
    } else if (nbt.getInt("spawnX") == 0 && nbt.getInt("spawnY") == 0 && nbt.getInt("spawnZ") == 0) {
      System.out.println("Villager Spawn Position was set to 0, 0, 0 which is assumed to be invalid. Backfilling with current position...");
      this.spawnPos = this.getBlockPos();
      return;
    }

    this.spawnPos = new BlockPos(nbt.getInt("spawnX"), nbt.getInt("spawnY"), nbt.getInt("spawnZ"));
  }

  @Inject(at = @At("HEAD"), method = "initialize")
  private void initialize(CallbackInfoReturnable<EntityData> cbir) {
    this.spawnPos = this.getBlockPos();
  }

  // Helper that determines if a villager can be interacted with due to it being away from its village.
  private boolean getIsOutOfBounds() {
    // This prevents the Client call that has spawnPos set to null and weird scenarios...
    if (this.spawnPos == null) { return false; }

    BlockPos currentPos = this.getBlockPos();
    if (Math.abs(currentPos.getX() - this.spawnPos.getX()) > 150) {
      return true;
    }

    if (Math.abs(currentPos.getZ() - this.spawnPos.getZ()) > 150) {
      return true;
    }

    if (Math.abs(currentPos.getY() - this.spawnPos.getY()) > 100) {
      return true;
    }

    return false;
  }

  @Inject(at = @At("HEAD"), method = "canBreed", cancellable = true)
  private void canBreed(CallbackInfoReturnable<Boolean> cbir) {
    if (this.getIsOutOfBounds()) {
      cbir.setReturnValue(false);
    }
  }

  @Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
  private void interactMob(CallbackInfoReturnable<ActionResult> cbir) {
    if (this.getIsOutOfBounds()) {
      this.sayNo();
      System.out.println("SPAWN: " + this.spawnPos.toShortString());
      cbir.setReturnValue(ActionResult.success(this.world.isClient));
      cbir.cancel();
    }
  }

  @Inject(at = @At("HEAD"), method = "fillRecipes", cancellable = true)
  protected void fillRecipes(CallbackInfo cb) {
    VillagerData villagerData = this.getVillagerData();
    Int2ObjectMap<Factory[]> int2ObjectMap = (Int2ObjectMap<Factory[]>)TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(villagerData.getProfession());
    if (int2ObjectMap == null || int2ObjectMap.isEmpty()) {
      return;
    }

    // Default Trades...
    Factory[] factorys = (Factory[])int2ObjectMap.get(villagerData.getLevel());
    if (factorys == null) {
      return;
    }

    // Get current trades
    TradeOfferList tradeOfferList = this.getOffers();
    int merchantLevel = villagerData.getLevel();

    // The last trade should always be the leveing trade
    // Remove this trade, it's the previous level's leveling trade
    // We can add the next level increase trade
    if (tradeOfferList.size() > 0) {
      int increaseLevelTrade = -1;
      for (int i = 0; i < tradeOfferList.size(); i++) {
        TradeOffer trade = tradeOfferList.get(i);
        ItemStack sellItem = trade.getSellItem();
        if (sellItem.getItem() instanceof IncreaseLevel) {
          // only remove IncreaseLevel trades
          increaseLevelTrade = i;
        }
      }

      if (increaseLevelTrade >= 0) {
        tradeOfferList.remove(increaseLevelTrade);
      }
    }

    if (villagerData.getProfession() != VillagerProfession.LIBRARIAN) {
      this.fillRecipesFromPool(tradeOfferList, factorys, 2);
    } else {
      if (merchantLevel == 1) {
        // Pass biome specific enchant
        Map<VillagerType, Enchantment[]> biomeMaps = TradeFactories.PROFESSION_BIOME_TRADES.get(villagerData.getProfession());
        Enchantment[] enchantList = biomeMaps.get(villagerData.getType());
        Enchantment enchant = enchantList[this.random.nextInt(enchantList.length)];
        this.fillEnchantOffers(tradeOfferList, factorys, 2, enchant, 1);
      } else {
        // I think the Client calls this without data... and breaks it
        // So let's ignore those calls
        if (tradeOfferList.size() == 0) {
          cb.cancel();
          return;
        }

        // Remove any books
        factorys = Arrays.stream(factorys).filter(factory -> {
          TradeOffer tradeOffer = factory.create(this, this.random);
          return tradeOffer.getSellItem().getItem() != Items.ENCHANTED_BOOK;
        }).toArray(Factory[]::new);
        
        // Otherwise we we care if level 1 has a book
        ItemStack firstTrade = tradeOfferList.get(0).getSellItem();
        ItemStack secondTrade = tradeOfferList.get(1).getSellItem();
        Enchantment enchant = null;

        if (firstTrade.getItem() == Items.ENCHANTED_BOOK) {
          enchant = EnchantmentHelper.get(firstTrade).keySet().iterator().next(); // just grab the first item
        } else if (secondTrade.getItem() == Items.ENCHANTED_BOOK) {
          enchant = EnchantmentHelper.get(secondTrade).keySet().iterator().next(); // just grab the first item
        }

        if (enchant != null) {
          this.fillEnchantOffers(tradeOfferList, factorys, 2, enchant, merchantLevel);
        } else {
          this.fillRecipesFromPool(tradeOfferList, factorys, 2);
        }
      }

    }

    if (merchantLevel < 5 && merchantLevel > 0) { // if not max level
      ItemStack priceItem = new ItemStack(
        Items.DIAMOND,
        merchantLevel // 1,2,3,4 should be the progression (10 total)
      );
      ItemStack item = new ItemStack(ItemLoader.INCREASE_LEVEL);
      item.setCustomName(Text.of("Increase Merchant Level"));
      // Add the trade as the last one
      int neededExpToLevel = VillagerEntityMixin.MerchExpRanges[merchantLevel - 1]; // This should not be out of range as it will not fire at level 5
      
      // Strip the exp from the other TradeOffers
      for (int i = 0; i < tradeOfferList.size(); i++) {
        TradeOffer trade = tradeOfferList.get(i);
        if (trade.getMerchantExperience() > 0) {
          // merchantExperience is private and has no updater
          // So... we convert to nbt data, update that and then create a new TradeOffer
          NbtCompound nbt = trade.toNbt();
          nbt.putInt("xp", 0);
          tradeOfferList.set(i, new TradeOffer(nbt));
        }
      }

      // Add the levelup trade
      tradeOfferList.add(new TradeOffer(priceItem, item, 1, neededExpToLevel, 0.0F));
    }

    // We are basically replacing fillRecipes(), so always cancel
    // if it makes it this far
    cb.cancel();
  }

  public void fillEnchantOffers(TradeOfferList tradeOfferList, Factory[] factorys, int count, Enchantment enchant, int merchantLevel) {
    Factory[] factories = this.resizeFactoryList(factorys, count);
    Boolean shouldAddEnchant = false;
    Boolean addedEnchant = false;

    // If merchant level is between 1 and the required level, we don't want enchant books in the pool
    if (enchant != null && merchantLevel > 1 && merchantLevel < 6 - enchant.getMaxLevel()) {
      Factory[] pool = Arrays.stream(factorys).filter(factory -> {
        TradeOffer tradeOffer = factory.create(this, this.random);
        return tradeOffer.getSellItem().getItem() != Items.ENCHANTED_BOOK;
      }).toArray(Factory[]::new);
      factories = this.resizeFactoryList(pool, count);
    }
    
    if (enchant != null && merchantLevel > 1 && merchantLevel >= 6 - enchant.getMaxLevel()) {
      shouldAddEnchant = true;
    }
    
    for(Factory factory : factories) {
      TradeOffer tradeOffer = factory.create(this, this.random);
      ItemStack sellItem = tradeOffer.getSellItem();
      if (sellItem.getItem() == Items.ENCHANTED_BOOK) {
        addedEnchant = true;
        this.addEnchantToOfferList(tradeOfferList, enchant, merchantLevel);
      } else if (tradeOffer != null) {
        tradeOfferList.add(tradeOffer);
      }
    }

    // If the level does not have an enchant book
    if (shouldAddEnchant && !addedEnchant) {
      if (tradeOfferList.size() % 2 == 0) {
        // If the 2 trades have been filled... remove the last one
        tradeOfferList.remove(tradeOfferList.size() - 1);
      }
      this.addEnchantToOfferList(tradeOfferList, enchant, merchantLevel);
    } 
  }

  public void addEnchantToOfferList(TradeOfferList tradeOfferList, Enchantment enchant, int merchantLevel) {
    Integer requiredMerchantLevel = 6 - enchant.getMaxLevel();
    Integer enchantLevel = merchantLevel - requiredMerchantLevel + 1;

    // Have to make a new itemStack and add the enchant to it
    ItemStack newSellItem = new ItemStack(Items.ENCHANTED_BOOK);
    EnchantmentHelper.set(new HashMap<Enchantment, Integer>(){{
      put(enchant, enchantLevel > 0 ? enchantLevel : 1); // default to level 1
    }}, newSellItem);

    if (merchantLevel < requiredMerchantLevel) {
      if (merchantLevel == 1) {
        ItemStack item = new ItemStack(Items.FLOWER_BANNER_PATTERN);
        item.setCustomName(Text.of("Requires Merchant Level: " + requiredMerchantLevel));
        // Add the TradeOffer directly
        tradeOfferList.add(new TradeOffer(item, newSellItem, 0, 0, 0.0F));
      }
    } else {
      tradeOfferList.add(new TradeOffer(new ItemStack(Items.EMERALD, this.random.nextInt(59) + 5), new ItemStack(Items.BOOK), newSellItem, 12, 0, 0.2F));
    }
  }

  public Factory[] resizeFactoryList(Factory[] factorys, int count) {
    List<Factory> result = new ArrayList<Factory>();
    Set<Integer> set = Sets.newHashSet();
    if (factorys.length > 2) {
        while(set.size() < 2) {
          set.add(this.random.nextInt(factorys.length));
        }
    } else {
        for(int i = 0; i < factorys.length; ++i) {
          set.add(i);
        }
    }

    Iterator<Integer> tradesToAdd = set.iterator();

    while(tradesToAdd.hasNext()) {
      Integer integer = (Integer)tradesToAdd.next();
      Factory factory = factorys[integer];
      if (factory != null) {
        result.add(factory);
      }
    }

    return result.toArray(Factory[]::new);
  }
}