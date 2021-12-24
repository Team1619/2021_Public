package org.uacr.shared.abstractions;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores all the configuration values from the robot-configuration.yaml file
 */

public interface RobotConfiguration {

    @Deprecated
    void initialize();

    Map<String, Set<String>> getStateNamesWithPriority();

    Set<String> getStateNames();

    Set<String> getSubsystemNames();

    Set<String> getInputBooleanNames();

    Set<String> getInputNumericNames();

    Set<String> getInputVectorNames();

    Set<String> getOutputNumericNames();

    Set<String> getOutputBooleanNames();

    Object get(String category, String key);

    Map<String, Object> getCategory(String category);

    int getInt(String category, String key);

    double getDouble(String category, String key);

    boolean getBoolean(String category, String key);

    String getString(String category, String key);

    <T> List getList(String category, String key);

    <K, V> Map<K, V> getMap(String category, String key);

    <T> Set getSet(String category, String key);

    <T extends Enum<T>> T getEnum(String category, String key, Class<T> enumClass);

    boolean contains(String category, String key);

    boolean categoryIsEmpty(String category);
}

