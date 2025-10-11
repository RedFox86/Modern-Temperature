package net.redfox.moderntemperature.util;

public class MathHelper {
  public static float roundToOneDecimal(float number) {
    return Math.round(number * 10.0F) / 10.0F;
  }

  public static float roundToOneDecimal(double number) {
    return Math.round(number * 10.0F) / 10.0F;
  }
}
