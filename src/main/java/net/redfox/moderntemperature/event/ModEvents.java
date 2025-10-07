package net.redfox.moderntemperature.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import net.redfox.moderntemperature.ModernTemperature;
import net.redfox.moderntemperature.command.GetTemperature;
import net.redfox.moderntemperature.command.SetTemperature;
import net.redfox.moderntemperature.effect.ModEffects;
import net.redfox.moderntemperature.networking.ModPackets;
import net.redfox.moderntemperature.networking.packet.TemperatureDataSyncS2CPacket;
import net.redfox.moderntemperature.temperature.PlayerTemperature;
import net.redfox.moderntemperature.temperature.PlayerTemperatureProvider;

@Mod.EventBusSubscriber(modid = ModernTemperature.MOD_ID)
public class ModEvents {
  @SubscribeEvent
  public static void onServerTickEvent(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      if (event.getServer().getTickCount() % 100 == 0) {
        return;
      }

      if (event.getServer().getTickCount() % 20 == 0) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
          player
              .getCapability(PlayerTemperatureProvider.PlAYER_TEMPERATURE)
              .ifPresent(
                  playerTemperature -> {
                    float approachingTemperature =
                        PlayerTemperature.calculateTemperatureGoal(player);
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
                      playerTemperature.approachTemperature(approachingTemperature);
                      ModPackets.sendToClient(
                          new TemperatureDataSyncS2CPacket(playerTemperature.getTemperature()),
                          player);
                  });
        }
      }
    }
  }

  @SubscribeEvent
  public static void onAttachCapabilitiesPlayerEvent(AttachCapabilitiesEvent<Entity> event) {
    if (event.getObject() instanceof Player) {
      if (!event
          .getObject()
          .getCapability(PlayerTemperatureProvider.PlAYER_TEMPERATURE)
          .isPresent()) {
        event.addCapability(
            ResourceLocation.fromNamespaceAndPath(ModernTemperature.MOD_ID, "properties"),
            new PlayerTemperatureProvider());
      }
    }
  }

  @SubscribeEvent
  public static void onPlayerClonedEvent(PlayerEvent.Clone event) {
    if (event.isWasDeath()) {
      event
          .getEntity()
          .getCapability(PlayerTemperatureProvider.PlAYER_TEMPERATURE)
          .ifPresent(
              playerTemperature -> {
                playerTemperature.setTemperature(60);
              });
    }
  }

  @SubscribeEvent
  public static void onPlayerJoinWorldEvent(EntityJoinLevelEvent event) {
    if (!event.getLevel().isClientSide()) {
      if (event.getEntity() instanceof ServerPlayer player) {
        player
            .getCapability(PlayerTemperatureProvider.PlAYER_TEMPERATURE)
            .ifPresent(
                playerTemperature -> {
                  ModPackets.sendToClient(
                      new TemperatureDataSyncS2CPacket(playerTemperature.getTemperature()), player);
                });
      }
    }
  }

  @SubscribeEvent
  public static void onCommandsRegister(RegisterCommandsEvent event) {
    new GetTemperature(event.getDispatcher());
    new SetTemperature(event.getDispatcher());

    ConfigCommand.register(event.getDispatcher());
  }
}
