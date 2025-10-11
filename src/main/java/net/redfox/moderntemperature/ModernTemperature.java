package net.redfox.moderntemperature;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.redfox.moderntemperature.config.ModernTemperatureCommonConfigs;
import net.redfox.moderntemperature.effect.ModEffects;
import net.redfox.moderntemperature.networking.ModPackets;
import net.redfox.moderntemperature.temperature.PlayerTemperature;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModernTemperature.MOD_ID)
public class ModernTemperature {
  public static final String MOD_ID = "moderntemperature";
  private static final Logger LOGGER = LogUtils.getLogger();

  public ModernTemperature(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    context.registerConfig(
        ModConfig.Type.COMMON,
        ModernTemperatureCommonConfigs.SPEC,
        "moderntemperature-common.toml");
    context.registerConfig(
        ModConfig.Type.CLIENT,
        ModernTemperatureCommonConfigs.SPEC,
        "moderntemperature-client.toml");

    ModEffects.register(modEventBus);

    modEventBus.addListener(this::commonSetup);

    MinecraftForge.EVENT_BUS.register(this);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    ModPackets.register();
    if (!ModernTemperatureCommonConfigs.TEMPERATURE_FLUCTUATION.get())
      PlayerTemperature.hasTemperatureFluctuation = false;
    PlayerTemperature.populateConfigLists();
  }
}
