package net.redfox.moderntemperature.temperature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.redfox.moderntemperature.config.ModernTemperatureCommonConfigs;
import net.redfox.moderntemperature.util.MathHelper;
import oshi.util.tuples.Pair;

@AutoRegisterCapability
public class PlayerTemperature {
  public static boolean hasTemperatureFluctuation = true;

  private static final HashMap<String, Integer> BIOME_VALUES = new HashMap<>();
  private static final HashMap<String, Integer> INSULATOR_VALUES = new HashMap<>();
  private static final HashMap<String, Integer> FLUID_TEMPERATURE_VALUES = new HashMap<>();
  private static final HashMap<String, Integer> WALKING_ON_TOP_VALUES = new HashMap<>();
  private static final HashMap<String, Integer> WALKING_ON_TOP_MINI_BLOCKS_VALUES = new HashMap<>();
  private static final HashMap<String, Pair<Integer, Integer>> ARMOR_VALUES = new HashMap<>();

  private float temperature = 0;

  public float getTemperature() {
    return MathHelper.roundToOneDecimal(temperature);
  }

  public void setTemperature(float temperature) {
    this.temperature = MathHelper.roundToOneDecimal(temperature);
  }

  public void approachTemperature(float goal) {
    float difference =
        MathHelper.roundToOneDecimal(
            ((Math.max(0.1, Math.min(5, Math.abs(goal - temperature) / 10))) * 10) / 10.0F);

    if (temperature > goal) {
      temperature -= difference;
    } else if (temperature < goal) {
      temperature += difference;
    }
  }

  public void saveNBTData(CompoundTag nbt) {
    nbt.putFloat("temperature", MathHelper.roundToOneDecimal(temperature));
  }

  public void loadNBTData(CompoundTag nbt) {
    temperature = MathHelper.roundToOneDecimal(nbt.getFloat("temperature"));
  }

  public static void populateConfigLists() {
    PlayerTemperature.populateListFromConfig(
        ModernTemperatureCommonConfigs.BIOME_TEMPERATURE, BIOME_VALUES);
    PlayerTemperature.populateListFromConfig(
        ModernTemperatureCommonConfigs.INSULATORS, INSULATOR_VALUES);
    PlayerTemperature.populateListFromConfig(
        ModernTemperatureCommonConfigs.FLUID_TEMPERATURES, FLUID_TEMPERATURE_VALUES);
    PlayerTemperature.populateListFromConfig(
        ModernTemperatureCommonConfigs.WALKING_ON_TOP_BOOTS, WALKING_ON_TOP_VALUES);
    PlayerTemperature.populateListFromConfig(
        ModernTemperatureCommonConfigs.WALKING_ON_TOP_MINI_BLOCKS_BOOTS,
        WALKING_ON_TOP_MINI_BLOCKS_VALUES);
    PlayerTemperature.populatePairMapFromConfig(ModernTemperatureCommonConfigs.ARMOR, ARMOR_VALUES);
  }

  private static void populateListFromConfig(
      ForgeConfigSpec.ConfigValue<List<String>> config, Map<String, Integer> map) {
    List<String> configValues = config.get();
    for (String value : configValues) {
      String[] split = value.split(",");
      map.put(split[0], Integer.parseInt(split[1]));
    }
  }

  private static void populatePairMapFromConfig(
      ForgeConfigSpec.ConfigValue<List<String>> config, Map<String, Pair<Integer, Integer>> map) {
    List<String> configValues = config.get();
    for (String value : configValues) {
      String[] split = value.split(",");
      map.put(split[0], new Pair<>(Integer.parseInt(split[1]), Integer.parseInt(split[2])));
    }
  }

  private static <T> String getItemNameFromKey(Holder<T> holder) {
    return holder.unwrapKey().map(key -> key.location().toString()).orElse("default");
  }

  private static String getCurrentPlayerBiomeName(Level level, ServerPlayer player) {
    return getItemNameFromKey(level.getBiome(player.blockPosition()));
  }

  private static int returnMapValueIfKeyExists(HashMap<String, Integer> map, String key) {
    if (map.containsKey(key)) return map.get(key);
    return 0;
  }

  private static String getBlockNameRelativeToPlayer(
      Level level, ServerPlayer player, Vec3i offset) {
    return getItemNameFromKey(
        level.getBlockState(player.blockPosition().offset(offset)).getBlockHolder());
  }

  public static float calculateTemperatureGoal(ServerPlayer player) {
    Level level = player.level();
    float goalTemperature = 0;
    // Biome
    goalTemperature +=
        returnMapValueIfKeyExists(BIOME_VALUES, getCurrentPlayerBiomeName(level, player));

    // Insulators (torches, campfires, lava, fire, magma)
    for (String insulator : INSULATOR_VALUES.keySet()) {
      if (level
              .getBlockStates(player.getBoundingBox().inflate(5))
              .filter(
                  blockState -> getItemNameFromKey(blockState.getBlockHolder()).equals(insulator))
              .toArray()
              .length
          > 0) goalTemperature += returnMapValueIfKeyExists(INSULATOR_VALUES, insulator);
    }
    if (player.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
      // Fluids
      goalTemperature +=
          returnMapValueIfKeyExists(
              FLUID_TEMPERATURE_VALUES, getBlockNameRelativeToPlayer(level, player, Vec3i.ZERO));

      // Ice, packed ice, blue ice
      goalTemperature +=
          returnMapValueIfKeyExists(
              FLUID_TEMPERATURE_VALUES,
              getBlockNameRelativeToPlayer(level, player, new Vec3i(0, -1, 0)));
    }
    // Rain / snow / thunder
    if (level.isRainingAt(player.blockPosition())) {
      goalTemperature -= 30;
    }
    if (level.isRaining()
        && level.getBiome(player.blockPosition()).value().coldEnoughToSnow(player.blockPosition())
        && level.canSeeSky(player.blockPosition())) {
      goalTemperature -= 60;
    }

    // Day or night

    if (level.isDay() && level.dimension() == Level.OVERWORLD) {
      goalTemperature += 10;
    } else {
      goalTemperature -= 10;
    }

    // On fire

    if (player.getRemainingFireTicks() > 0) {
      goalTemperature += 50;
    }

    // Altitude

    if (player.getY() < 40) {
      goalTemperature -=
          MathHelper.roundToOneDecimal(Math.abs((float) ((player.getY() - 40) / 5.0F)));
    }
    if (player.getY() > 80) {
      goalTemperature -=
          MathHelper.roundToOneDecimal(Math.abs((float) ((80 - player.getY()) / 5.0F)));
    }

    Pair<Integer, Integer> resistances = calculateHeatAndColdResistance(player);

    if (goalTemperature < 0) {
      goalTemperature += resistances.getB();
      if (goalTemperature > 0) goalTemperature = 0;
    } else if (goalTemperature > 0) {
      goalTemperature -= resistances.getA();
      if (goalTemperature < 0) goalTemperature = 0;
    }

    return goalTemperature
        + ((hasTemperatureFluctuation) ? MathHelper.roundToOneDecimal(Math.random() - 0.5F) : 0);
  }

  public static Pair<Integer, Integer> calculateHeatAndColdResistance(ServerPlayer player) {
    int coldResistance = 0;
    int heatResistance = 0;

    if (!player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
      String itemName =
          getItemNameFromKey(player.getItemBySlot(EquipmentSlot.HEAD).getItemHolder());
      if (ARMOR_VALUES.containsKey(itemName)) {
        heatResistance += ARMOR_VALUES.get(itemName).getA();
        coldResistance += ARMOR_VALUES.get(itemName).getB();
      }
    }
    if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
      String itemName =
          getItemNameFromKey(player.getItemBySlot(EquipmentSlot.CHEST).getItemHolder());
      if (ARMOR_VALUES.containsKey(itemName)) {
        heatResistance += ARMOR_VALUES.get(itemName).getA();
        coldResistance += ARMOR_VALUES.get(itemName).getB();
      }
    }
    if (!player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) {
      String itemName =
          getItemNameFromKey(player.getItemBySlot(EquipmentSlot.LEGS).getItemHolder());
      if (ARMOR_VALUES.containsKey(itemName)) {
        heatResistance += ARMOR_VALUES.get(itemName).getA();
        coldResistance += ARMOR_VALUES.get(itemName).getB();
      }
    }
    if (!player.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
      String itemName =
          getItemNameFromKey(player.getItemBySlot(EquipmentSlot.FEET).getItemHolder());
      if (ARMOR_VALUES.containsKey(itemName)) {
        heatResistance += ARMOR_VALUES.get(itemName).getA();
        coldResistance += ARMOR_VALUES.get(itemName).getB();
      }
    }
    return new Pair<>(heatResistance, coldResistance);
  }
}
