package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Libs.AR.AR_Arm_Fisher;

@TeleOp(name = "TeleOp_Arm_Test", group = "TeleOp")
public class TeleOp_Arm_Test extends LinearOpMode
{
    private AR_Arm_Fisher arm;


    //@Override
    public void runOpMode()
    {
        // Initialize the arm
        arm = new AR_Arm_Fisher(this);

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive())
        {

            if (gamepad2.dpad_up) {
                arm.setArmDeployPos();
                telemetry.addData("Arm Status", "Set Arm Deploy");
            }
            else if (gamepad2.dpad_right) {
                arm.setArmActivePos();
                telemetry.addData("Arm Status", "Set Arm Active");
            }
            else if (gamepad2.dpad_left) {
                arm.setArmStartPos();
                telemetry.addData("Arm Status", "Set Arm Start");
            }
            else if (gamepad2.dpad_down) {
                arm.setArmGrabPos();
                telemetry.addData("Arm Status", "Set Arm Grab");
            }
            else if (gamepad2.square) {
                arm.setArmMidPos();
                telemetry.addData("Arm Status", "Set Arm Mid");
            }
            arm.updateArmPos();

            if (gamepad2.left_trigger > 0.1) {
                arm.grab();
            } else if (gamepad2.right_trigger > 0.1){
                arm.drop();
            }
            else{
                arm.rest();
            }

            if(arm.getDetectedColor()==0){
                arm.turnBlue();
            }
            else if(arm.getDetectedColor()==1){
                arm.turnRed();
            }
            else{
                arm.turnYellow();
            }

            //**************************************************************************************
            // ---------------------Gamepad 2 Controls ---------------------------------------------

            //**************************************************************************************
            //--------------------- TELEMETRY Code -------------------------------------------------
            // Useful telemetry data in case needed for testing and to find heading of robot
            arm.getTelemetry();
            telemetry.update();
        }
    }
}
