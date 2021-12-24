package org.uacr.utilities;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Creates a unique key/ single identifier out of multiple objects
 */

public class MultiObjectKey {

    private final Object[] keys;

    /**
     * Accepts keys and stores them
     * If no keys are specified, creates a key with a value of an array containing a single 0
     * @param keys the keys to turn into an identifier
     */
    public MultiObjectKey(@Nullable Object... keys) {
        if (keys == null) {
            keys = new Object[0];
        }
        this.keys = keys;
    }

    /**
     * @param object the int identifier to compare to the multiObjectKey stored in this instance of this class
     * @return if the objects contain the same keys
     */
    public boolean equals(Object object) {
        if (object instanceof MultiObjectKey) {
            return Arrays.deepEquals(keys, ((MultiObjectKey) object).keys);
        }
        return false;
    }

    /**
     * @return an int used to identify this specific set of keys
     */

    public int hashCode() {
        return Arrays.deepHashCode(keys);
    }
}
