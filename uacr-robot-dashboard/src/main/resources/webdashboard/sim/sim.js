const refreshRate = 10;
const robotWidth = 80;
const robotLength = 120;
const wheelWidth = 15;
const wheelLength = 30;
const maxModuleAngularVelocity = 2;
const maxModuleLinearVelocity = 1;
const wheelHalfWidth = wheelWidth / 2;
const wheelHalfLength = wheelLength / 2;
const frPosition = new Vector(new Point(robotLength / 2, -robotWidth / 2));
const flPosition = new Vector(new Point(robotLength / 2, robotWidth / 2));
const blPosition = new Vector(new Point(-robotLength / 2, robotWidth / 2));
const brPosition = new Vector(new Point(-robotLength / 2, -robotWidth / 2));

let robotPosition = new Point(200, 200);
let robotAngle = 0;

let lastValues = {}

let frMovement = new Vector(0, 0);
let flMovement = new Vector(0, 0);
let blMovement = new Vector(0, 0);
let brMovement = new Vector(0, 0);
let frRequestedMovement = new Vector(0, 0);
let flRequestedMovement = new Vector(0, 0);
let blRequestedMovement = new Vector(0, 0);
let brRequestedMovement = new Vector(0, 0);

const canvas = $("canvas")[0];
const drawContext = canvas.getContext("2d");

//The websocket which connects to the java server in the robot code
let socket;

function drawRobot(fr, fl, bl, br) {
    canvas.width = canvas.offsetWidth
    canvas.height = canvas.offsetHeight

    let x = robotPosition.x;
    let y = robotPosition.y;

    if(x < 0) {
        robotPosition = new Vector(new Point(0, robotPosition.y));
    }

    if(x > canvas.width) {
        robotPosition = new Vector(new Point(canvas.width, robotPosition.y));
    }

    if(y < 0) {
        robotPosition = new Vector(new Point(robotPosition.x, 0));
    }

    if(y > canvas.height) {
        robotPosition = new Vector(new Point(robotPosition.x, canvas.height));
    }

    rotate(x, y, -robotAngle);

    drawContext.strokeStyle = "#FFFFFF";

    let frameLeft = y - robotWidth / 2;
    let frameRight = y + robotWidth / 2;
    let frameFront = x + robotLength / 2;
    let frameBack = x - robotLength / 2;

    drawContext.lineWidth = 6;

    drawModule(frameRight, frameFront, fr);
    drawModule(frameLeft, frameFront, fl);
    drawModule(frameLeft, frameBack, bl);
    drawModule(frameRight, frameBack, br);

    drawContext.lineWidth = 2;

    drawContext.strokeStyle = "#999999";
    drawContext.strokeRect(x - robotLength / 2, y - robotWidth / 2, robotLength, robotWidth);

    drawContext.strokeStyle = "#FFFF00";
    drawContext.beginPath();
    drawContext.moveTo(x + robotLength / 2, y - robotWidth / 2);
    drawContext.lineTo(x + robotLength / 2, y + robotWidth / 2);
    drawContext.stroke();
}

function drawModule(y, x, vector) {
    let speed = vector.magnitude;
    let angle = -vector.angle;

    rotate(x, y, angle);

    if(speed > 0) {
        drawContext.fillStyle = "#00FF00";
    } else if(speed < 0) {
        drawContext.fillStyle = "#FF0000";
    } else {
        drawContext.fillStyle = "#FFFFFF";
    }

    rectangle(x - wheelHalfLength, y - wheelHalfWidth,
        x + wheelHalfLength, y + wheelHalfWidth);

    drawContext.strokeStyle = "#FF00FF";
    drawContext.beginPath();
    drawContext.moveTo(x + wheelHalfLength, y - wheelHalfWidth);
    drawContext.lineTo(x + wheelHalfLength, y + wheelHalfWidth);
    drawContext.stroke();

    rotate(x, y, -angle);
}

function rectangle(x1, y1, x2, y2) {
    drawContext.fillRect(x1, y1, x2 - x1, y2 - y1);
}

function rotate(x, y, angle) {
    drawContext.translate(x, y)
    drawContext.rotate(toRadians(angle))
    drawContext.translate(-x, -y)
}

function scaleModuleVector(vector) {
    if(vector.magnitude > maxModuleLinearVelocity) {
        return vector.normalize().scale(maxModuleLinearVelocity);
    }
    return vector;
}

function moduleTorque(positionVector, movementVector) {
    return Math.sin(toRadians(positionVector.angle - movementVector.angle)) * -movementVector.magnitude;
}

function updateModule(current, requested) {
    requested = scaleModuleVector(requested);
    if(requested.angle - current.angle > 180) {
        return new Vector(requested.magnitude, current.angle - maxModuleAngularVelocity);
    } else if(requested.angle - current.angle > maxModuleAngularVelocity) {
        return new Vector(requested.magnitude, current.angle + maxModuleAngularVelocity);
    } else if(requested.angle - current.angle < -180) {
        return new Vector(requested.magnitude, current.angle + maxModuleAngularVelocity);
    } else if(requested.angle - current.angle < -maxModuleAngularVelocity) {
        return new Vector(requested.magnitude, current.angle - maxModuleAngularVelocity);
    } else {
        return requested;
    }
}

function sendValue(name, value, selected) {
    try {
        let key = name + "-" + selected;
        value = Math.round(value);
        if (lastValues[key] !== value) {
            if (selected !== undefined) {
                socket.send(new UrlFormData().append("request", "change_value").append("type", "vector").append("name", name).append("value", value).append("selected", selected).toString());
            } else {
                socket.send(new UrlFormData().append("request", "change_value").append("type", "numeric").append("name", name).append("value", value).toString());
            }
        }
        lastValues[key] = value;
    } catch (e) {
        console.log(e);
    }
}

//Code to create, maintain, and reopen a connection with the server in the robot code
function connect() {

    //Create a new websocket with the same host as the page and the page port plus 1 and path "/log"
    //to connect with the server in the robot code
    socket = new WebSocket("ws://" + window.location.hostname + ":" + (parseInt(window.location.port) + 1) + "/values");

    //Called when the connection opens
    socket.onopen = function () {

    };

    //Called when the connection closes
    socket.onclose = function () {

        //Try to reopen the connection after 1 second
        setTimeout(function () {
            connect();
        }, 1000);
    };

    //Called when a message is received over the connection
    socket.onmessage = function (message) {

        //Turn message data into UrlFormData
        let messageData = new UrlFormData(message.data);

        //Case statement on the message response type
        switch (messageData.get("response")) {
            case "values":

                let values = messageData.get("values").split("~");

                for(let value of values) {
                    let valueData = value.split("*");

                    if(valueData[1] === "opn_drivetrain_front_right_speed") {
                        frRequestedMovement = new Vector(parseFloat(valueData[2]), frRequestedMovement.angle);
                    }

                    if(valueData[1] === "opn_drivetrain_front_left_speed") {
                        flRequestedMovement = new Vector(parseFloat(valueData[2]), flRequestedMovement.angle);
                    }

                    if(valueData[1] === "opn_drivetrain_back_left_speed") {
                        blRequestedMovement = new Vector(parseFloat(valueData[2]), blRequestedMovement.angle);
                    }

                    if(valueData[1] === "opn_drivetrain_back_right_speed") {
                        brRequestedMovement = new Vector(parseFloat(valueData[2]), brRequestedMovement.angle);
                    }

                    if(valueData[1] === "opn_drivetrain_front_right_angle") {
                        frRequestedMovement = new Vector(frRequestedMovement.magnitude, parseFloat(valueData[2]));
                    }

                    if(valueData[1] === "opn_drivetrain_front_left_angle") {
                        flRequestedMovement = new Vector(flRequestedMovement.magnitude, parseFloat(valueData[2]));
                    }

                    if(valueData[1] === "opn_drivetrain_back_left_angle") {
                        blRequestedMovement = new Vector(blRequestedMovement.magnitude, parseFloat(valueData[2]));
                    }

                    if(valueData[1] === "opn_drivetrain_back_right_angle") {
                        brRequestedMovement = new Vector(brRequestedMovement.magnitude, parseFloat(valueData[2]));
                    }
                }

                break;
        }
    };
}

//Call connect to initiate a connection with the server in the robot code
connect();

//Check every second to confirm the websocket is still connected with the robot code
setInterval(() => {
    if (socket !== undefined) {
        //If socket exists then send a keepalive to make sure the socket is still connected
        socket.send("keepalive");
    }
}, 1000);

setInterval(() => {
    frMovement = updateModule(frMovement, frRequestedMovement);
    flMovement = updateModule(flMovement, flRequestedMovement);
    blMovement = updateModule(blMovement, blRequestedMovement);
    brMovement = updateModule(brMovement, brRequestedMovement);

    sendValue("ipn_drivetrain_front_right_angle", frMovement.angle);
    sendValue("ipn_drivetrain_front_left_angle", flMovement.angle);
    sendValue("ipn_drivetrain_back_left_angle", blMovement.angle);
    sendValue("ipn_drivetrain_back_right_angle", brMovement.angle);

    let robotMovement = new Vector(frMovement.add(flMovement).add(blMovement).add(brMovement));

    let robotTorque = moduleTorque(frPosition, frMovement) + moduleTorque(flPosition, flMovement) +
        moduleTorque(blPosition, blMovement) + moduleTorque(brPosition, brMovement);

    robotAngle += robotTorque;

    robotAngle = cleanAngle(robotAngle);

    sendValue("ipv_navx", Math.round(robotAngle), "angle");

    robotMovement = new Vector(robotMovement.magnitude, robotMovement.angle + robotAngle);
    robotMovement = new Vector(new Point(robotMovement.x, -robotMovement.y))

    robotPosition = robotPosition.add(robotMovement)

    drawRobot(frMovement, flMovement, blMovement, brMovement);
}, refreshRate);
