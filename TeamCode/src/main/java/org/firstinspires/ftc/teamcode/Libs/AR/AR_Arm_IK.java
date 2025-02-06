package org.firstinspires.ftc.teamcode.Libs.AR;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.ArrayList;
import java.util.List;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

public class AR_Arm_IK{
    // Arm dimensions (in some consistent units, e.g., cm)
    public final double L1 = 22.0; // **NOT MEASURED** Length of first arm segment
    public final double L2 = 16.0; // **NOT MEASURED** Length of second arm segment
    private DcMotor shoulderMotor;
    private DcMotor elbowMotor;
    private int angle1;
    private int angle2;
    public final AR_Joint joint1;
    public final AR_Joint joint2;
    public final CRServo leftGripper;
    public final CRServo rightGripper;
    public final LinearOpMode bot;
    // PID Values From AR_Arm; To Be Tuned
    public static double P1 = 0.003, I1 = 0.05, D1 = 0.0001;
    public static double F1 = 0.05;
    public static double P2 = 0.001, I2 = 0.05, D2 = 0.0001;
    public static double F2 = 0.05;
    public static int startJointAngle1 = -40; //degrees
    public static int startJointAngle2 = 0; //degrees
    public double activeTargetX=0; // Place-holding the active position x-coordinate
    public double activeTargetY=0; // Place-holding the active position y-coordinate
    public double grabTargetX=0; // Place-holding the grab position x-coordinate
    public double grabTargetY=0; // Place-holding the grab position y-coordinate
    public double deployTargetX=0; // Place-holding the deploy position x-coordinate
    public double deployTargetY=0; // Place-holding the deploy position y-coordinate
    private int lastState = AR_Auto.START; // Existing from prior states in AR_Arm
    private int currentState = AR_Auto.START; // Existing from prior states in AR_Arm
    public AR_Arm_IK(LinearOpMode iBot){
        // Based upon AR_Arm method from AR_Arm class
        this.bot = iBot;
        this.joint1 = new AR_Joint(this.bot, "first_joint", P1, I1, D1, F1);
        this.joint2 = new AR_Joint(this.bot, "second_joint", P2, I2, D2, F2);
        this.leftGripper = bot.hardwareMap.crservo.get("left_gripper");
        this.rightGripper = bot.hardwareMap.crservo.get("right_gripper");
        this.shoulderMotor = bot.hardwareMap.get(DcMotor.class, "first_joint");
        this.elbowMotor = bot.hardwareMap.get(DcMotor.class, "second_joint");
    }

    public void calculateJointAngles(double x, double y) {
        double distance = Math.sqrt(x * x + y * y);
        // Check if the target is reachable
        if (distance > (L1 + L2) || distance < Math.abs(L1 - L2)) {
            angle1=0; // Target is unreachable
            angle2=0;
        }
        // Law of cosines for elbow angle
        double cosTheta2 = (x * x + y * y - L1 * L1 - L2 * L2) / (2 * L1 * L2);
        double theta2 = Math.acos(cosTheta2); // Elbow angle in radians
        // Law of cosines and trigonometry for shoulder angle
        double theta1 = Math.atan2(y, x) - Math.atan2(L2 * Math.sin(theta2), L1 + L2 * Math.cos(theta2));
        // Convert radians to degrees
        double shoulderAngle = Math.toDegrees(theta1);
        double elbowAngle = -1 * (180 - Math.toDegrees(theta2));
        // Set angle 1 and angle 2 to useful integers
        angle1 = (int) shoulderAngle;
        angle2 = (int) elbowAngle;
    }

    public void updateArmPos()
    {   // Arm should be tested before adding that code
        this.joint1.moveJoint(angle1, currentState, lastState);
        this.joint2.moveJoint(angle2, currentState, lastState);
    }
    public void setArmCustomPos(int firstJoint, int secondJoint )
    {   // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        angle1 = firstJoint;
        angle2 = secondJoint;}

    public void setArmDeployPos() {
        /* Rest of the methods follow the flow of AR_Arm
        Calculates and sets appropriate angles
        based on current state's x and y coordinates
        */
        // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        calculateJointAngles(deployTargetX, deployTargetY);
        if (currentState != AR_Auto.DEPLOY) {
            lastState = currentState;
            currentState = AR_Auto.DEPLOY;
        }
    }


    public void setArmGrabPos()
    {   // Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        // Calculates and sets joint angles for GRAB state
        calculateJointAngles(grabTargetX, grabTargetY);
        if( currentState != AR_Auto.GRAB ){
            lastState = currentState;
            currentState = AR_Auto.GRAB;}}
    public void setArmActivePos()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        // Calculates and sets joint angles for ACTIVE state
        calculateJointAngles(activeTargetX, activeTargetY);
        if( currentState != AR_Auto.ACTIVE ) {
            lastState = currentState;
            currentState = AR_Auto.ACTIVE;}}
    public void setArmStartPos()
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        // Sets each joint ang. to the rest variables defined from AR_Arm
        angle1 = startJointAngle1;
        angle2 = startJointAngle2;
        if (currentState != AR_Auto.START) {
            lastState = currentState;
            currentState = AR_Auto.START;}}
    public void grab( )
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(1);
        rightGripper.setPower(1);
    }
    public void drop( )
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(-1);
        rightGripper.setPower(-1);
    }
    public void rest( )
    {// Todo: This needs to be carefully tested before we run the code to make sure the motor direction is correct, etc.
        leftGripper.setPower(0);
        rightGripper.setPower(0);
    }
}
 
