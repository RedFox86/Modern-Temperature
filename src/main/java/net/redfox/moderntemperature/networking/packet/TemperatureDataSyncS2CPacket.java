package net.redfox.moderntemperature.networking.packet;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
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

  public boolean handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context context = supplier.get();
    context.enqueueWork(() -> ClientTemperatureData.set(temperature));
    return true;
  }
}
