package net.redfox.moderntemperature.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.NetworkContext;
import net.redfox.moderntemperature.client.ClientTemperatureData;

public class TemperatureDataSyncS2CPacket {
  private final float temperature;

  public TemperatureDataSyncS2CPacket(float temperature) {
    this.temperature = temperature;
  }

  public TemperatureDataSyncS2CPacket(FriendlyByteBuf buf) {
    this.temperature = buf.readFloat();
  }

  public void toBytes(FriendlyByteBuf buf) {
    buf.writeFloat(temperature);
  }

  public boolean handle(CustomPayloadEvent.Context context) {
    context.enqueueWork(() -> {
      ClientTemperatureData.set(temperature);
    });
    return true;
  }
}
