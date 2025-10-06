package net.redfox.moderntemperature.networking.packet;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.redfox.moderntemperature.client.ClientTemperatureData;
import net.redfox.moderntemperature.networking.ModPackets;
import net.redfox.moderntemperature.temperature.PlayerTemperature;
import net.redfox.moderntemperature.temperature.PlayerTemperatureProvider;

public class TemperatureC2SPacket {
  public TemperatureC2SPacket() {}

  public TemperatureC2SPacket(FriendlyByteBuf buf) {}

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
                      float approachingTemperature =
                          PlayerTemperature.calculateTemperatureGoal(player);

                      player.sendSystemMessage(
                          Component.literal(
                              "Actual: "
                                  + playerTemperature.getTemperature()
                                  + ", goal: "
                                  + approachingTemperature
                                  + ", client: "
                                  + ClientTemperatureData.getPlayerTemperature()));

                      playerTemperature.approachTemperature(approachingTemperature);

                      ModPackets.sendToClient(
                          new TemperatureDataSyncS2CPacket(playerTemperature.getTemperature()),
                          player);
                    });
        });
    return true;
  }
}
