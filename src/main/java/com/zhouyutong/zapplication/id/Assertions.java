package com.zhouyutong.zapplication.id;

/**
 * <p>Design by contract assertions.</p> <p>This class is not part of the public API and may be removed or changed at any time.</p>
 */
public final class Assertions {
    /**
     * Throw IllegalArgumentException if the value is null.
     *
     * @param name  the parameter name
     * @param value the value that should not be null
     * @param <T>   the value type
     * @return the value
     * @throws IllegalArgumentException if value is null
     */
    public static <T> T notNull(final String name, final T value) {
        if (value == null) {
            throw new IllegalArgumentException(name + " can not be null");
        }
        return value;
    }

    /**
     * Throw IllegalStateException if the condition if false.
     *
     * @param name      the name of the state that is being checked
     * @param condition the condition about the parameter to check
     * @throws IllegalStateException if the condition is false
     */
    public static void isTrue(final String name, final boolean condition) {
        if (!condition) {
            throw new IllegalStateException("state should be: " + name);
        }
    }

    /**
     * Throw IllegalArgumentException if the condition if false.
     *
     * @param name      the name of the state that is being checked
     * @param condition the condition about the parameter to check
     * @throws IllegalArgumentException if the condition is false
     */
    public static void isTrueArgument(final String name, final boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException("state should be: " + name);
        }
    }

    /**
     * Cast an object to the given class and return it, or throw IllegalArgumentException if it's not assignable to that class.
     *
     * @param clazz        the class to cast to
     * @param value        the value to cast
     * @param errorMessage the error message to include in the exception
     * @param <T>          the Class type
     * @return value cast to clazz
     * @throws IllegalArgumentException if value is not assignable to clazz
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertToType(final Class<T> clazz, final Object value, final String errorMessage) {
        if (!clazz.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(errorMessage);
        }
        return (T) value;
    }

    private Assertions() {
    }
}