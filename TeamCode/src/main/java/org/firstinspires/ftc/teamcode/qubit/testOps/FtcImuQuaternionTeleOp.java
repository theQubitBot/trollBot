/* Copyright (c) 2023 Viktor Taylor. All rights reserved.
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

package org.firstinspires.ftc.teamcode.qubit.testOps;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.qubit.core.FtcLogger;
import org.firstinspires.ftc.teamcode.qubit.core.FtcUtils;

@Disabled
@TeleOp(group = "TestOp")
public class FtcImuQuaternionTeleOp extends OpMode {
    // Declare OpMode members
    private ElapsedTime runtime = null;
    private ElapsedTime loopTime = null;
    private IMU imu = null;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        FtcLogger.enter();
        telemetry.addData(">", "Initializing, please wait...");
        telemetry.update();
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        imu.resetYaw();
        FtcLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        telemetry.addData(">", "Waiting for driver to press play");
        telemetry.update();
        FtcUtils.sleep(50);
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        FtcLogger.enter();
        telemetry.addData(">", "Starting...");
        telemetry.update();
        runtime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        FtcLogger.exit();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        FtcLogger.enter();
        loopTime.reset();

        YawPitchRollAngles ypra = imu.getRobotYawPitchRollAngles();
        telemetry.addData(">", "yaw %.0f, roll %.0f, pitch %.0f",
                ypra.getYaw(AngleUnit.DEGREES), ypra.getRoll(AngleUnit.DEGREES), ypra.getPitch(AngleUnit.DEGREES));

        Quaternion q = imu.getRobotOrientationAsQuaternion();
        telemetry.addData(">", "w %.2f, x %.2f, y %.2f, z %.2f",
                q.w, q.x, q.y, q.z);
        Orientation o = imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        telemetry.addData(">", "first %.0f, second %.0f, third %.0f",
                o.firstAngle, o.secondAngle, o.thirdAngle);
        o = q.toOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        telemetry.addData(">", "first %.0f, second %.0f, third %.0f",
                o.firstAngle, o.secondAngle, o.thirdAngle);
        telemetry.addData(">", "Loop %.0f ms, cumulative %.0f seconds",
                loopTime.milliseconds(), runtime.seconds());
        telemetry.update();
        FtcLogger.exit();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        FtcLogger.enter();
        telemetry.addData(">", "Tele Op stopped.");
        telemetry.update();
        FtcLogger.exit();
    }
}