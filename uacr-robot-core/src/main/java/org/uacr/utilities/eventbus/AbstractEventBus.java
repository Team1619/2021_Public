package org.uacr.utilities.eventbus;

public abstract class AbstractEventBus {

    public abstract void register(Object listener);

    public abstract void unregister(Object listener);

    public abstract void post(Object event);
}
