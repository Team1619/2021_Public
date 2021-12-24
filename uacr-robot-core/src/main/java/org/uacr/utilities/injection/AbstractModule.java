package org.uacr.utilities.injection;

import java.util.HashMap;

/**
 * Handles configuration of bindings
 */

public abstract class AbstractModule {

    private final HashMap<Class, Class> bindings;

    public AbstractModule() {
        bindings = new HashMap<>();
    }

    protected abstract void configure();

    public void bind(Class parent, Class child) {
        if (!parent.isAssignableFrom(child)) {
            throw new RuntimeException("Cannot bind " + child + " to " + parent);
        }

        bindings.put(parent, child);
    }

    public HashMap<Class, Class> getBindings() {
        return (HashMap<Class, Class>) bindings.clone();
    }
}
