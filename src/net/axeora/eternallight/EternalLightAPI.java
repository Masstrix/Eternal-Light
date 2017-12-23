package net.axeora.eternallight;

import net.axeora.eternallight.handle.ListItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EternalLightAPI {

    private static Set<ListItem> blocks = new HashSet<>();
    private final byte[] VALID_STAIR = new byte[] {4, 5, 6, 7};

    EternalLightAPI() {
        setAllTags(Arrays.asList(
                new ListItem("pot", (byte) 2),
                new ListItem("web", (byte) 2),
                new ListItem("glass", (byte) 2),
                new ListItem("glowstone", (byte) 1),
                new ListItem("ice", (byte) 1),
                new ListItem("lantern", (byte) 1),
                new ListItem("leaves", (byte) 2),
                new ListItem("piston", (byte) 2),
                new ListItem("lamp", (byte) 2),
                new ListItem("tnt", (byte) 1),
                new ListItem("anvil", (byte) 2),
                new ListItem("bars", (byte) 2),
                new ListItem("bed", (byte) 1),
                new ListItem("glowstone", (byte) 1),
                new ListItem("cake", (byte) 1),
                new ListItem("carpet", (byte) 2),
                new ListItem("cauldron", (byte) 1),
                new ListItem("chest", (byte) 2),
                new ListItem("wall", (byte) 1),
                new ListItem("detector", (byte) 1),
                new ListItem("door", (byte) 2),
                new ListItem("enchantment", (byte) 1),
                new ListItem("farm", (byte) 1),
                new ListItem("fence", (byte) 2),
                new ListItem("lily", (byte) 2),
                new ListItem("repeater", (byte) 1),
                new ListItem("comparator", (byte) 2),
                new ListItem("snow_layer", (byte) 1),
                new ListItem("vine", (byte) 1),
                new ListItem("button", (byte) 1),
                new ListItem("lever", (byte) 1),
                new ListItem("pressure_plate", (byte) 2),
                new ListItem("rail", (byte) 1),
                new ListItem("repeater", (byte) 1),
                new ListItem("redstone_block", (byte) 0),
                new ListItem("redstone_wire", (byte) 2),
                new ListItem("torch", (byte) 1),
                new ListItem("dust", (byte) 2),
                new ListItem("portal", (byte) 1),
                new ListItem("fire", (byte) 1),
                new ListItem("ladder", (byte) 1),
                new ListItem("sign", (byte) 1),
                new ListItem("torch", (byte) 1),
                new ListItem("cactus", (byte) 1),
                new ListItem("crop", (byte) 2),
                new ListItem("potato", (byte) 2),
                new ListItem("beetroot", (byte) 2),
                new ListItem("flower", (byte) 1),
                new ListItem("rose", (byte) 1),
                new ListItem("long_grass", (byte) 1),
                new ListItem("plant", (byte) 1),
                new ListItem("mushroom", (byte) 1),
                new ListItem("diode", (byte) 2),
                new ListItem("plate", (byte) 2),
                new ListItem("end_rod", (byte) 2),
                new ListItem("sapling", (byte) 1),
                new ListItem("sugar", (byte) 1),
                new ListItem("spawner", (byte) 1),
                new ListItem("tripwire", (byte) 1),
                new ListItem("banner", (byte) 1)));
    }

    /**
     * Set a tags value. If the tag has not been added already then a new
     * value will be added to the list otherwise the old one is updated.
     *
     * 0 = Opaque.
     * 1 = Transparent.
     * 2 = Transparent & Solid.
     *
     * @param tag index value of the tag.
     * @param value what the new value is. This value must be between 0 and 2.
     */
    public void setTag(String tag, byte value) {
        if (value > 2) value = 2;
        else if (value < 0) value = 0;
        for (ListItem l : blocks) {
            if (l.getTag().equalsIgnoreCase(tag)) {
                l.setLevel(value);
                return;
            }
        }
        blocks.add(new ListItem(tag, value));
    }

    /**
     * Set a list of tag values. If the tag has not been added already then a new
     * value will be added to the list otherwise the old one is updated.
     *
     * @param items list of all tags being set.
     */
    public void setAllTags(List<ListItem> items) {
        for (ListItem i : items)
            setTag(i.getTag(), i.getLevel());
    }

    /**
     * Return a tag from cache as an entire {@link ListItem}.
     *
     * @param tag what the index of the tag is.
     * @return the found item or null if it does not exist.
     */
    public ListItem getTag(String tag) {
        for (ListItem l : blocks) {
            if (l.getTag().equalsIgnoreCase(tag)) return l;
        }
        return null;
    }

    /**
     * Remove all items from the block list matching the given regex.
     *
     * @param regex regex remove parameters. To remove all blocks that contain glass
     *              in the tag you can do {@code *glass*}.
     */
    public void removeTag(String regex) {
        Set<ListItem> remove = new HashSet<>();
        for (ListItem l : blocks) {
            if (l.getTag().matches(regex)) remove.add(l);
        }
        blocks.removeAll(remove);
    }

    /**
     * @return all tags.
     */
    public Set<ListItem> getBlocks() {
        return blocks;
    }

    /**
     * @return all rotational data values of a stair block mobs can spawn on/
     */
    public byte[] getValidStairRotations() {
        return VALID_STAIR;
    }
}
