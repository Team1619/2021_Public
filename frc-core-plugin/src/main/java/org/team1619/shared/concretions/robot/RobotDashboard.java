package org.team1619.shared.concretions.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1619.shared.abstractions.Dashboard;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;


public class RobotDashboard implements Dashboard {

    private static final Logger logger = LogManager.getLogger(RobotDashboard.class);

    private final InputValues sharedInputValues;

    Preferences prefs = Preferences.getInstance();
    SendableChooser<String> autoOrigin;
    SendableChooser<String> autoDestination;
    SendableChooser<String> autoAction;
    private String previousAutoOrigin = "none";
    private String previousAutoDestination = "none";
    private String previousAutoAction = "none";

    @Inject
    public RobotDashboard(InputValues inputValues) {
        sharedInputValues = inputValues;

        autoOrigin = new SendableChooser<>();
        autoDestination = new SendableChooser<>();
        autoAction = new SendableChooser<>();
    }

    @Override
    public void initialize() {
//		autoOrigin.setDefaultOption("None", "None");
//		autoOrigin.addOption("Left", "Left");
//		autoOrigin.addOption("Right", "Right");
//		autoOrigin.addOption("Center", "Center");
//		SmartDashboard.putData("Origin", autoOrigin);
//		autoDestination.setDefaultOption("None", "None");
//		autoDestination.addOption("Trench", "Trench Left");
//		autoDestination.addOption("Shield Generator", "Shield Generator");
//		autoDestination.addOption("None", "None");
//		SmartDashboard.putData("Action", autoAction);
//		autoAction.setDefaultOption("None", "None");
//		autoAction.addOption("Shoot 3 Collect 5", "Shoot 3 Collect 5");
//		autoAction.addOption("Shoot 3", "Shoot 3");
//		autoAction.addOption("8 Ball", "8 Ball");
//		SmartDashboard.putData("Destination", autoDestination);
//		previousAutoOrigin = autoOrigin.getSelected();
//		previousAutoDestination = autoDestination.getSelected();
//		previousAutoAction = autoAction.getSelected();

    }

    @Override
    public void putNumber(String name, double value) {
        SmartDashboard.putNumber(name, value);
    }

    @Override
    public void putBoolean(String name, boolean value) {
        SmartDashboard.putBoolean(name, value);
    }

    //Intended for Preferences
    @Override
    public double getNumber(String name) {
        return prefs.getDouble(name, -1);
    }

    @Override
    public void putString(String key, String value) {
        SmartDashboard.putString(key, value);
    }

    @Override
    public void setNetworkTableValue(String table, String entry, Object value) {
        NetworkTableInstance.getDefault().getTable(table).getEntry(entry).setValue(value);
    }

    @Override
    public void smartdashboardSetAuto() {
        sharedInputValues.setString("ips_auto_origin", autoOrigin.getSelected());
        sharedInputValues.setString("ips_auto_destination", autoDestination.getSelected());
        sharedInputValues.setString("ips_auto_action", autoAction.getSelected());
        sharedInputValues.setString("ips_selected_auto",
                sharedInputValues.getString("ips_auto_origin") + " to " +
                        sharedInputValues.getString("ips_auto_destination") + ", " +
                        sharedInputValues.getString("ips_auto_action"));
    }

    @Override
    public boolean autoSelectionRisingEdge() {
//		String origin = autoOrigin.getSelected();
//		String destination = autoDestination.getSelected();
//		String action = autoAction.getSelected();
//		if(origin != null && destination != null && action != null && (!previousAutoOrigin.equals(origin) || !previousAutoDestination.equals(destination) || !previousAutoAction.equals(action))) {
//			previousAutoOrigin = origin;
//			previousAutoDestination = destination;
//			previousAutoAction = action;
//			return true;
//		}
        return false;
    }
}
