package org.firstinspires.ftc.teamcode.Libs.AR;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AR_Test_IK", group = "Linear Opmode")

public class AR_Test_IK extends LinearOpMode {
    // Hardware variables
    private DcMotor shoulderMotor;
    private DcMotor elbowMotor;

    // Arm dimensions (in some consistent units, e.g., cm)
    private final double L1 = 22.0; // Length of first arm segment
    private final double L2 = 16.0; // Length of second arm segment

    static double activeX = 17;
    static double activeY = 6;

    static double grabX = 30;
    static double grabY = 14;
    public double deployX=2; // Place-holding the deploy position x-coordinate
    public double deployY=-35; // Place-holding the deploy position y-coordinate

    public double deployXLow=6; // Place-holding the deploy position x-coordinate
    public double deployYLow=-37; // Place-holding the deploy position y-coordinate
    static int startJoint1 = -40;
    static int startJoint2 = 0;
    private CRServo leftGripper;
    private CRServo rightGripper;

    @Override

    public void runOpMode() {
        // Initialize motors
        shoulderMotor = hardwareMap.get(DcMotor.class, "first_joint");
        elbowMotor = hardwareMap.get(DcMotor.class, "second_joint");
        leftGripper = hardwareMap.crservo.get("left_gripper");
        rightGripper = hardwareMap.crservo.get("right_gripper");
        // Wait for the start button
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Set up the arm to the start position from its folded form
            calculateJointAngles(shoulderMotor, elbowMotor, grabX, grabY);
            sleep(500);
            calculateJointAngles(shoulderMotor, elbowMotor, activeX, activeY);
            sleep(500);
            calculateJointAngles(shoulderMotor, elbowMotor, deployX, deployY);
            sleep(500);
            drop();
            sleep(3000);


            // Calculate joint angles using inverse kinematics
                // Display telemetry
                telemetry.addData("Target X", activeX);
                telemetry.addData("Target Y", activeY);
                telemetry.update();

        }
    }

    private void calculateJointAngles(DcMotor shoulderMotor, DcMotor elbowMotor, double x, double y) {

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

        // Move the motors to the calculated angle
        moveMotorToAngle(shoulderMotor, shoulderAngle);
        moveMotorToAngle(elbowMotor, elbowAngle);
        sleep(3000);
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
        leftGripper.setPower(-1);
        rightGripper.setPower(-1);
    }
    public void drop()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(1);
        rightGripper.setPower(1);
    }
    public void rest()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(0);
        rightGripper.setPower(0);
    }
}

