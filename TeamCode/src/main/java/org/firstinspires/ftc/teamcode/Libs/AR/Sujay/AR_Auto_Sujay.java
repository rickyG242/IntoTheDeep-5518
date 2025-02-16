package org.firstinspires.ftc.teamcode.Libs.AR.Sujay;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Libs.AR.AR_Arm_Fisher;
import org.firstinspires.ftc.teamcode.Libs.AR.AutonomousDrivetrain;

@Autonomous(name = "AR_Auto_Sujay", group = "Robot")
public class AR_Auto_Sujay extends LinearOpMode {
    private LinearOpMode iBot;
    private AR_Arm_Fisher arm;
    private AutonomousDrivetrain drivetrain;

    public void runOpMode() {
        waitForStart();
        while (opModeIsActive()){
            iBot = this;
            drivetrain = new AutonomousDrivetrain(iBot);
            arm =  new AR_Arm_Fisher(iBot);
            //Start
            drivetrain.turnToHeading(iBot, .5, 90);
            sleep(2000);
            drivetrain.driveStraight(iBot, .5, 20, 0);
            //Reach Bucket
            sleep(2000);
            deploy();
            getSample(.5,90);
            deploy();
            getSample(6, 70);
            //deploy();
            getSample(7, 60);
            //deploy();
            getSample(10, 0);
            sleep(2000);
            getSample(5, 70);
            sleep(5000);
        }
    }

    public void deploy(){
        arm.setArmDeployPos();
        arm.updateArmPos();
        sleep(3000);
        arm.drop();
        sleep(1500);
        arm.rest();
        arm.setArmActivePos();
        sleep(1500);
        arm.setArmStartPos();
        sleep(1000);
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
