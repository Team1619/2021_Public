package org.uacr.utilities;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Create a list of elements but restricts it to a specified size
 * If an element is added when the list is already at max length it will not add the element
 */

public class LimitedSizeQueue<E> extends LinkedBlockingQueue<E> {

    private final int limit;

    /**
     * Sets the size of the queue
     * @param limit the max number of elements
     */
    public LimitedSizeQueue(int limit) {
        this.limit = limit;
    }

    /**
     * Tries to add an element to the queue
     * @param element the element to add
     * @return whether it was added
     */

    @Override
    public boolean add(E element) {
        boolean added = super.add(element);
        while (added && size() > limit) {
            super.remove();
        }
        return added;
    }
}
