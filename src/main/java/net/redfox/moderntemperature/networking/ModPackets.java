package net.redfox.moderntemperature.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.redfox.moderntemperature.ModernTemperature;
import net.redfox.moderntemperature.networking.packet.SetTemperatureC2SPacket;
import net.redfox.moderntemperature.networking.packet.SymptomC2SPacket;
import net.redfox.moderntemperature.networking.packet.TemperatureC2SPacket;
import net.redfox.moderntemperature.networking.packet.TemperatureDataSyncS2CPacket;

public class ModPackets {
  private static SimpleChannel INSTANCE;

  private static int packetId = 0;

  private static int id() {
    return packetId++;
  }

  public static void register() {
    SimpleChannel net =
        NetworkRegistry.ChannelBuilder.named(
                ResourceLocation.fromNamespaceAndPath(ModernTemperature.MOD_ID, "message"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    INSTANCE = net;

    net.messageBuilder(TemperatureC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
        .decoder(TemperatureC2SPacket::new)
        .encoder(TemperatureC2SPacket::toBytes)
        .consumerMainThread(TemperatureC2SPacket::handle)
        .add();

    net.messageBuilder(SymptomC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
        .decoder(SymptomC2SPacket::new)
        .encoder(SymptomC2SPacket::toBytes)
        .consumerMainThread(SymptomC2SPacket::handle)
        .add();

    net.messageBuilder(TemperatureDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
        .decoder(TemperatureDataSyncS2CPacket::new)
        .encoder(TemperatureDataSyncS2CPacket::toBytes)
        .consumerMainThread(TemperatureDataSyncS2CPacket::handle)
        .add();
    net.messageBuilder(SetTemperatureC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
        .decoder(SetTemperatureC2SPacket::new)
        .encoder(SetTemperatureC2SPacket::toBytes)
        .consumerMainThread(SetTemperatureC2SPacket::handle)
        .add();
  }

  public static <MSG> void sendToServer(MSG message) {
    INSTANCE.sendToServer(message);
  }

  public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
    INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
  }
}
