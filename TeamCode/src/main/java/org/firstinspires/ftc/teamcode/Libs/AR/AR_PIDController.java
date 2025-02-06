package org.firstinspires.ftc.teamcode.Libs.AR;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Libs.AR.Archive.AR_Arm;

/**
 * This class creates a PID Controller to use with each joint.
 *
 * Instantiate this class for each JOINT in the ARM object.
 *
 * Creation Date: 11/3/2024
 */

public class AR_PIDController
{
    private PIDController controller;
    // These variables are used to customize the PID Controller for the application.
    private double p, i, d;
    private double f;
    // This variable need to be customized for the motor being used.
    private final double ticksPerDegree = 5281.1 / 360;  // For GoBilda 30 RPM Motor
    LinearOpMode bot;
    private DcMotor motor;
    private String jointName;
    private int loopCount;  // Used for debugging to count the number of PID loops.
    // Boolean flag to activate fuzzy logic autotuning
    public boolean fuzzyLogicActive = false;
    /**
     * Constructor. Perform the setup of the PID Controller.
     */
    public AR_PIDController(LinearOpMode iBot, DcMotor iMotor, String iJointName, double iP, double iI, double iD, double iF, boolean activateFuzzyLogic)
    {
        this.bot = iBot;
        this.motor = iMotor;
        this.jointName = iJointName;
        p = iP;
        i = iI;
        d = iD;
        f = iF;
        setFuzzyLogicActive(activateFuzzyLogic);
        // Create PID Controller
        controller = new PIDController(p, i, d);
        loopCount = 0; // Set to 0 when initialized
    }
    // Setters and Getters for fuzzy logic toggle
    public void setFuzzyLogicActive(boolean active) {
        fuzzyLogicActive = active;
    }
    // Define fuzzy logic function to adjust PID parameters based on error and derivative
    private void fuzzyLogicAutotune(double error, double errorRate) {
        // Basic Fuzzy Logic Rules for adjusting PID parameters
        if (error > 5 && errorRate > 2) {
            p = 1.0; i = 0.5; d = 0.2;  // High gains for fast response
        } else if (error > 2 && errorRate > 1) {
            p = 0.8; i = 0.4; d = 0.15; // Medium gains
        } else if (error < 1 && errorRate < 0.5) {
            p = 0.5; i = 0.2; d = 0.1;  // Low gains for stability
        } else {
            p = 0.6; i = 0.3; d = 0.1;  // Default values if fuzzy logic is not activated
        }
    }
    /**
     * This function takes a target value and moves the joint to that position.
     */
    public void loop(int target, int iCurrentState, int iLastState ) // Input in degrees
    {
        // Activate fuzzy logic if the flag is true
        if (fuzzyLogicActive) {
            double error = target - (motor.getCurrentPosition() / ticksPerDegree);  // Current error
            double errorRate = (error - (target - (motor.getCurrentPosition() / ticksPerDegree))) / 1; // Approximate rate of error change
            fuzzyLogicAutotune(error, errorRate);  // Adjust PID parameters using fuzzy logic
        }
        // Apply updated PID values from fuzzy logic (if active)
        this.controller.setPID(p, i, d);
        double armPos = 0;
        // Are we working on the first joint or second?
        if(this.jointName.equals("first_joint")) {
            armPos = this.motor.getCurrentPosition() + ( AR_Arm_IK.startJointAngle1 * ticksPerDegree );       // armPos is in Ticks
        } else if (this.jointName.equals("second_joint")){
            armPos = this.motor.getCurrentPosition() + ( AR_Arm_IK.startJointAngle2 * ticksPerDegree );    // armPos is in Ticks
        }
        // Calculate PID control value
        double pid = this.controller.calculate(armPos, target * ticksPerDegree);  // target is in degrees
        double ff = Math.cos(Math.toRadians(target * ticksPerDegree)) * f;  // Feedforward
        double power = pid + ff;  // Total power from PID and feedforward
        // Apply conditions for special cases like arm descent
        if ((this.jointName.equals("first_joint")) &&  // First Joint Only.
                (iLastState == AR_Auto.DEPLOY) &&   // Has to be leaving the DEPLOY state.
                ((armPos / ticksPerDegree) < target - 10)) // Can only be in the initial decent.
        {
            power = power * 0.1;       // Throttle power back for arm descent.
        }
        // Set motor power
        this.motor.setPower(power);
        this.bot.telemetry.addData("Power", " (" + this.jointName + ") " + power);
        this.bot.telemetry.addData("Pos(Ticks)", " (" + this.jointName + ") " + armPos);
        this.bot.telemetry.addData("Pos", " (" + this.jointName + ") " + armPos / ticksPerDegree);
        this.bot.telemetry.addData("Target", " (" + this.jointName + ") " + target);
        this.bot.telemetry.addData("States", iCurrentState + "(" + iLastState + ")");
        loopCount = loopCount + 1;
    }
}