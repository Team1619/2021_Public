package org.uacr.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A wrapper for ArrayList that allows for the creation of one using List.of()
 *  Also prevents you from changing the ArrayList once it has been created
 */

public class Lists {

    /**
     * @param <T> type of objects to be put in the list
     * @return an empty ArrayList
     */
    public static <T> List<T> of() {
        return new ArrayList<>();
    }

    /**
     * @param objects the objects to be put in the list
     * @param <T> the type of objects
     * @return an ArrayList containing those objects
     */

    public static <T> List<T> of(T... objects) {
        return Arrays.asList(objects);
    }

    /**
     * Allows for the conversion of a set of objects into an ArrayList
     * @param objects a set of objects to be put in a list
     * @param <T> the type of objects
     * @return an Arraylist containing those objects
     */

    public static <T> List<T> of(Set<T> objects) {
        List<T> list = of();
        list.addAll(objects);
        return list;
    }
}
