package org.firstinspires.ftc.teamcode.Libs.AR;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

public class AR_Auto extends LinearOpMode {
    public static final int DEPLOY = 3, GRAB = 2, ACTIVE = 1, START = 0, point0 = 4, point1 = 5, point2 = 6;
    public static int currentState = 0, lastState = 0;
    // Ideal Final State Machine: private int[] stateMachine = {START, ACTIVE, point0, DEPLOY, ACTIVE, point1};
    private int[] stateMachine = {START, ACTIVE, DEPLOY, ACTIVE};
    public static boolean activateFuzzyTunerJoint1 = false, activateFuzzyTunerJoint2 = false;
    LinearOpMode iBot;
    AR_Arm_IK arm = new AR_Arm_IK(iBot, activateFuzzyTunerJoint1, activateFuzzyTunerJoint2);
    AutonomousDrivetrain drivetrain = new AutonomousDrivetrain(iBot);

    public void runOpMode() {
        int index = 0;
        while (opModeIsActive() && index < stateMachine.length) {
            currentState = stateMachine[index];
            setState(currentState);
            index++;
            if (index >= stateMachine.length) break;
            if (currentState >= 4) drivetrain.activateDriveTrainState();
            else {arm.activateArmState();}
            telemetry.update();
            sleep(3000);
        }
        telemetry.addData("currentState", currentState);
        telemetry.addData("lastState", lastState);
    }

    public void setState(int currentState) {
        switch (currentState) {
            case START: telemetry.addData("State", "START"); arm.setArmStartPos(); break;
            case ACTIVE: telemetry.addData("State", "ACTIVE"); arm.setArmActivePos(); break;
            case GRAB: telemetry.addData("State", "GRAB"); arm.grab(); arm.setArmGrabPos(); break;
            case DEPLOY: telemetry.addData("State", "DEPLOY"); arm.drop(); arm.setArmDeployPos(); break;
            case point0: telemetry.addData("State", "point0"); drivetrain.setPoint0(iBot); break;
            case point1: telemetry.addData("State", "point1"); drivetrain.setPoint1(iBot); telemetry.addData("Path", "Complete"); telemetry.update(); break;
            case point2: telemetry.addData("State", "point2"); drivetrain.setPoint2(iBot); break;
            default: telemetry.addData("State", "Unknown"); break;
        }
    }

}
