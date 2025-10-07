package net.redfox.moderntemperature.client;

import net.redfox.moderntemperature.math.MathHelper;

public class ClientTemperatureData {
  private static float playerTemperature;

  public static void set(float playerTemperature) {
    ClientTemperatureData.playerTemperature = MathHelper.roundToOneDecimal(playerTemperature);
  }

  public static float getPlayerTemperature() {
    return MathHelper.roundToOneDecimal(playerTemperature);
  }
}
