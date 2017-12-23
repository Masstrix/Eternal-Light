package net.axeora.eternallight.handle;

public enum DisplayMethod {
    /**
     * Display the overlay to show all spawn-able areas for hostile mobs.
     * These areas are displayed as yellow; only able to spawn during night
     * while red shows they can spawn at any time.
     */
    NORMAL,

    /**
     * Displays much like {@code NORMAL} but instead also shows what areas are
     * not spawn-able high lighted with a green particle.
     */
    INCLUSIVE,

    /**
     * Displays a smoothed out version of {@code INCLUSIVE}, fading from each color.
     */
    SMOOTH;

    private static DisplayMethod[] values = values();

    public static DisplayMethod find(String s) {
        String find = s.toLowerCase();
        for (DisplayMethod type : DisplayMethod.values()) {
            String typeL = type.name().toLowerCase();
            if (typeL.contains(find) || find.contains(typeL)) return type;
        }
        return NORMAL;
    }

    public DisplayMethod next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
