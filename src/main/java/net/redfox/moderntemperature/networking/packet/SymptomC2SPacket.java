package net.redfox.moderntemperature.networking.packet;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.network.NetworkEvent;
import net.redfox.moderntemperature.effect.ModEffects;
import net.redfox.moderntemperature.temperature.PlayerTemperatureProvider;

public class SymptomC2SPacket {
  public SymptomC2SPacket() {}

  public SymptomC2SPacket(FriendlyByteBuf buf) {}

  public void toBytes(FriendlyByteBuf buf) {}

  public boolean handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context context = supplier.get();
    context.enqueueWork(
        () -> {
          ServerPlayer player = context.getSender();

          if (player != null)
            player
                .getCapability(PlayerTemperatureProvider.PlAYER_TEMPERATURE)
                .ifPresent(
                    playerTemperature -> {
                      float temp = playerTemperature.getTemperature();
                      if (temp >= 80) {
                        player.addEffect(
                            new MobEffectInstance(
                                ModEffects.HEAT_STROKE.get(),
                                120,
                                Math.abs((int) ((50 - temp) / 30)) - 1,
                                false,
                                false,
                                true));
                      } else if (temp <= -80) {
                        player.addEffect(
                            new MobEffectInstance(
                                ModEffects.HYPOTHERMIA.get(),
                                120,
                                Math.abs((int) ((temp + 50) / 30)) - 1,
                                false,
                                false,
                                true));
                      }
                    });
        });
    return true;
  }
}
