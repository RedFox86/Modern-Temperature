package net.redfox.moderntemperature.config;

import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModernTemperatureCommonConfigs {
  private static final List<String> DEFAULT_BIOME_TEMPERATURES =
      List.of(
          "default,0",
          "minecraft:ocean,-20",
          "minecraft:deep_ocean,-30",
          "minecraft:warm_ocean,20",
          "minecraft:lukewarm_ocean,5",
          "minecraft:deep_lukewarm_ocean,-5",
          "minecraft:cold_ocean,-40",
          "minecraft:deep_cold_ocean,-50",
          "minecraft:frozen_ocean,-60",
          "minecraft:deep_frozen_ocean,-70",
          "minecraft:mushroom_fields,0",
          "minecraft:jagged_peaks,-80",
          "minecraft:frozen_peaks,-100",
          "minecraft:stony_peaks,-60",
          "minecraft:meadow,-10",
          "minecraft:cherry_grove,10",
          "minecraft:grove,-50",
          "minecraft:windswept_hills,-30",
          "minecraft:windswept_gravelly_hills,-30",
          "minecraft:windswept_forest,-30",
          "minecraft:forest,10",
          "minecraft:flower_forest,10",
          "minecraft:taiga,-10",
          "minecraft:old_growth_pine_taiga,-20",
          "minecraft:old_growth_spruce_taiga,-20",
          "minecraft:snowy_taiga,-40",
          "minecraft:birch_forest,10",
          "minecraft:old_growth_birch_forest,10",
          "minecraft:dark_forest,40",
          "minecraft:pale_garden,-70",
          "minecraft:jungle,90",
          "minecraft:sparse_jungle,70",
          "minecraft:bamboo_jungle,80",
          "minecraft:river,-20",
          "minecraft:frozen_river,-80",
          "minecraft:swamp,60",
          "minecraft:mangrove_swamp,90",
          "minecraft:beach,30",
          "minecraft:snowy_beach,-30",
          "minecraft:stony_shore,-20",
          "minecraft:plains,0",
          "minecraft:sunflower_plains,5",
          "minecraft:snowy_plains,-40",
          "minecraft:ice_spikes,-70",
          "minecraft:desert,120",
          "minecraft:savanna,70",
          "minecraft:savanna_plateau,70",
          "minecraft:windswept_savanna,65",
          "minecraft:badlands,140",
          "minecraft:wooded_badlands,130",
          "minecraft:eroded_badlands,150",
          "minecraft:deep_dark,-150",
          "minecraft:dripstone_caves,-30",
          "minecraft:lush_caves,5",
          "minecraft:the_void,0",
          "minecraft:nether_wastes,200",
          "minecraft:soul_sand_valley,190",
          "minecraft:crimson_forest,220",
          "minecraft:warped_forest,80",
          "minecraft:basalt_delta,230",
          "minecraft:the_end,-400",
          "minecraft:small_end_islands,-400",
          "minecraft:end_midlands,-400",
          "minecraft:end_highlands,-400",
          "minecraft:end_barrens,-400");

  private static final List<String> DEFAULT_INSULATORS =
      List.of(
          "default,0",
          "minecraft:torch,15",
          "minecraft:fire,20",
          "minecraft:lantern,25",
          "minecraft:wall_torch,15",
          "minecraft:campfire,30",
          "minecraft:lava,60",
          "minecraft:magma_block,30",
          "minecraft:candle,5",
          "minecraft:soul_campfire,-30",
          "minecraft:soul_torch,-15",
          "minecraft:soul_lantern,-25");

  private static final List<String> DEFAULT_FLUID_TEMPERATURES =
      List.of(
          "default,0", "minecraft:water,-30", "minecraft:powdered_snow,-100", "minecraft:lava,100");

  private static final List<String> DEFAULT_WALKING_ON_TOP_BOOTS =
      List.of(
          "default,0",
          "minecraft:blue_ice,-60",
          "minecraft:packed_ice,-40",
          "minecraft:ice,-20",
          "minecraft:snow_block,-15");

  private static final List<String> DEFAULT_WALKING_ON_TOP_MINI_BLOCKS_BOOTS =
      List.of("default,0", "minecraft:snow,15,-15");

  private static final List<String> DEFAULT_ARMOR =
      List.of(
          "default,0,0",
          "minecraft:leather_boots,2,10",
          "minecraft:leather_leggings,6,30",
          "minecraft:leather_chestplate,8,40",
          "minecraft:leather_helmet,4,20",
          "minecraft:chainmail_boots,30,0",
          "minecraft:chainmail_leggings,50,0",
          "minecraft:chainmail_chestplate,60,0",
          "minecraft:chainmail_helmet,40,0",
          "minecraft:iron_boots,-10,-10",
          "minecraft:iron_leggings,-30,-30",
          "minecraft:iron_chestplate,-40,-40",
          "minecraft:iron_helmet,-20,-20",
          "minecraft:gold_boots,10,10",
          "minecraft:gold_leggings,30,30",
          "minecraft:gold_chestplate,40,40",
          "minecraft:gold_helmet,20,20",
          "minecraft:diamond_boots,-20,-20",
          "minecraft:diamond_leggings,-40,-40",
          "minecraft:diamond_chestplate,-50,-50",
          "minecraft:diamond_helmet,-30,-30",
          "minecraft:netherite_boots,40,-40",
          "minecraft:netherite_leggings,60,-60",
          "minecraft:netherite_chestplate,70,-70",
          "minecraft:netherite_helmet,50,-50");

  public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final ForgeConfigSpec SPEC;

  public static final ForgeConfigSpec.ConfigValue<List<String>> BIOME_TEMPERATURE;
  public static final ForgeConfigSpec.ConfigValue<List<String>> INSULATORS;
  public static final ForgeConfigSpec.ConfigValue<List<String>> FLUID_TEMPERATURES;
  public static final ForgeConfigSpec.ConfigValue<List<String>> WALKING_ON_TOP_BOOTS;
  public static final ForgeConfigSpec.ConfigValue<List<String>> WALKING_ON_TOP_MINI_BLOCKS_BOOTS;
  public static final ForgeConfigSpec.ConfigValue<List<String>> ARMOR;

  public static final ForgeConfigSpec.ConfigValue<Boolean> TEMPERATURE_FLUCTUATION;

  static {
    BUILDER.push("Common Configs for Modern Temperature");

    BIOME_TEMPERATURE =
        BUILDER
            .comment("A Map of all biomes and their temperature values.")
            .define("biomeTemperature", DEFAULT_BIOME_TEMPERATURES);
    INSULATORS =
        BUILDER
            .comment("A Map of all the blocks that will provide a change in temperature")
            .define("insulators", DEFAULT_INSULATORS);
    FLUID_TEMPERATURES =
        BUILDER
            .comment(
                "A Map of all the blocks that will change your temperature by standing in them")
            .define("fluidTemperature", DEFAULT_FLUID_TEMPERATURES);
    WALKING_ON_TOP_BOOTS =
        BUILDER
            .comment(
                "A Map of all the blocks that will change your temperature by standing on top of them while not wearing boots")
            .define("walkingOnTop", DEFAULT_WALKING_ON_TOP_BOOTS);
    WALKING_ON_TOP_MINI_BLOCKS_BOOTS =
        BUILDER
            .comment(
                "A Map of all the mini blocks that will change your temperature by standing on top of them while not wearing boots")
            .define("walkingOnTopMiniBlocks", DEFAULT_WALKING_ON_TOP_MINI_BLOCKS_BOOTS);
    ARMOR =
        BUILDER
            .comment(
                "A Map of all the armor pieces that give some temperature value while wearing them")
            .define("armor", DEFAULT_ARMOR);

    TEMPERATURE_FLUCTUATION =
        BUILDER
            .comment("If true, the temperature will fluctuate around its goal by a margin of one.")
            .define("temperatureFluctuation", true);

    BUILDER.pop();
    SPEC = BUILDER.build();
  }
}
