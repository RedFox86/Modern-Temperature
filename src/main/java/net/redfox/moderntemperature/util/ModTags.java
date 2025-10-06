package net.redfox.moderntemperature.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.redfox.moderntemperature.ModernTemperature;

public class ModTags {
  public static class Biomes {
    public static final TagKey<Biome> SCORCHING_BIOMES = tag("scorching");
    public static final TagKey<Biome> VERY_HOT_BIOMES = tag("very_hot");
    public static final TagKey<Biome> HOT_BIOMES = tag("hot");
    public static final TagKey<Biome> WARM_BIOMES = tag("warm");
    public static final TagKey<Biome> MILD_BIOMES = tag("mild");
    public static final TagKey<Biome> CHILLY_BIOMES = tag("chilly");
    public static final TagKey<Biome> COLD_BIOMES = tag("cold");
    public static final TagKey<Biome> VERY_COLD_BIOMES = tag("very_cold");
    public static final TagKey<Biome> FREEZING_BIOMES = tag("freezing");

    private static TagKey<Biome> tag(String name) {
      return TagKey.create(
          Registry.BIOME_REGISTRY,
          ResourceLocation.fromNamespaceAndPath(ModernTemperature.MOD_ID, name));
    }
  }
}
