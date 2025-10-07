package net.redfox.moderntemperature.config;

import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModernTemperatureClientConfigs {
  public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final ForgeConfigSpec SPEC;

  public static final ForgeConfigSpec.ConfigValue<String> DISPLAY_MODE;

  static {
    BUILDER.push("Client Configs for Modern Temperature");

    DISPLAY_MODE =
        BUILDER
            .comment("The method in which the mod displays your temperature.")
            .defineInList("defaultMode", "GAUGE", List.of("GAUGE", "NUMBER", "NONE"));

    BUILDER.pop();
    SPEC = BUILDER.build();
  }
}
