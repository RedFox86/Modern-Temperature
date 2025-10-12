package net.redfox.moderntemperature.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.redfox.moderntemperature.util.ModDamageTypes;

public class HeatStrokeEffect extends MobEffect {
  public HeatStrokeEffect(MobEffectCategory pCategory, int pColor) {
    super(pCategory, pColor);
  }

  @Override
  public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
    pLivingEntity.hurt(
        new DamageSource(
            pLivingEntity
                .level()
                .registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(ModDamageTypes.HEAT_STROKE_KEY)),
        1 + pAmplifier);
    return super.applyEffectTick(pLivingEntity, pAmplifier);
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
    return pDuration % Math.max(10, 50 - 10 * pAmplifier) == 0;
  }
}
