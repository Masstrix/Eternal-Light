package net.axeora.eternallight.handle;

public enum DisplayMethod {
    /**
     *
     */
    NORMAL,
    /**
     *
     */
    INCLUSIVE,
    /**
     *
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
