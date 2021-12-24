package org.uacr.shared.concretions;

import org.uacr.shared.abstractions.FMS;
import org.uacr.utilities.injection.Singleton;
import org.uacr.utilities.logging.LogManager;
import org.uacr.utilities.logging.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores and updates our internal FMS (can be different than the FMS sent by the field)
 */

@Singleton
public class SharedFMS implements FMS {

    private static final Logger logger = LogManager.getLogger(SharedFMS.class);

    private final Map<String, Mode> data;

    /**
     * Creates a map to store the FMS mode
     */
    public SharedFMS() {
        data = new ConcurrentHashMap<>();
    }

    /**
     * @return the current FMS mode, if none is specified return DISABLED
     */
    @Override
    public Mode getMode() {
        return data.getOrDefault("mode", Mode.DISABLED);
    }

    /**
     * Sets the FMS mode
     * @param mode the mode to set the FMS to (AUTONOMOUS, TELEOP, DISABLED, TEST)
     */
    @Override
    public void setMode(Mode mode) {
        data.put("mode", mode);

        logger.debug("FMS mode set to '{}'", mode);
    }
}
