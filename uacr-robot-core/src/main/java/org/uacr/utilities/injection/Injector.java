package org.uacr.utilities.injection;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * Handles constructing services in Sim and Robot mode
 */

public class Injector {

    private final HashMap<Class, Object> singletons;

    private HashMap<Class, Class> bindings;

    /**
     * Configures bindings and creates singletons
     */
    public Injector(AbstractModule module) {
        bindings = new HashMap<>();

        singletons = new HashMap<>();

        module.configure();

        bindings = module.getBindings();

        HashMap<Class, Class> bindings = new HashMap<>();
        bindings.putAll(bindings);

        HashMap<Class, Class> singletonMap = new HashMap<>();

        //Determines which classes are singletons and moves them to the singletons map
        for (HashMap.Entry<Class, Class> binding : bindings.entrySet()) {
            for (Annotation annotation : binding.getValue().getDeclaredAnnotations()) {
                if (annotation.annotationType().equals(Singleton.class)) {
                    singletonMap.put(binding.getKey(), binding.getValue());
                    bindings.remove(binding.getKey());
                }
            }
        }

        //Creates singletons without dependencies
        for (HashMap.Entry<Class, Class> singleton : singletonMap.entrySet()) {
            if (singletons.containsKey(singleton.getKey())) continue;

            try {
                singletons.put(singleton.getKey(), getInstance(singleton.getValue()));
            } catch (Exception e) {

            }
        }

        //Creates singletons with dependencies
        while (singletons.size() < singletonMap.size()) {
            boolean updated = false;

            for (HashMap.Entry<Class, Class> singleton : singletonMap.entrySet()) {
                if (singletons.containsKey(singleton.getKey())) continue;

                try {
                    singletons.put(singleton.getKey(), getSingletonInstance(singleton.getValue(), singleton.getValue()));
                    updated = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!updated) {
                for (HashMap.Entry<Class, Class> singleton : singletonMap.entrySet()) {
                    if (singletons.containsKey(singleton.getKey())) continue;

                    throw new RuntimeException("Cannot create " + singleton.getKey());
                }
            }
        }
    }

    /**
     * Creates an instance of a class
     */
    public <T> T getInstance(Class<T> tClass) {

        @Nullable
        T singleton = getSingleton(tClass);
        if (singleton != null) {
            return singleton;
        }

        Constructor constructor = getConstructor(tClass);

        Class[] parameterTypes = constructor.getParameterTypes();

        Object[] parameters = new Object[parameterTypes.length];

        for (int p = 0; p < parameterTypes.length; p++) {
            @Nullable
            Object parameter = singletons.get(parameterTypes[p]);

            if (parameter == null) {
                Class parameterClass = bindings.get(parameterTypes[p]);

                if (parameterClass != null) {
                    parameter = getInstance(parameterClass);
                }
            }

            if (parameter == null) {
                throw new RuntimeException("Missing parameter " + parameterTypes[p].getSimpleName() + " creating " + tClass.getSimpleName());
            }

            parameters[p] = parameter;
        }

        try {
            return (T) constructor.newInstance(parameters);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Cannot create " + tClass);
    }

    /**
     * Creates an instance of a singleton with or without circular dependencies
     */
    @Nullable
    private <T> T getSingletonInstance(Class<T> tClass, Class<T> singletonClass) {

        Constructor constructor = getConstructor(tClass);

        Class[] parameterTypes = constructor.getParameterTypes();

        Object[] parameters = new Object[parameterTypes.length];

        for (int p = 0; p < parameterTypes.length; p++) {
            @Nullable
            Object parameter = singletons.get(parameterTypes[p]);

            if (parameter == null) {
                Class parameterClass = bindings.get(parameterTypes[p]);

                //Creates a java proxy to handle circular dependencies
                if (parameterClass == null && parameterTypes[p].isAssignableFrom(singletonClass)) {
                    parameter = Proxy.newProxyInstance(Injector.class.getClassLoader(), new Class[]{parameterTypes[p]}, (proxy, method, args) -> {
                        return method.getReturnType().cast(method.invoke(getSingleton(singletonClass), args));
                    });
                } else if (parameterClass != null) {
                    parameter = getSingletonInstance(parameterClass, singletonClass);
                }
            }

            if (parameter == null) {
                throw new RuntimeException("Missing parameter " + parameterTypes[p].getSimpleName() + " creating " + tClass.getSimpleName());
            }

            parameters[p] = parameter;
        }

        try {
            return (T) constructor.newInstance(parameters);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Determines the best constructor to use
     */
    private <T> Constructor<T> getConstructor(Class<T> tClass) {
        Constructor[] constructors = tClass.getConstructors();

        //If there is a constructor annotated with @Inject use that
        for (Constructor constructor : constructors) {
            for (Annotation annotation : constructor.getDeclaredAnnotations()) {
                if (annotation.annotationType().equals(Inject.class)) {
                    return constructor;
                }
            }
        }

        //If not use the default constructor
        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length < 1) {
                return constructor;
            }
        }

        throw new RuntimeException("Class " + tClass + " cannot be Injected");
    }

    /**
     * @Returns the instance the requested singleton
     */
    @Nullable
    private <T> T getSingleton(Class<T> tClass) {
        for (HashMap.Entry<Class, Object> singleton : singletons.entrySet()) {
            if (singleton.getKey().isAssignableFrom(tClass)) {
                return (T) singleton.getValue();
            }
        }

        return null;
    }
}
