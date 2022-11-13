package io.sandbox.trades;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

public class TradeFactories {
  public static Map<VillagerProfession, Map<VillagerType, Enchantment[]>> PROFESSION_BIOME_TRADES = new HashMap<VillagerProfession, Map<VillagerType, Enchantment[]>>(){{
    put(VillagerProfession.LIBRARIAN, new HashMap<VillagerType, Enchantment[]>() {{
      put(VillagerType.DESERT, new Enchantment[]{
        Enchantments.BLAST_PROTECTION,
        Enchantments.FIRE_PROTECTION,
        Enchantments.FIRE_ASPECT,
        Enchantments.FLAME,
        Enchantments.KNOCKBACK,
        Enchantments.LOOTING,
        Enchantments.UNBREAKING
      });
      put(VillagerType.PLAINS, new Enchantment[]{
        Enchantments.CHANNELING,
        Enchantments.IMPALING,
        Enchantments.RIPTIDE,
        Enchantments.LOYALTY,
        Enchantments.PROJECTILE_PROTECTION,
        Enchantments.POWER,
        Enchantments.INFINITY,
        Enchantments.PUNCH,
        Enchantments.LURE,
        Enchantments.LUCK_OF_THE_SEA
      });
      put(VillagerType.SAVANNA, new Enchantment[]{
        Enchantments.PROJECTILE_PROTECTION,
        Enchantments.SHARPNESS,
        Enchantments.SWEEPING,
        Enchantments.MULTISHOT,
        Enchantments.QUICK_CHARGE,
        Enchantments.PIERCING,
        Enchantments.MENDING
      });
      put(VillagerType.SNOW, new Enchantment[]{
        Enchantments.AQUA_AFFINITY,
        Enchantments.RESPIRATION,
        Enchantments.DEPTH_STRIDER,
        Enchantments.FROST_WALKER,
        Enchantments.SMITE
      });
      put(VillagerType.TAIGA, new Enchantment[]{
        Enchantments.PROTECTION,
        Enchantments.BANE_OF_ARTHROPODS,
        Enchantments.EFFICIENCY,
        Enchantments.FORTUNE,
        Enchantments.SILK_TOUCH,
        Enchantments.FEATHER_FALLING,
        Enchantments.THORNS
      });
    }});
  }};

  public static class BuyForOneEmeraldFactory implements TradeOffers.Factory {
    private final Item buy;
    private final int price;
    private final int maxUses;
    private final int experience;
    private final float multiplier;

    public BuyForOneEmeraldFactory(ItemConvertible item, int price, int maxUses, int experience) {
      this.buy = item.asItem();
      this.price = price;
      this.maxUses = maxUses;
      this.experience = experience;
      this.multiplier = 0.05F;
    }

    @Override
    public TradeOffer create(Entity entity, net.minecraft.util.math.random.Random random) {
      ItemStack itemStack = new ItemStack(this.buy, this.price);
      return new TradeOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.experience, this.multiplier);
    }
  }

  public static class SellItemFactory implements TradeOffers.Factory {
    private final ItemStack sell;
    private final int price;
    private final int count;
    private final int maxUses;
    private final int experience;
    private final float multiplier;

    public SellItemFactory(Block block, int price, int count, int maxUses, int experience) {
      this(new ItemStack(block), price, count, maxUses, experience);
    }

    public SellItemFactory(Item item, int price, int count, int experience) {
      this((ItemStack) (new ItemStack(item)), price, count, 12, experience);
    }

    public SellItemFactory(Item item, int price, int count, int maxUses, int experience) {
      this(new ItemStack(item), price, count, maxUses, experience);
    }

    public SellItemFactory(ItemStack itemStack, int price, int count, int maxUses, int experience) {
      this(itemStack, price, count, maxUses, experience, 0.05F);
    }

    public SellItemFactory(ItemStack itemStack, int price, int count, int maxUses, int experience, float multiplier) {
      this.sell = itemStack;
      this.price = price;
      this.count = count;
      this.maxUses = maxUses;
      this.experience = experience;
      this.multiplier = multiplier;
    }

    @Override
    public TradeOffer create(Entity entity, net.minecraft.util.math.random.Random random) {
      return new TradeOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(this.sell.getItem(), this.count),
          this.maxUses, this.experience, this.multiplier);
    }
  }
}
