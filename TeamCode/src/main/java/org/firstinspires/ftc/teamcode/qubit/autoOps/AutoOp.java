/* Copyright (c) 2024 The Qubit Bot. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.qubit.autoOps;

import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.localization.localizers.PinpointLocalizer;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.pedropathing.follower.Follower;
import org.firstinspires.ftc.teamcode.qubit.core.FtcBot;
import org.firstinspires.ftc.teamcode.qubit.core.FtcImu;
import org.firstinspires.ftc.teamcode.qubit.core.FtcLift;
import org.firstinspires.ftc.teamcode.qubit.core.FtcLogger;
import org.firstinspires.ftc.teamcode.qubit.core.FtcUtils;
import org.firstinspires.ftc.teamcode.qubit.core.enumerations.RobotPositionEnum;

@Autonomous(group = "Official", preselectTeleOp = "DriverTeleOp")
public class AutoOp extends LinearOpMode {
    private ElapsedTime runtime = null;
    FtcBot robot = null;
    Follower follower;
    OptionBase optionBase;
    OptionLeft optionLeft;
    OptionRight optionRight;

    @Override
    public void runOpMode() {
        FtcLogger.enter();
        initializeModules();
        processStuffDuringInit();
        executeAutonomousOperation();
        waitForEnd();
        FtcLogger.exit();
    }

    /**
     * Invokes autonomous operation based on match configuration.
     */
    private void executeAutonomousOperation() {
        FtcLogger.enter();
        runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        if (robot.config.delayInSeconds > 0) {
            long countDown = robot.config.delayInSeconds;
            while (countDown > 0) {
                if (!opModeIsActive()) return;
                telemetry.addData(FtcUtils.TAG, "Delaying start by %d seconds",
                        robot.config.delayInSeconds);
                telemetry.addData(FtcUtils.TAG, "Countdown %d seconds", countDown);
                telemetry.update();
                FtcUtils.sleep(1000);
                countDown--;
            }
        }

        if (!opModeIsActive()) return;
        telemetry.addData(FtcUtils.TAG, "Auto Op started.");
        telemetry.update();
        if (!opModeIsActive()) return;

        // Enable and reset servos
        robot.start();

        if (robot.config.robotPosition == RobotPositionEnum.LEFT) {
            optionLeft.execute();
        } else {
            optionRight.execute();
        }

        FtcLogger.exit();
    }

    /**
     * Initialize the variables, etc.
     */
    private void initializeModules() {
        FtcLogger.enter();
        telemetry.addData(FtcUtils.TAG, "Initializing. Please wait...");
        telemetry.update();

        // Clear out any previous end heading of the robot.
        FtcImu.endAutoOpHeading = 0;
        FtcLift.endAutoOpLeftLiftPosition = FtcLift.POSITION_MINIMUM;
        FtcLift.endAutoOpRightLiftPosition = FtcLift.POSITION_MINIMUM;

        // Initialize robot.
        robot = new FtcBot();
        robot.init(hardwareMap, telemetry, true);
        robot.blinkinLed.set(RevBlinkinLedDriver.BlinkinPattern.BLACK);
        robot.intake.spinStop();
        robot.rnp.stop(false);
        robot.intake.rightSpecimenRelease();
        if (robot.config.robotPosition == RobotPositionEnum.RIGHT) {
            robot.intake.leftSpecimenGrab(false);
        } else {
            robot.intake.leftSpecimenRelease();
        }

        if (FtcUtils.DEBUG) {
            robot.enableTelemetry();
        } else {
            // Disable telemetry to speed things up.
            robot.disableTelemetry();
        }

        // Initialize Pedro for robot paths and trajectories
        // Must initialize this after robot.driveTrain initialization since driveTrain
        // sets the motors to run without encoders.
        follower = new Follower(hardwareMap, FConstants.class, LConstants.class);
        if (robot.config.robotPosition == RobotPositionEnum.LEFT) {
            optionLeft = new OptionLeft(this, robot, follower).init();
            optionBase = optionLeft;
        } else {
            optionRight = new OptionRight(this, robot, follower).init();
            optionBase = optionRight;
        }

        FtcLogger.exit();
    }

    private void processStuffDuringInit() {
        FtcLogger.enter();

        while (opModeInInit()) {
            robot.config.showConfiguration();
            telemetry.addLine();
            if (robot.imu.isGyroDrifting()) {
                telemetry.addLine();
                telemetry.addData(FtcUtils.TAG, "Gyro %.1f is DRIFTING! STOP and ReInitialize.",
                        robot.imu.getHeading());
            }

            telemetry.addData(FtcUtils.TAG, "Waiting for driver to press play.");
            telemetry.update();
            FtcUtils.sleep(FtcUtils.CYCLE_MS);
        }

        FtcLogger.exit();
    }


    /**
     * Waits for the autonomous operation to end.
     * If using FOD, saves gyro Heading for use by TeleOp.
     */
    private void waitForEnd() {
        FtcLogger.enter();

        double autoOpExecutionDuration = 0;
        if (runtime != null) {
            autoOpExecutionDuration = runtime.seconds();
        }

        while (optionBase != null && optionBase.saveAndTest()) {
            telemetry.addData(FtcUtils.TAG, "endGyro=%.1f, endLeftLift=%d, endRightLift=%d",
                    FtcImu.endAutoOpHeading, FtcLift.endAutoOpLeftLiftPosition, FtcLift.endAutoOpRightLiftPosition);
            telemetry.addData(FtcUtils.TAG, "Auto Op took %.0f seconds.", autoOpExecutionDuration);
            telemetry.addData(FtcUtils.TAG, "Waiting for auto Op to end.");
            telemetry.update();
            FtcUtils.sleep(FtcUtils.CYCLE_MS);
        }

        robot.stop();
        FtcLogger.exit();
    }
}
