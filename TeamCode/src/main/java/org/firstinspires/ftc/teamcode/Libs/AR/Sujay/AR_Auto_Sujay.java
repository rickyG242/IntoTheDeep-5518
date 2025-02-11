package org.firstinspires.ftc.teamcode.Libs.AR.Sujay;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Libs.AR.AR_Arm_Fisher;
import org.firstinspires.ftc.teamcode.Libs.AR.AutonomousDrivetrain;

public class AR_Auto_Sujay extends LinearOpMode {
    LinearOpMode iBot;
    AR_Arm_Fisher arm = new AR_Arm_Fisher(iBot);
    AutonomousDrivetrain drivetrain = new AutonomousDrivetrain(iBot);

    public void runOpMode() {
        while (opModeIsActive()){
            //Start
            drivetrain.turnToHeading(iBot, .5, -90);
            sleep(500);
            drivetrain.driveStraight(iBot, .5, 20, 0);
            //Reach Bucket
            sleep(500);
            deploy();
            getSample(.5,90);
            deploy();
            getSample(6, 70);
            deploy();
            getSample(7, 60);
            deploy();
            getSample(10, 0);
            sleep(2000);
            getSample(5, 70);
            sleep(5000);
        }
    }

    public void deploy(){
        arm.setArmDeployPos();
        arm.updateArmPos();
        sleep(2000);
        arm.drop();
        sleep(1500);
        arm.rest();
    }

    public void getSample (double startTravel, int returnTurn){
        drivetrain.turnToHeading(iBot, .5, returnTurn);
        sleep(500);
        drivetrain.driveStraight(iBot, .5, startTravel, 0);
        arm.grab();
        sleep(500);
        drivetrain.driveStraight(iBot, .5, -startTravel, 0);
        sleep(1500);
        drivetrain.turnToHeading(iBot, .5, -returnTurn);
        sleep(1500);
    }
}
