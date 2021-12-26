# 2021_Public

Dec 26, 2021

The uacr-framework-development project breaks the code into separate projects, each that work together to create the final robot project.

The core projects are:
uacr-robot-core: Handles common (non-hardware) logic
frc-core-plugin: Handles the interface to the hardware and WPILib
uacr_robot_dashboard: Creates a web browser dashboard for testing in simulation mode, on the robot, and at competitions
uacr_pure_pursuit: Handles path following

The 4 core projects above are used in the following robot projects:
2020-basebot: used as a template project
2020-frc-compbot: 2020 competition robot
2020_frc_protobot: prototyping robot platform
2020-frc-challengebot: 2021 at home competition robot
2021-frc-defensebot: experimental swerve robot
2021-t-shirt-bot: off season project

To debug any of the robot projects in simulation mode:
* Open the gradle tab
* Go to Tasks > other
* Right click on xyzSim (such as compbotSim)
* Select 'Modify Run Configuration"
* Click the 'Modify options' dropdown box and select 'Debug all tasks on the execution graph'
* Click 'ok'
* Set a breakpoint
* Double click xyzSim (such as compbotSim)
* You should see the following messages in the Intellij console window
>   Task :2020-frc-basebot:run
    14:48:54.002 main [INFO] RobotCore - Starting services
    14:48:54.042 LoggingService [DEBUG] LoggingService - Starting LoggingService
    14:48:54.044 LoggingService [DEBUG] LoggingService - LoggingService started
    14:48:54.044 WebDashboardService [DEBUG] WebDashboardService - Starting WebDashboardService
    14:48:54.046 WebDashboardService [DEBUG] WebDashboardService - WebDashboardService started
    14:48:54.047 main [INFO] RobotCore - ********************* ALL SERVICES STARTED *******************************
    14:49:07.472 LoggingService [DEBUG] LoggingService - ********** Info thread frame cycle time = 83.0
* Open a Chrome browser
* Go to http://localhost:5800/
* Plug in 2 Xbox Controllers to your laptop
* Double click the web page to bring a configuration screen
* Select 'Teleop', your Xbox controllers and click 'Close'
* You should now be able to control the code with the Xbox controllers and hit breakpoints







