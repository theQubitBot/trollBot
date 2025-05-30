package org.firstinspires.ftc.teamcode.qubit.core;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.internal.system.Assert;

import java.util.HashMap;
import java.util.Locale;

/**
 * A global utility for managing robot execution logs.
 */
public final class FtcLogger {
  private static final String TAG = "FtcLogger";
  private static ElapsedTime runtime = null;

  // PERFORMANCE
  // Set to false for official runs.
  private static final boolean performanceMetricsEnabled = true;
  private static HashMap<String, Double> performanceMetricsMap = null;

  /* Constructor */
  static {
    if (performanceMetricsEnabled) {
      runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
      performanceMetricsMap = new HashMap<>(64);
    }
  }

  public FtcLogger() {
  }

  public static void debug(String format, Object... args) {
    if (FtcUtils.DEBUG) {
      RobotLog.dd(TAG, format, args);
    }
  }

  public static void enter() {
    if (performanceMetricsEnabled || FtcUtils.DEBUG) {
      StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
      StackTraceElement ste = steArray[3];
      String className = getClassNameOnly(ste.getClassName());
      String methodName = ste.getMethodName();
      String key = String.format("%s.%s", className, methodName);
      if (performanceMetricsEnabled) {
        performanceMetricsMap.put(key, runtime.milliseconds());
      }

      String message = String.format("%s - enter", key);
      RobotLog.dd(TAG, message);
    }
  }

  public static void error(String tag, String format, Object... args) {
    RobotLog.ee(tag, format, args);
  }

  public static void exit() {
    if (performanceMetricsEnabled || FtcUtils.DEBUG) {
      StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
      StackTraceElement ste = steArray[3];
      String className = getClassNameOnly(ste.getClassName());
      String methodName = ste.getMethodName();
      String key = String.format("%s.%s", className, methodName);
      String message = String.format("%s - exit", key);
      if (performanceMetricsEnabled && performanceMetricsMap.containsKey(key)) {
        double methodExecutionTime = runtime.milliseconds() - performanceMetricsMap.get(key);
        message += String.format(Locale.US, " - %.2f ms", methodExecutionTime);
      }

      RobotLog.dd(TAG, message);
    }
  }

  private static String getClassNameOnly(String fullClassName) {
    Assert.assertNotNull(fullClassName, "getClassNameOnly>fullClassName");
    String classNameOnly;
    String[] dataArray = fullClassName.split("\\.");
    if (dataArray.length > 0) {
      classNameOnly = dataArray[dataArray.length - 1];
    } else {
      classNameOnly = fullClassName;
    }

    return classNameOnly;
  }

  public static void info(String tag, String format, Object... args) {
    if (performanceMetricsEnabled || FtcUtils.DEBUG) {
      RobotLog.ii(tag, format, args);
    }
  }
}
