package org.firstinspires.ftc.teamcode.Auto;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.TeleOp.Test.AR_Arm_Fisher_Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Autonomous(name = "AR_Auto_Sujay_Test", group = "Robot")
public class AR_Auto_Sujay_Test extends LinearOpMode {
    private static final Logger log = LoggerFactory.getLogger(AR_Auto_Sujay_Test.class);
    public static final int DEPLOY = 3, GRAB = 2, ACTIVE = 1, START = 0, point0 = 4, point1 = 5, point2 = 6;
    public static int currentState = 0, lastState = 0;
    // Ideal Final State Machine: private int[] stateMachine = {START, ACTIVE, point0, DEPLOY, ACTIVE, point1};
    private int[] stateMachine = {START, ACTIVE, DEPLOY, ACTIVE};
    private LinearOpMode iBot;
    private AR_Arm_Fisher_Test arm;
    public void runOpMode() {
        iBot = this;
        arm =  new AR_Arm_Fisher_Test(iBot);
        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()){
            deploy();
        }
    }

    public void deploy(){
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        // Deploy Arm
        arm.setArmGrabPos();
        arm.updateArmPos();

        while (timer.milliseconds() < 1500) {
            arm.updateArmPos();  // Keep updating the arm
        }
        Log.i("FTC", "Grabbed");

        /*
        // Move to Active Position
        arm.setArmActivePos();
        arm.updateArmPos();

        // Non-blocking wait instead of sleep(1500)
        timer.reset();
        while (timer.milliseconds() < 1500) {
            arm.updateArmPos();
        }

        // Move to Start Position
        arm.setArmStartPos();
        arm.updateArmPos();
        while (arm.isBusy()) {
            arm.updateArmPos();
        }

        // Non-blocking wait instead of sleep(1000)
        timer.reset();
        while (timer.milliseconds() < 1000) {
            arm.updateArmPos();
        }
        */
        /*
        arm.setArmDeployPos();
        arm.updateArmPos();
        while (arm.isBusy()){
            arm.updateArmPos();
        }
        //arm.drop();
        sleep(1500);
        //arm.rest();
        arm.setArmActivePos();
        arm.updateArmPos();
        while (arm.isBusy()) {
            arm.updateArmPos();
        }
        sleep(1500);
        arm.setArmStartPos();
        arm.updateArmPos();
        while (arm.isBusy()){
            arm.updateArmPos();
        }
        sleep(1000);
         */
    }

    public void getSample (double startTravel, int returnTurn){
        //drivetrain.turnToHeading(iBot, .5, returnTurn);
        sleep(1500);
        //drivetrain.driveStraight(iBot, .5, startTravel, 0);
        //arm.grab();
        sleep(1500);
        //drivetrain.driveStraight(iBot, .5, -startTravel, 0);
        sleep(1500);
        //drivetrain.turnToHeading(iBot, .5, -returnTurn);
        //arm.rest();
        sleep(1500);
    }
}
