package org.firstinspires.ftc.teamcode.qubit.autoOps;

import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.PathCallback;
import com.pedropathing.pathgen.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import org.firstinspires.ftc.teamcode.qubit.core.FtcBot;
import org.firstinspires.ftc.teamcode.qubit.core.FtcImu;
import org.firstinspires.ftc.teamcode.qubit.core.FtcLift;
import org.firstinspires.ftc.teamcode.qubit.core.FtcLogger;
import org.firstinspires.ftc.teamcode.qubit.core.FtcUtils;
import org.firstinspires.ftc.teamcode.qubit.core.enumerations.DriveTrainEnum;
import org.firstinspires.ftc.teamcode.qubit.core.enumerations.DriveTypeEnum;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A base class to provide common variables and methods.
 */
public class OptionBase {
  protected static final double RADIAN0;
  protected static final double RADIAN15;
  protected static final double RADIAN20;
  protected static final double RADIAN30;
  protected static final double RADIAN45;
  protected static final double RADIAN60;
  protected static final double RADIAN75;
  protected static final double RADIAN90;
  protected static final double RADIAN105;
  protected static final double RADIAN120;
  protected static final double RADIAN135;
  protected static final double RADIAN150;
  protected static final double RADIAN180;
  protected LinearOpMode autoOpMode;
  protected FtcBot robot;
  protected Follower follower;
  protected final Pose startPose = new Pose(0, 0, 0);

  protected Runnable lift2HighBasket, lift2HighBasketBlocking,
      lift2HighChamber, lift2HighChamberBlocking, lift2HighChamberDeliveryBlocking,
      lift2Low, resetLift;
  protected Runnable intakeSpinIn, intakeSpinOut, intakeSpinStop;
  protected Runnable intakeFlipDown, intakeFlipDelivery, intakeFlipHorizontal;
  protected Runnable grabLeftSpecimen, releaseLeftSpecimen, grabRightSpecimen, releaseRightSpecimen;

  static {
    RADIAN0 = Math.toRadians(0);
    RADIAN15 = Math.toRadians(15);
    RADIAN20 = Math.toRadians(20);
    RADIAN30 = Math.toRadians(30);
    RADIAN45 = Math.toRadians(45);
    RADIAN60 = Math.toRadians(60);
    RADIAN75 = Math.toRadians(75);
    RADIAN90 = Math.toRadians(90);
    RADIAN105 = Math.toRadians(105);
    RADIAN120 = Math.toRadians(120);
    RADIAN135 = Math.toRadians(135);
    RADIAN150 = Math.toRadians(150);
    RADIAN180 = Math.toRadians(179.99);
  }

  /**
   * A Base AutoOp option class to hold all the common variables.
   *
   * @param autoOpMode The autoOp itself.
   * @param robot      The robot object to execute robot actions.
   * @param follower   The PP follower to execute trajectories.
   */
  public OptionBase(LinearOpMode autoOpMode, FtcBot robot, Follower follower) {
    this.autoOpMode = autoOpMode;
    this.robot = robot;
    this.follower = follower;

    lift2HighBasket = () -> robot.lift.move(FtcLift.POSITION_HIGH_BASKET, FtcLift.POSITION_HIGH_BASKET, false);
    lift2HighBasketBlocking = () -> robot.lift.move(FtcLift.POSITION_HIGH_BASKET, FtcLift.POSITION_HIGH_BASKET, true);
    lift2HighChamber = () -> robot.lift.move(FtcLift.POSITION_HIGH_CHAMBER, FtcLift.POSITION_HIGH_CHAMBER, false);
    lift2HighChamberBlocking = () -> robot.lift.move(FtcLift.POSITION_HIGH_CHAMBER, FtcLift.POSITION_HIGH_CHAMBER, true);
    lift2HighChamberDeliveryBlocking = () -> robot.lift.move(FtcLift.POSITION_HIGH_CHAMBER_DELIVERY, FtcLift.POSITION_HIGH_CHAMBER_DELIVERY, true);
    lift2Low = () -> robot.lift.move(FtcLift.POSITION_FLOOR, FtcLift.POSITION_FLOOR, false);
    resetLift = () -> robot.lift.resetLiftIfTouchPressed();

    intakeSpinIn = () -> robot.intake.spinIn(false);
    intakeSpinOut = () -> robot.intake.spinOut(false);
    intakeSpinStop = () -> robot.intake.spinStop();

    intakeFlipDown = () -> robot.intake.flipDown(false);
    intakeFlipDelivery = () -> robot.intake.flipDelivery(false);
    intakeFlipHorizontal = () -> robot.intake.flipHorizontal(false);

    grabLeftSpecimen = () -> robot.intake.leftSpecimenGrab(true);
    releaseLeftSpecimen = () -> robot.intake.leftSpecimenRelease();
    grabRightSpecimen = () -> robot.intake.rightSpecimenGrab(true);
    releaseRightSpecimen = () -> robot.intake.rightSpecimenRelease();
  }

  /**
   * Executes the path chain.
   *
   * @param pathChain The pathChain to execute
   * @param holdEnd   When true, attempts to hold the path end point. This flag is
   *                  passed onto the pathChain follower.
   * @param timeout   A user provided timeout for path execution. Path execution is
   *                  terminated if robot doesn't finish within the timeout value.
   */
  public void runFollower(PathChain pathChain, boolean holdEnd, long timeout) {
    FtcLogger.enter();
    ElapsedTime runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    follower.followPath(pathChain, holdEnd);
    if (timeout < 0) timeout = Long.MAX_VALUE;
    Deadline d = new Deadline(timeout, TimeUnit.MILLISECONDS);
    do {
      follower.update();
    } while (autoOpMode.opModeIsActive() && !d.hasExpired() && follower.isBusy());
    if (follower.isBusy()) follower.breakFollowing();
    String message = String.format(Locale.US, "%s execution: %.0f ms",
        pathChain.name, runtime.milliseconds());
    FtcLogger.info(FtcUtils.TAG, message);
    autoOpMode.telemetry.addData(FtcUtils.TAG, message);
    autoOpMode.telemetry.update();
    FtcLogger.exit();
  }

  /**
   * A helper method to test if the autoOp is active. When autoOp is inactive,
   * stores the lift and gyro values for use in TeleOp.
   *
   * @return
   */
  public boolean saveAndTest() {
    FtcLogger.enter();
    boolean opModeIsActive;
    if (autoOpMode.opModeIsActive()) {
      opModeIsActive = true;
    } else {
      // Save settings for use by TeleOp
      FtcLift.endAutoOpLeftLiftPosition = robot.lift.getLeftPosition();
      FtcLift.endAutoOpRightLiftPosition = robot.lift.getRightPosition();
      if (robot.driveTrain.driveTrainEnum == DriveTrainEnum.MECANUM_WHEEL_DRIVE &&
          robot.driveTrain.driveTypeEnum == DriveTypeEnum.FIELD_ORIENTED_DRIVE) {
        robot.imu.read();
        FtcImu.endAutoOpHeading = robot.imu.getHeading();
      }

      opModeIsActive = false;
    }

    FtcLogger.exit();
    return opModeIsActive;
  }

  public void runCallbacks(PathChain pathChain, ElapsedTime pathChainElapsedTime) {
    for (PathCallback callback : pathChain.getCallbacks()) {
      if (!callback.hasBeenRun() && callback.getType() == PathCallback.TIME &&
          pathChainElapsedTime.milliseconds() >= callback.getStartCondition()) {
        callback.run();
      }
    }
  }
}
