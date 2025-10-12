package net.redfox.moderntemperature.networking;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
import net.redfox.moderntemperature.ModernTemperature;
import net.redfox.moderntemperature.networking.packet.SetTemperatureC2SPacket;
import net.redfox.moderntemperature.networking.packet.TemperatureDataSyncS2CPacket;

public class ModPackets {
  private static SimpleChannel INSTANCE;

  private static int packetId = 0;

  private static int id() {
    return packetId++;
  }

  public static void register() {
    SimpleChannel net =
        ChannelBuilder.named(
                ResourceLocation.fromNamespaceAndPath(ModernTemperature.MOD_ID, "message"))
            .networkProtocolVersion(1)
            .clientAcceptedVersions(Channel.VersionTest.exact(1))
            .serverAcceptedVersions(Channel.VersionTest.exact(1))
            .simpleChannel();

    INSTANCE = net;

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
    INSTANCE.send(message, PacketDistributor.SERVER.noArg());
  }

  public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
    INSTANCE.send(message, PacketDistributor.PLAYER.with(player));
  }
}
