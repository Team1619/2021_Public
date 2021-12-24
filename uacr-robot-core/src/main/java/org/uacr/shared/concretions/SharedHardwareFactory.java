package org.uacr.shared.concretions;

import org.uacr.shared.abstractions.HardwareFactory;
import org.uacr.utilities.MultiObjectKey;
import org.uacr.utilities.injection.Inject;
import org.uacr.utilities.injection.Singleton;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Handles the instantiation and storing of external hardware objects
 */

@Singleton
public class SharedHardwareFactory implements HardwareFactory {

    private final HashMap<MultiObjectKey, Object> objectMap;

    @Inject
    public SharedHardwareFactory() {
        objectMap = new HashMap<>();
    }

    /**
     * Finds, instantiates, stores, and returns external hardware objects for framework hardware objects
     *
     * @param tClass the class of the external hardware object to be returned
     * @param parameters the constructor parameters of the external hardware object to be returned
     * @param <T> the type of the object to be returned
     * @return an instance of the requested class
     */
    public <T> T get(Class<T> tClass, Object... parameters) {
        MultiObjectKey key = createKey(tClass, parameters);

        if (objectMap.containsKey(key)) {
            @SuppressWarnings("unchecked")
            T tObject = (T) objectMap.get(key);
            return tObject;
        }

        T tObject = construct(tClass, parameters);
        objectMap.put(key, tObject);
        return tObject;
    }

    /**
     * Instantiates a hardware object of type tClass with the provided parameters
     *
     * @param tClass the class to be instantiated
     * @param parameters the parameters to instantiate the hardware object
     * @param <T> the type of hardware object to be instantiated
     * @return the new hardware object
     */
    private <T> T construct(Class<T> tClass, Object... parameters) {
        Class<?>[] parameterTypes = getParameterTypes(parameters);
        Constructor<?> tConstructor = getConstructor(tClass, parameterTypes);
        try {
            @SuppressWarnings("unchecked")
            T tObject = (T) tConstructor.newInstance(parameters);
            return tObject;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Arrays.toString(tConstructor.getParameterTypes()));
            throw new RuntimeException("Invalid Configuration when creating " + tClass.getName() + " with parameters " + Arrays.toString(parameters));
        }
    }

    /**
     * Finds the correct constructor of tClass with the provided parameterTypes
     *
     * @param tClass the class to find the constructor of
     * @param parameterTypes the types of the parameters used to find the correct constructor
     * @return the correct constructor of the object
     */
    private Constructor<?> getConstructor(Class<?> tClass, Class<?>[] parameterTypes) {
        Constructor<?>[] constructors = tClass.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (parameterTypesMatch(constructor.getParameterTypes(), parameterTypes)) {
                return constructor;
            }
        }

        throw new RuntimeException("No constructor found for " + tClass);
    }

    /**
     * Compares two lists of parameter to find the correct constructor of an object
     *
     * @param constructorParameterTypes a list of constructor parameters types
     * @param objectParameterTypes a list of parameters types
     * @return whether the objectParameterTypes are compatible with the constructorParameterTypes
     */
    private boolean parameterTypesMatch(Class<?>[] constructorParameterTypes, Class<?>[] objectParameterTypes) {
        if (constructorParameterTypes.length != objectParameterTypes.length) {
            return false;
        }
        for (int p = 0; p < constructorParameterTypes.length; p++) {
            if (!convertToNonPrimitiveClass(constructorParameterTypes[p]).isAssignableFrom(convertToNonPrimitiveClass(objectParameterTypes[p]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a class is primitive and if it is, converts the primitive class to the primitive's wrapper class
     *
     * @param tClass the class which could be primitive
     * @return the original class or a primitive wrapper if the original class was a primitive
     */
    private Class<?> convertToNonPrimitiveClass(Class<?> tClass) {
        if(tClass == int.class) {
            return Integer.class;
        }

        if(tClass == short.class) {
            return Short.class;
        }

        if(tClass == long.class) {
            return Long.class;
        }

        if(tClass == float.class) {
            return Float.class;
        }

        if(tClass == double.class) {
            return Double.class;
        }

        if(tClass == byte.class) {
            return Byte.class;
        }

        if(tClass == char.class) {
            return Character.class;
        }

        if(tClass == boolean.class) {
            return Boolean.class;
        }

        return tClass;
    }

    /**
     * Converts an array of parameters to an array of the classes of the original parameters
     *
     * @param parameters the parameters to be converted classes
     * @return an array of classes corresponding to the original parameters
     */
    private Class<?>[] getParameterTypes(Object[] parameters) {
        Class<?>[] parameterClasses = new Class[parameters.length];

        for (int p = 0; p < parameters.length; p++) {
            parameterClasses[p] = parameters[p].getClass();
        }

        return parameterClasses;
    }

    /**
     * Creates a MultiObjectKey so hardware objects can be stored and referenced in a Map
     *
     * @param tClass the class of the hardware object
     * @param parameters the parameters of the hardware object
     * @return a MultiObjectKey corresponding to the class and parameters of the hardware object
     */
    private MultiObjectKey createKey(Class<?> tClass, Object[] parameters) {
        return new MultiObjectKey(tClass, parameters);
    }
}
