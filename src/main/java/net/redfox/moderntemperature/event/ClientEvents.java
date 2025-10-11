package net.redfox.moderntemperature.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.redfox.moderntemperature.ModernTemperature;
import net.redfox.moderntemperature.client.TemperatureHudOverlay;

public class ClientEvents {
  @Mod.EventBusSubscriber(
      modid = ModernTemperature.MOD_ID,
      value = Dist.CLIENT,
      bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class ClientModBusEvents {
    @SubscribeEvent
    public static void onRegisterGuiOverlaysEvent(RegisterGuiOverlaysEvent event) {
      event.registerAbove(
          VanillaGuiOverlay.DEBUG_TEXT.id(),
          "temperature_gauge",
          TemperatureHudOverlay.TEMPERATURE_GAUGE);
      event.registerAbove(
          VanillaGuiOverlay.DEBUG_TEXT.id(),
          "temperature_reading",
          TemperatureHudOverlay.TEMPERATURE_READING);
    }
  }
}
