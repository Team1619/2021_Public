package org.uacr.robot;

import org.uacr.models.exceptions.ConfigurationException;
import org.uacr.shared.abstractions.FMS;
import org.uacr.shared.abstractions.InputValues;
import org.uacr.shared.abstractions.RobotConfiguration;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * The base for all state control logic
 */

public abstract class AbstractStateControls {

    private static final Logger logger = LogManager.getLogger(AbstractStateControls.class);

    protected final InputValues sharedInputValues;
    protected final RobotConfiguration robotConfiguration;
    private final HashMap<ControlMode, AbstractModeLogic> modeLogicMap;

    protected FMS.Mode fmsMode;
    private ControlMode currentControlMode;
    @Nullable
    private AbstractRobotStatus robotStatus;

    public AbstractStateControls(InputValues inputValues, RobotConfiguration robotConfiguration) {
        sharedInputValues = inputValues;
        this.robotConfiguration = robotConfiguration;
        modeLogicMap = new HashMap<>();
        fmsMode = FMS.Mode.DISABLED;
        currentControlMode = ControlMode.TELEOP;
    }

    /**
     * Called when switching into Teleop or Auto
     * This is a place clear and initialize variables...
     */
    public abstract void initialize(FMS.Mode currentFmsMode);

    /**
     * Called every frame
     * This is a place read buttons, check modes...
     */
    public abstract void update();

    /**
     * Called when switching between Auto, Teleop and Disabled
     * This is a place for any clean up, clearing variables...
     */
    public abstract void dispose();

    /**
     * Called by the concretion of StateControls to initialize the mode logic for each control mode
     *
     * @param controlMode the ControlMode that is mode logic should be used for
     * @param modeLogic   the ModeLogic concretion to be used for the ControlMode
     */
    public void registerModeLogic(ControlMode controlMode, AbstractModeLogic modeLogic) {
        modeLogicMap.put(controlMode, modeLogic);
    }

    /**
     * Called by the concretion of StateControls to initialize the robot status for this robot
     *
     * @param robotStatus the RobotStatus concretion to be used for this robot
     */
    public void registerRobotStatus(AbstractRobotStatus robotStatus) {
        this.robotStatus = robotStatus;
    }

    /**
     * Called by the RobotManager to get the current ModeLogic concretion selected by the StateControls concretion
     *
     * @return the current ModeLogic concretion
     */
    public AbstractModeLogic getCurrentModeLogic() {
        return modeLogicMap.get(getCurrentControlMode());
    }

    /**
     * Called by the RobotManager to get the RobotStatus for this robot
     *
     * @return the RobotStatus for this robot
     */
    public AbstractRobotStatus getRobotStatus() {
        if (robotStatus != null) {
            return robotStatus;
        }
        throw new ConfigurationException("RobotStatus not registered. Call method registerRobotStatus() in the constructor of the concretion of StateControls");
    }

    /**
     * Called by RobotManager to get the current requested control mode
     *
     * @return the current control mode
     */
    public ControlMode getCurrentControlMode() {
        return currentControlMode;
    }

    /**
     * Called by the StateControls concretion to select a ControlMode
     *
     * @param currentControlMode
     */
    public void setCurrentControlMode(ControlMode currentControlMode) {

        if (modeLogicMap.get(currentControlMode) == null) {
            logger.error("******** No mode logic specified for " + currentControlMode + " ********");
        } else {
            this.currentControlMode = currentControlMode;
        }
    }
}