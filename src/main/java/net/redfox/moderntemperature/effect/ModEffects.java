package net.redfox.moderntemperature.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.redfox.moderntemperature.ModernTemperature;

public class ModEffects {
  public static final DeferredRegister<MobEffect> MOB_EFFECTS =
      DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModernTemperature.MOD_ID);

  public static final RegistryObject<MobEffect> HYPOTHERMIA =
      MOB_EFFECTS.register(
          "hypothermia",
          () ->
              new HypothermiaEffect(MobEffectCategory.HARMFUL, 0x7ae9ff)
                  .addAttributeModifier(
                      Attributes.MOVEMENT_SPEED,
                      ResourceLocation.fromNamespaceAndPath(ModernTemperature.MOD_ID, "effect.hypothermia"),
                      -0.15F,
                      AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

  public static final RegistryObject<MobEffect> HEAT_STROKE =
      MOB_EFFECTS.register(
          "heat_stroke",
          () ->
              new HeatStrokeEffect(MobEffectCategory.HARMFUL, 0xfa7223)
                  .addAttributeModifier(
                      Attributes.ATTACK_DAMAGE,
                      ResourceLocation.fromNamespaceAndPath(ModernTemperature.MOD_ID, "effect.heat_stroke"),
                      -0.15F,
                      AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

  public static void register(IEventBus bus) {
    MOB_EFFECTS.register(bus);
  }
}
