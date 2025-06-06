package org.firstinspires.ftc.teamcode.qubit.core;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * A class to manage a color sensor.
 */
public class FtcColorSensor extends FtcSubSystemBase {
  private static final String TAG = "FtcColorSensor";
  private static final String COLOR_SENSOR_NAME = "colorSensor";
  private static final float GAIN_HIGH_THRESHOLD = 0.80F;
  private static final float GAIN_LOW_THRESHOLD = 0.20F;
  public boolean colorSensorEnabled = true;
  public boolean telemetryEnabled = true;
  private Telemetry telemetry;
  private NormalizedColorSensor colorSensor;

  // Sensor gain is calculated dynamically.
  private float gain;
  private final float[] hsvValues = new float[3];
  public NormalizedRGBA rgbaColors = new NormalizedRGBA();
  public float hue, saturation, value;
  public double distance = -1F;

  public void init(HardwareMap hardwareMap, Telemetry telemetry) {
    FtcLogger.enter();
    this.telemetry = telemetry;

    /*
     * You can give the sensor a gain value, will be multiplied by the sensor's raw value
     * before the normalized color values are calculated. Color sensors (especially the REV
     * Color Sensor V3) can give very low values (depending on the lighting conditions),
     * which only use a small part of the 0-1 range that is available for the red, green,
     * and blue values. In brighter conditions, you should use a smaller gain than in dark
     * conditions. If your gain is too high, all of the colors will report at or near 1,
     * and you won't be able to determine what color you are actually looking at. For this
     * reason, it's better to err on the side of a lower gain (but always greater than or
     * equal to 1).
     */
    gain = 1;

    if (colorSensorEnabled) {
      colorSensor = hardwareMap.get(NormalizedColorSensor.class, COLOR_SENSOR_NAME);
      telemetry.addData(TAG, "initialized");
    } else {
      telemetry.addData(TAG, "not enabled");
    }

    FtcLogger.exit();
  }

  public void enableLight() {
    if (colorSensorEnabled && colorSensor != null && colorSensor instanceof SwitchableLight) {
      ((SwitchableLight) colorSensor).enableLight(true);
    }
  }

  public void disableLight() {
    if (colorSensorEnabled && colorSensor != null && colorSensor instanceof SwitchableLight) {
      ((SwitchableLight) colorSensor).enableLight(false);
    }
  }

  public void read() {
    // Get the normalized colors from the sensor
    rgbaColors = colorSensor.getNormalizedColors();

    // Update the hsvValues array
    Color.colorToHSV(rgbaColors.toColor(), hsvValues);
    hue = hsvValues[0];
    saturation = hsvValues[1];
    value = hsvValues[2];

    if (colorSensor instanceof DistanceSensor) {
      distance = ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM);
    }

    // Adjust gain for next reading.
    // Adjust gain so that RGB values are in [GAIN_LOW_THRESHOLD, GAIN_HIGH_THRESHOLD] range
    float max = Math.max(Math.max(rgbaColors.red, rgbaColors.green), rgbaColors.blue);
    if (max > GAIN_HIGH_THRESHOLD) {
      gain = GAIN_HIGH_THRESHOLD / max;
    } else if (max < GAIN_LOW_THRESHOLD) {
      gain = GAIN_LOW_THRESHOLD / max;
    }

    colorSensor.setGain(gain);
  }

  /**
   * Displays telemetry. Helps with debugging.
   */
  public void showTelemetry() {
    FtcLogger.enter();
    if (colorSensorEnabled && telemetryEnabled) {
      telemetry.addData("Gain", gain);
      telemetry.addData("RGBA", "%.3f, %.3f, %.3f, %.3f",
          rgbaColors.red, rgbaColors.green, rgbaColors.blue, rgbaColors.alpha);
      telemetry.addData("HSV", "%.3f, %.3f, %.3f", hue, saturation, value);
      telemetry.addData("Distance", "%.1f cm", distance);
    }

    FtcLogger.exit();
  }
}
