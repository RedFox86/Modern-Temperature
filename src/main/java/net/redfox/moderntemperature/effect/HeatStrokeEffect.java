package net.redfox.moderntemperature.effect;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HeatStrokeEffect extends MobEffect {
  public static final DamageSource HEAT_STROKE = new DamageSource("heat_stroke").bypassArmor();

  public HeatStrokeEffect(MobEffectCategory pCategory, int pColor) {
    super(pCategory, pColor);
  }

  @Override
  public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
    pLivingEntity.hurt(
        HEAT_STROKE,
        1 + pAmplifier);
    super.applyEffectTick(pLivingEntity, pAmplifier);
  }

  @Override
  public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
    return pDuration % Math.max(10, 50 - 10 * pAmplifier) == 0;
  }
}
