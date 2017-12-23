package net.axeora.eternallight.handle;

public class ListItem {

    private String tag;
    private byte level;

    public ListItem(String tag, byte level) {
        this.tag = tag;
        this.level = level;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }
}
