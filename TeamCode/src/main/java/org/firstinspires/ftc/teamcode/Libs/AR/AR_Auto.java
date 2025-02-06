package org.firstinspires.ftc.teamcode.Libs.AR;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

public class AR_Auto extends LinearOpMode{
    private int[] stateMachine = {START, ACTIVE, point0, DEPLOY, ACTIVE, point1};
    public static int DEPLOY = 3;
    public static int GRAB = 2;
    public static int ACTIVE = 1;
    public static int START = 0; // Assuming Start-Position
    public int currentState = 0;
    public int lastState = 0;

    public static int point0 = 4;
    public static int point1 = 5;
    public static int point2 = 6;
    LinearOpMode iBot;
    AR_Arm_IK arm = new AR_Arm_IK(iBot);
    AutonomousDrivetrain drivetrain = new AutonomousDrivetrain(iBot);


    public void runOpMode(){
        // Iterate the following loop through the sequence of states.
        int iterator = 1;
        while (opModeIsActive() && currentState < stateMachine.length) {
            currentState = stateMachine[currentState];
            // State Transition Method based on State Input
            stateTransition(currentState);
            // Update the robot's position
            arm.updateArmPos();
            currentState = currentState + iterator;
            // Display telemetry for debugging
            telemetry.update();
            sleep(100); // Add a short delay to prevent too fast looping
        }
    }

    public void stateTransition(int currentState){
        if (currentState == START) {
            telemetry.addData("State", "START");
            arm.setArmStartPos();
            // Transition to ACTIVE after startup
        }
        if (currentState == ACTIVE) {
            telemetry.addData("State", "ACTIVE");
            // TODO: Add logic here to determine if the arm should go to GRAB or DEPLOY
            arm.setArmActivePos();
            // Example: transition to GRAB after being active
        }
        if (currentState == GRAB) {
            telemetry.addData("State", "GRAB");
            arm.grab(); // Start gripping
            arm.setArmGrabPos();
            // Move to DEPLOY position after grabbing
        }
        if (currentState == DEPLOY) {
            telemetry.addData("State", "DEPLOY");
            // TODO: Deploy or perform actions like releasing the object
            arm.drop(); // Drop object or complete the task
            arm.setArmDeployPos();
            // Return to START position after task is complete
        }
        if (currentState == point0) {
            telemetry.addData("State", "point0");
            drivetrain.setPoint0(iBot);
            // Transition to ACTIVE after startup
        }
        if (currentState == point1) {
            telemetry.addData("State", "point1");
            // TODO: Add logic here to determine if the arm should go to GRAB or DEPLOY
            drivetrain.setPoint1(iBot);
            telemetry.addData("Path", "Complete");
            telemetry.update();
            // Example: transition to GRAB after being active
        }
        if (currentState == point2) {
            telemetry.addData("State", "point2");
            drivetrain.setPoint2(iBot);
            // Move to DEPLOY position after grabbing
        }
    }
}
 
