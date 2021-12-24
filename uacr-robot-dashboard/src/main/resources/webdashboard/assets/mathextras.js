//Stores point data for the path page
//Emulates the java Point class
class Point {

    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    add(point) {
        return new Point(this.x + point.x, this.y + point.y);
    };

    subtract(point) {
        return new Point(this.x - point.x, this.y - point.y);
    }

    distance(point) {
        return Math.sqrt(Math.pow(point.x - this.x, 2) + Math.pow(point.y - this.y, 2));
    }

    toString() {
        return this.x + "," + this.y;
    }
}

//Stores path point data for the path page
//Emulates the java PathPoint class
class PathPoint extends Point {

    constructor(x, y, distance, curvature, velocity) {
        super(x, y);
        this.startDistance = distance;
        this.curvature = curvature;
        this.velocity = velocity;
    }
}

//Stores vector data for the path page
//Emulates the java Vector class
class Vector extends Point {

    constructor(val1, val2) {
        if (val2 !== undefined) {
            if(typeof val1 === "object" && typeof val2 === "object") {
                super(val2.x - val1.x, val2.y - val1.y);
            } else {
                val2 = cleanAngle(val2);
                let angle = toRadians(val2);
                super(Math.cos(angle) * val1, Math.sin(angle) * val1);
                this.magnitude = val1;
                this.angle = val2;
            }
        } else {
            super(val1.x, val1.y);
        }

        if(this.magnitude === undefined) {
            this.magnitude = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
        }
        if(this.angle === undefined) {
            this.angle = toDegrees(Math.atan2(this.y, this.x));
        }
    }

    normalize() {
        return new Vector(new Point(0, 0), new Point((1 / this.magnitude) * this.x, (1 / this.magnitude) * this.y));
    }

    scale(scalar) {
        return new Vector(new Point(0, 0), new Point(this.x * scalar, this.y * scalar));
    }
}

function toRadians(angle) {
    return angle * (Math.PI / 180)
}

function toDegrees(angle) {
    return angle * (180 / Math.PI)
}

function cleanAngle(angle) {
    angle = angle % 360;

    if(angle > 180) {
        return -360 + angle;
    }

    if(angle < -180) {
        return 360 + angle;
    }

    return angle;
}