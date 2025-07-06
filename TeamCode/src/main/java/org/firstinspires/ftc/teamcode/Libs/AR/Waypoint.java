package org.firstinspires.ftc.teamcode.Libs.AR;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Waypoint {
    Pose2D target;
    Runnable[] functions;
    Runnable telemetry;

    public Waypoint(Pose2D target, Runnable[] functions, Runnable telemetry) {
        this.target = target;
        this.functions = functions;
        this.telemetry = telemetry;
    }
}