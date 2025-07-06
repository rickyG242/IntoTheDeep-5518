package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Libs.AR.AR_Arm_Fisher;
import org.firstinspires.ftc.teamcode.Libs.GoBilda.DriveToPoint;
import org.firstinspires.ftc.teamcode.Libs.GoBilda.GoBildaPinpointDriver;

import java.util.Locale;

@Autonomous(name="Main Left Auto", group="CompAuto")
public class OdometryParkAuto extends LinearOpMode {

    // Initialize drive motors
    DcMotor MTR_LF;
    DcMotor MTR_RF;
    DcMotor MTR_LB;
    DcMotor MTR_RB;

    // Initialize arm
    AR_Arm_Fisher arm;

    // Declare OpMode member for the Odometry Computer
    GoBildaPinpointDriver odo;
    //OpMode member for the point-to-point navigation class
    DriveToPoint nav = new DriveToPoint(this);

    enum StateMachine {
        WAITING_FOR_START,
        COMPLETED_PATH,
        DRIVE_TO_TARGET_1
    }

    // Targets/Points along the robot's drive path
    static Pose2D START_POS;
    static final Pose2D TARGET_1 = new Pose2D(DistanceUnit.MM, 60 * 25.4, -72 * 25.4 + 451.2 / 2, AngleUnit.DEGREES, 0);

    private final double power = 0.2;
    private double waitTime = 0;
    private Pose2D currentTarget;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        MTR_LF = hardwareMap.get(DcMotor.class, "left_front_mtr");
        MTR_RF = hardwareMap.get(DcMotor.class, "right_front_mtr");
        MTR_LB = hardwareMap.get(DcMotor.class, "left_back_mtr");
        MTR_RB = hardwareMap.get(DcMotor.class, "right_back_mtr");

        // Initialize arm and claw
        arm = new AR_Arm_Fisher(this);

        MTR_LF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MTR_RF.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MTR_LB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MTR_RB.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        MTR_LF.setDirection(DcMotorSimple.Direction.REVERSE);
        MTR_LB.setDirection(DcMotorSimple.Direction.REVERSE);

        // Changes starting position for parking
        if(gamepad1.a) {
            START_POS = new Pose2D(DistanceUnit.MM, 36 * 25.4, -72 * 25.4 + 451.2 / 2, AngleUnit.DEGREES, 0);
        }
        else {
            START_POS = new Pose2D(DistanceUnit.MM, -36 * 25.4, -72 * 25.4 + 451.2 / 2, AngleUnit.DEGREES, 0);
            waitTime = 5;
        }

        // Increase wait time before parking
        if(gamepad1.b) {
            waitTime = 10;
        }

        odo = hardwareMap.get(GoBildaPinpointDriver.class,"odo");
        odo.resetPosAndIMU();

        odo.setOffsets(-84.0, -168.0);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        // Initializes robot's position
        odo.setPosition(START_POS);
        telemetry.addData("Position", odo.getPosition());
        telemetry.addData("Start Position", START_POS);

        //nav.setXYCoefficients(0.02,0.002,0.0,DistanceUnit.MM,12);
        //nav.setYawCoefficients(1,0,0.0, AngleUnit.DEGREES,2);
        nav.setDriveType(DriveToPoint.DriveType.MECANUM);

        // Initializes state machine
        StateMachine stateMachine;
        stateMachine = StateMachine.WAITING_FOR_START;

        telemetry.addData("Status", "Initialized");
        telemetry.addData("X offset", odo.getXOffset());
        telemetry.addData("Y offset", odo.getYOffset());
        telemetry.addData("Device Version Number:", odo.getDeviceVersion());
        telemetry.addData("Device Scalar", odo.getYawScalar());
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();
        resetRuntime();

        odo.setPosition(START_POS);

        // Timer for wait time before parking
        ElapsedTime waitTimer = new ElapsedTime();

        while(opModeIsActive()) {
            odo.update();

            if(waitTimer.seconds() > waitTime) {
                switch (stateMachine) {
                    case WAITING_FOR_START:
                        stateMachine = StateMachine.DRIVE_TO_TARGET_1;
                        break;
                    case DRIVE_TO_TARGET_1:
                        currentTarget = TARGET_1;
                        if (nav.driveTo(odo.getPosition(), TARGET_1, power, 0)) {
                            stateMachine = StateMachine.COMPLETED_PATH;
                        }
                        break;
                }
            }

            //nav calculates the power to set to each motor in a mecanum or tank drive. Use nav.getMotorPower to find that value.
            MTR_LF.setPower(nav.getMotorPower(DriveToPoint.DriveMotor.LEFT_FRONT));
            MTR_RF.setPower(nav.getMotorPower(DriveToPoint.DriveMotor.RIGHT_FRONT));
            MTR_LB.setPower(nav.getMotorPower(DriveToPoint.DriveMotor.LEFT_BACK));
            MTR_RB.setPower(nav.getMotorPower(DriveToPoint.DriveMotor.RIGHT_BACK));

            telemetry.addData("Current state", stateMachine);

            String formattedPosition = "";
            if(currentTarget != null) {
                formattedPosition = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", currentTarget.getX(DistanceUnit.MM), currentTarget.getY(DistanceUnit.MM), currentTarget.getHeading(AngleUnit.DEGREES));
            }
            telemetry.addData("Target position", formattedPosition);

            Pose2D pos = odo.getPosition();
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Position", data);

            telemetry.update();
        }
    }
}