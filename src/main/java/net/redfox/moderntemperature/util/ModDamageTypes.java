package net.redfox.moderntemperature.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.redfox.moderntemperature.ModernTemperature;

public class ModDamageTypes {
  public static final ResourceKey<DamageType> HYPOTHERMIA_KEY =
      ResourceKey.create(
          Registries.DAMAGE_TYPE,
          ResourceLocation.fromNamespaceAndPath(ModernTemperature.MOD_ID, "hypothermia"));
  public static final ResourceKey<DamageType> HEAT_STROKE_KEY =
      ResourceKey.create(
          Registries.DAMAGE_TYPE,
          ResourceLocation.fromNamespaceAndPath(ModernTemperature.MOD_ID, "heat_stroke"));
}
