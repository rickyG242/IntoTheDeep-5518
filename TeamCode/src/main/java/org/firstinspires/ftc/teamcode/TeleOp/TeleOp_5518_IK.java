package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import android.util.Log;

import org.firstinspires.ftc.teamcode.Libs.AR.MecanumDrive_5518;

@TeleOp(name = "TeleOp_5518_IK", group = "TeleOp")
public class TeleOp_5518_IK extends LinearOpMode
{
    private MecanumDrive_5518 mecanumDrive;
    public static final int DEPLOY = 3, GRAB = 2, ACTIVE = 1, START = 0, point0 = 4, point1 = 5, point2 = 6;
    public static int currentState = 0, lastState = 0;
    // Ideal Final State Machine: private int[] stateMachine = {START, ACTIVE, point0, DEPLOY, ACTIVE, point1};
    private int[] stateMachine = {START, ACTIVE, DEPLOY, ACTIVE};
    public static boolean activateFuzzyTunerJoint1 = false, activateFuzzyTunerJoint2 = false;
    private DcMotor shoulderMotor;
    private DcMotor elbowMotor;
    private final double L1 = 22.0; // Length of first arm segment
    private final double L2 = 16.0; // Length of second arm segment


    static double activeX = 17;
    static double activeY = 6;

    static double grabX = 29; // Prior Val:
    static double grabY = 30; // Prior Val: 12
    public double deployX=1; // Place-holding the deploy position x-coordinate
    public double deployY=-36; // Place-holding the deploy position y-coordinate

    public double deployXLow=6; // Place-holding the deploy position x-coordinate
    public double deployYLow=-37; // Place-holding the deploy position y-coordinate
    static int startJoint1 = -40;
    static int startJoint2 = 0;
    private CRServo leftGripper;
    private CRServo rightGripper;


    //@Override
    public void runOpMode()
    {
        // Initialize the drivetrain
        shoulderMotor = hardwareMap.get(DcMotor.class, "first_joint");
        elbowMotor = hardwareMap.get(DcMotor.class, "second_joint");
        leftGripper = hardwareMap.crservo.get("left_gripper");
        rightGripper = hardwareMap.crservo.get("right_gripper");
        rightGripper.setDirection(DcMotorSimple.Direction.REVERSE);
        mecanumDrive = new MecanumDrive_5518(this);
        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive())
        {
            //telemetry.addData("currentState", currentState);
            //telemetry.addData("lastState", lastState);
            // This call is made every loop and will read the current control pad values (for driving)
            // and update the drivetrain with those values.
            //mecanumDrive.drive();
            if (gamepad2.dpad_up) {
                calculateJointAngles(shoulderMotor, elbowMotor, deployX, deployY);
            }
            if (gamepad2.dpad_right) {
                calculateJointAngles(shoulderMotor, elbowMotor, activeX, activeY);
                Log.i("FTC", "EXIT");
            }
            if (gamepad2.dpad_left) {
                calculateJointAngles(shoulderMotor, elbowMotor, startJoint1, startJoint2);
            }
            if (gamepad2.dpad_down) {
                calculateJointAngles(shoulderMotor, elbowMotor, grabX, grabY);
            }
            // Set this up: drivetrain.activateDriveTrainState();
            mecanumDrive.drive();
            //arm.activateArmState();

            if (gamepad1.left_trigger != 0) {
                mecanumDrive.setBoost(1);
            }
            else {
                mecanumDrive.setBoost(0.5);
            }

            if (gamepad2.left_trigger > 0.1) {
                grab();
            } else if (gamepad2.right_trigger > 0.1){
                drop();
            }
            else{
                rest();
            }

            //**************************************************************************************
            // ---------------------Gamepad 2 Controls ---------------------------------------------

            //**************************************************************************************
            //--------------------- TELEMETRY Code -------------------------------------------------
            // Useful telemetry data in case needed for testing and to find heading of robot
            //mecanumDrive.getTelemetryData();
        }
    }
    private void calculateJointAngles(DcMotor shoulderMotor, DcMotor elbowMotor, double x, double y) {
        Log.i("FTC", "ENTER calcJointAngles");
        double distance = Math.sqrt(x * x + y * y);
        // Check if the target is reachable
        if (distance > (L1 + L2) || distance < Math.abs(L1 - L2)){
            return; // Target is unreachable
        }
        // Law of cosines for elbow angle
        double cosTheta2 = (x * x + y * y - L1 * L1 - L2 * L2) / (2 * L1 * L2);
        if (cosTheta2 < -1 || cosTheta2 > 1) {
            return;
        }
        double theta2 = 0.0;
        try {
            theta2 = Math.acos(cosTheta2); // Elbow angle in radians
        } catch(Exception e){
            theta2 = 35.51;
        }
        // Law of cosines and trigonometry for shoulder angle
        double theta1 = Math.atan2(y, x) - Math.atan2(L2 * Math.sin(theta2), L1 + L2 * Math.cos(theta2));
        // Convert radians to degrees
        double shoulderAngle = Math.toDegrees(theta1);
        double elbowAngle = -(180-Math.toDegrees(theta2));
        Log.i("FTC", "EXIT calcJointAngles");
        // Move the motors to the calculated angle
        moveMotorToAngle(shoulderMotor, shoulderAngle);
        moveMotorToAngle(elbowMotor, elbowAngle);
        Log.i("FTC", "COMPLETE calcJointAngles");
    }
    private void moveMotorToAngle(DcMotor motor, double angle) {

        // Convert the angle to encoder counts (adjust based on motor setup)

        int targetPosition = (int) (angle * (5281.1 / 360.0)); // Example: 1440 counts per revolution

        motor.setTargetPosition(targetPosition);

        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motor.setPower(0.5); // Set motor power
    }


    public void grab()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(-.5);
        rightGripper.setPower(-.5);
    }
    public void drop()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(.5);
        rightGripper.setPower(.5);
    }
    public void rest()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(0);
        rightGripper.setPower(0);
    }
}
