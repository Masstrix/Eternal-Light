package me.masstrix.eternallight.util;

public class EnumUtil {

    public static <T extends Enum<?>> T getValue(Class<T> clazz, String name) {
        return getValue(clazz, name, false);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T getValue(Class<T> clazz, String name, boolean ignoreCase) {
        Enum<?>[] constants = clazz.getEnumConstants();
        for (Enum<?> e : constants) {
            if (ignoreCase) {
                if (e.name().equalsIgnoreCase(name))
                    return (T) e;
            }
            if (e.name().equals(name)) {
                return (T) e;
            }
        }
        return null;
    }
}
