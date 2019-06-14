package me.masstrix.eternallight.handle;

import java.util.ArrayList;
import java.util.List;

public enum DisplayMethod {
    /**
     * Display the overlay to show all spawn-able areas for hostile mobs.
     * These areas are displayed as yellow; only able to spawn during night
     * while red shows they can spawn at any time.
     */
    SPAWNABLE,

    /**
     * Displays much like {@code SPAWNABLE} but instead also shows what areas are
     * not spawn-able high lighted with a green particle.
     */
    ALL,

    /**
     * Displays a smoothed out version of {@code ALL}, fading from each color.
     */
    LIGHTLEVEL;

    private static List<String> options = new ArrayList<>();

    static {
        for (DisplayMethod m : values()) {
            options.add(m.name().substring(0, 1) + m.name().substring(1).toLowerCase());
        }
    }

    public static List<String> getOptions() {
        return options;
    }

    private static DisplayMethod[] values = values();

    public static DisplayMethod find(String s) {
        if (s == null) return SPAWNABLE;
        String find = s.toLowerCase();
        for (DisplayMethod type : DisplayMethod.values()) {
            String typeL = type.name().toLowerCase();
            if (typeL.contains(find) || find.contains(typeL)) return type;
        }
        return SPAWNABLE;
    }

    public DisplayMethod next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
