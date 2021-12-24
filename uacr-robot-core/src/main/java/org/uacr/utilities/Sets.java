package org.uacr.utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 *  A wrapper for HashSets that allows for the creation of one using Sets.of()
 *  Also prevents you from changing the HashSet once it has been created
 */

public class Sets {

    /**
     * @param objects the objects to be put in the list
     * @param <T> the type of objects
     * @return a HashSet containing the objects
     */
    public static <T> HashSet<T> of(T... objects) {
        return new HashSet<>(Arrays.asList(objects));
    }

    /**
     * @param objects a list of objects to be converted into the set
     * @param <T> the type of objects
     * @return a HashSet containing the objects
     */

    public static <T> HashSet<T> of(List<T> objects) {
        return new HashSet<>(objects);
    }
}
