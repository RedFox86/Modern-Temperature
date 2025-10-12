package net.redfox.moderntemperature.event;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
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
    public static void onRegisterGuiOverlaysEvent(AddGuiOverlayLayersEvent event) {
      ForgeLayeredDraw draw = event.getLayeredDraw();
      draw.addAbove(ForgeLayeredDraw.DEBUG_OVERLAY,
          ResourceLocation.fromNamespaceAndPath(ModernTemperature.MOD_ID, "temperature_gauge"),
          ((pGuiGraphics, pDeltaTracker) -> {
            
          })
          );
    }
  }
}
