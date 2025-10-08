package net.redfox.moderntemperature.temperature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.redfox.moderntemperature.config.ModernTemperatureCommonConfigs;
import net.redfox.moderntemperature.util.MathHelper;
import oshi.util.tuples.Pair;

@AutoRegisterCapability
public class PlayerTemperature {
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

  public static float calculateTemperatureGoal(ServerPlayer player) {
    float goalTemperature = 0;
    // Biome
    String currentBiome = getItemNameFromKey(player.level().getBiome(player.blockPosition()));

    if (BIOME_VALUES.containsKey(currentBiome)) goalTemperature = BIOME_VALUES.get(currentBiome);
    // Insulators (torches, campfires, lava, fire, magma)
    for (String insulator : INSULATOR_VALUES.keySet()) {
      if (player
              .level()
              .getBlockStates(player.getBoundingBox().inflate(5))
              .filter(
                  blockState -> {
                    String blockName = getItemNameFromKey(blockState.getBlockHolder());
                    return blockName.equals(insulator);
                  })
              .toArray()
              .length
          > 0)
        if (INSULATOR_VALUES.containsKey(insulator))
          goalTemperature += INSULATOR_VALUES.get(insulator);
    }
    // Fluids
    String blockName =
        getItemNameFromKey(player.level().getBlockState(player.blockPosition()).getBlockHolder());
    if (FLUID_TEMPERATURE_VALUES.containsKey(blockName))
      goalTemperature += FLUID_TEMPERATURE_VALUES.get(blockName);

    // Ice, packed ice, blue ice

    if (player.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
      String blockBelowName =
          getItemNameFromKey(
              player.level().getBlockState(player.blockPosition().below()).getBlockHolder());
      if (WALKING_ON_TOP_VALUES.containsKey(blockBelowName))
        goalTemperature += WALKING_ON_TOP_VALUES.get(blockBelowName);
      if (WALKING_ON_TOP_MINI_BLOCKS_VALUES.containsKey(blockName))
        goalTemperature += WALKING_ON_TOP_MINI_BLOCKS_VALUES.get(blockName);
    }

    // Rain / snow / thunder

    if (player.level().isRainingAt(player.blockPosition())) {
      goalTemperature -= 30;
    }
    if (player.level().isRaining()
        && player
            .level()
            .getBiome(player.blockPosition())
            .value()
            .coldEnoughToSnow(player.blockPosition())
        && player.level().canSeeSky(player.blockPosition())) {
      goalTemperature -= 60;
    }

    // Day or night

    if (player.level().isDay()) {
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

    return goalTemperature + MathHelper.roundToOneDecimal(Math.random() - 0.5F);
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
