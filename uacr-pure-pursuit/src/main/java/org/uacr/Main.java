package org.uacr;

import org.uacr.purepursuit.math.Point;
import org.uacr.purepursuit.math.Pose2d;
import org.uacr.purepursuit.path.Path;
import org.uacr.purepursuit.path.PathBuilder;
import org.uacr.purepursuit.path.PathConstrains;
import org.uacr.purepursuit.path.velocity.TrapezoidVelocityProfile;
import org.uacr.purepursuit.path.velocity.VelocityProfile;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        PathConstrains pathConstrains = new PathConstrains();
        pathConstrains.lookaheadDistance = 40;
        pathConstrains.maxVelocity = 10;
        pathConstrains.minVelocity = 1;
        pathConstrains.maxAcceleration = 0.2;
        pathConstrains.maxDeceleration = 0.2;

        PathBuilder.start(0, 0, pathConstrains).lineTo(60, 0).lineTo(60, 60)
				.lineTo(200, 60).lineTo(200, 0)
                .build();

        Path path = PathBuilder.start(new Point(0, 0))
                .lineTo(50, 0)
                .lineTo(50, 50)
                .lineTo(100, 50)
                .lineTo(100, 0)
                .lineTo(150, 0)
                .build();


//        System.out.println(path.getVelocity(new Pose2d(50, 20, 0)));

        PathConstrains constrains = new PathConstrains();
        constrains.maxAcceleration = 0.2;
        constrains.maxDeceleration = 0.2;

        VelocityProfile profile = new TrapezoidVelocityProfile(constrains, path.length(), path.getSpeedReductions());

        List<Point> velocityProfile = new ArrayList<>();
        for (double d = -10; d <= path.length() + 10; d += 0.25) {
            velocityProfile.add(new Point(d, profile.getVelocity(d)));
        }
        System.out.println(velocityProfile);
    }
}
