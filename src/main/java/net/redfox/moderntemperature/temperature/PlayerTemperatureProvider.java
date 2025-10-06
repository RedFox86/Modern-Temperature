package net.redfox.moderntemperature.temperature;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerTemperatureProvider
    implements ICapabilityProvider, INBTSerializable<CompoundTag> {
  public static Capability<PlayerTemperature> PlAYER_TEMPERATURE =
      CapabilityManager.get(new CapabilityToken<>() {});
  private PlayerTemperature temperature = null;
  private final LazyOptional<PlayerTemperature> optional =
      LazyOptional.of(this::createPlayerTemperature);

  private @NotNull PlayerTemperature createPlayerTemperature() {
    if (this.temperature == null) {
      this.temperature = new PlayerTemperature();
    }

    return this.temperature;
  }

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(
      @NotNull Capability<T> cap, @Nullable Direction side) {
    if (cap == PlAYER_TEMPERATURE) {
      return optional.cast();
    }

    return LazyOptional.empty();
  }

  @Override
  public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    createPlayerTemperature().saveNBTData(tag);
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundTag nbt) {
    createPlayerTemperature().loadNBTData(nbt);
  }
}
