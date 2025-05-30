package org.firstinspires.ftc.teamcode.qubit.core;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/**
 * A cache enabled (performant) DcMotorEx implementation.
 */
public class FtcMotor implements DcMotorEx {
  public static final double MIN_POWER = -1.0;
  public static final double MAX_POWER = 1.0;
  public static final double ZERO_POWER = 0.0;
  private final DcMotorEx motor;

  // PERFORMANCE
  // Motor power writes takes about 4.1 ms.
  // Use a simple and effective motor power caching mechanism.
  private double currentPower = 0;

  public FtcMotor(DcMotorEx motor) {
    this.motor = motor;
  }

  @Override
  public void setMotorEnable() {
    motor.setMotorEnable();
  }

  @Override
  public void setMotorDisable() {
    motor.setMotorDisable();
  }

  @Override
  public boolean isMotorEnabled() {
    return motor.isMotorEnabled();
  }

  @Override
  public void setVelocity(double angularRate) {
    motor.setVelocity(angularRate);
  }

  @Override
  public void setVelocity(double angularRate, AngleUnit unit) {
    motor.setVelocity(angularRate, unit);
  }

  @Override
  public double getVelocity() {
    return motor.getVelocity();
  }

  @Override
  public double getVelocity(AngleUnit unit) {
    return motor.getVelocity(unit);
  }

  @Override
  public void setPIDCoefficients(RunMode mode, PIDCoefficients pidCoefficients) {
  }

  @Override
  public void setPIDFCoefficients(RunMode mode, PIDFCoefficients pidfCoefficients) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setVelocityPIDFCoefficients(double p, double i, double d, double f) {
    motor.setVelocityPIDFCoefficients(p, i, d, f);
  }

  @Override
  public void setPositionPIDFCoefficients(double p) {
    motor.setPositionPIDFCoefficients(p);
  }

  @Deprecated
  @Override
  public PIDCoefficients getPIDCoefficients(RunMode mode) {
    return motor.getPIDCoefficients(mode);
  }

  @Override
  public PIDFCoefficients getPIDFCoefficients(RunMode mode) {
    return motor.getPIDFCoefficients(mode);
  }

  @Override
  public void setTargetPositionTolerance(int tolerance) {
    motor.setTargetPositionTolerance(tolerance);
  }

  @Override
  public int getTargetPositionTolerance() {
    return motor.getTargetPositionTolerance();
  }

  @Override
  public double getCurrent(CurrentUnit unit) {
    return motor.getCurrent(unit);
  }

  @Override
  public double getCurrentAlert(CurrentUnit unit) {
    return motor.getCurrentAlert(unit);
  }

  @Override
  public void setCurrentAlert(double current, CurrentUnit unit) {
    motor.setCurrentAlert(current, unit);
  }

  @Override
  public boolean isOverCurrent() {
    return motor.isOverCurrent();
  }

  @Override
  public MotorConfigurationType getMotorType() {
    return motor.getMotorType();
  }

  @Override
  public void setMotorType(MotorConfigurationType motorType) {
    motor.setMotorType(motorType);
  }

  @Override
  public DcMotorController getController() {
    return motor.getController();
  }

  @Override
  public int getPortNumber() {
    return motor.getPortNumber();
  }

  @Override
  public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
    motor.setZeroPowerBehavior(zeroPowerBehavior);
  }

  @Override
  public ZeroPowerBehavior getZeroPowerBehavior() {
    return motor.getZeroPowerBehavior();
  }

  @Deprecated
  @Override
  public void setPowerFloat() {
    motor.setPowerFloat();
  }

  @Override
  public boolean getPowerFloat() {
    return motor.getPowerFloat();
  }

  @Override
  public void setTargetPosition(int position) {
    motor.setTargetPosition(position);
  }

  @Override
  public int getTargetPosition() {
    return motor.getTargetPosition();
  }

  @Override
  public boolean isBusy() {
    return motor.isBusy();
  }

  @Override
  public int getCurrentPosition() {
    return motor.getCurrentPosition();
  }

  @Override
  public void setMode(RunMode mode) {
    motor.setMode(mode);
  }

  @Override
  public RunMode getMode() {
    return motor.getMode();
  }

  @Override
  public void setDirection(Direction direction) {
    motor.setDirection(direction);
  }

  @Override
  public Direction getDirection() {
    return motor.getDirection();
  }

  @Override
  public void setPower(double power) {
    if (!FtcUtils.areEqual(currentPower, power, FtcUtils.EPSILON3)) {
      motor.setPower(power);
      currentPower = power;
    }
  }

  @Override
  public double getPower() {
    return currentPower;
  }

  @Override
  public Manufacturer getManufacturer() {
    return motor.getManufacturer();
  }

  @Override
  public String getDeviceName() {
    return motor.getDeviceName();
  }

  @Override
  public String getConnectionInfo() {
    return motor.getConnectionInfo();
  }

  @Override
  public int getVersion() {
    return motor.getVersion();
  }

  @Override
  public void resetDeviceConfigurationForOpMode() {
    motor.resetDeviceConfigurationForOpMode();
  }

  @Override
  public void close() {
    motor.close();
  }
}
