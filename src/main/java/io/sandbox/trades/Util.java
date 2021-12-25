package io.sandbox.trades;

import com.google.common.collect.ImmutableMap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;

public class Util {
  public static String MOD_ID = "trade";

  public static Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeOffers.Factory[]> immutableMap) {
    return new Int2ObjectOpenHashMap<TradeOffers.Factory[]>(immutableMap);
  }

  public static Identifier id(String name) {
    return new Identifier(MOD_ID, name);
  }
}
