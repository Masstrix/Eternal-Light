package me.masstrix.eternallight;

import me.masstrix.eternallight.handle.BlockOpacityData;
import me.masstrix.eternallight.handle.SpawnValue;
import org.bukkit.Material;

import java.util.*;

public class EternalLightAPI {

    private static Set<BlockOpacityData> blocks = new HashSet<>();

    EternalLightAPI() {

        setAllTags(Arrays.asList(
                new BlockOpacityData("pot", (byte) 2),
                new BlockOpacityData("pot", (byte) 2),
                new BlockOpacityData("web", (byte) 2),
                new BlockOpacityData("glass", (byte) 2),
                new BlockOpacityData("glowstone", (byte) 1),
                new BlockOpacityData("ice", (byte) 1),
                new BlockOpacityData("lantern", (byte) 1),
                new BlockOpacityData("leaves", (byte) 2),
                new BlockOpacityData("piston", (byte) 2),
                new BlockOpacityData("lamp", (byte) 2),
                new BlockOpacityData("tnt", (byte) 1),
                new BlockOpacityData("anvil", (byte) 2),
                new BlockOpacityData("bars", (byte) 2),
                new BlockOpacityData("bed", (byte) 1),
                new BlockOpacityData("glowstone", (byte) 1),
                new BlockOpacityData("cake", (byte) 1),
                new BlockOpacityData("carpet", (byte) 2),
                new BlockOpacityData("cauldron", (byte) 1),
                new BlockOpacityData("chest", (byte) 2),
                new BlockOpacityData("wall", (byte) 1),
                new BlockOpacityData("detector", (byte) 1),
                new BlockOpacityData("door", (byte) 2),
                new BlockOpacityData("enchantment", (byte) 1),
                new BlockOpacityData("farm", (byte) 1),
                new BlockOpacityData("fence", (byte) 2),
                new BlockOpacityData("lily", (byte) 2),
                new BlockOpacityData("repeater", (byte) 1),
                new BlockOpacityData("comparator", (byte) 2),
                new BlockOpacityData("snow_layer", (byte) 1),
                new BlockOpacityData("vine", (byte) 1),
                new BlockOpacityData("button", (byte) 1),
                new BlockOpacityData("lever", (byte) 1),
                new BlockOpacityData("pressure_plate", (byte) 2),
                new BlockOpacityData("rail", (byte) 1),
                new BlockOpacityData("repeater", (byte) 1),
                new BlockOpacityData("redstone_block", (byte) 0),
                new BlockOpacityData("redstone_wire", (byte) 2),
                new BlockOpacityData("torch", (byte) 1),
                new BlockOpacityData("dust", (byte) 2),
                new BlockOpacityData("portal", (byte) 1),
                new BlockOpacityData("fire", (byte) 1),
                new BlockOpacityData("ladder", (byte) 1),
                new BlockOpacityData("sign", (byte) 1),
                new BlockOpacityData("torch", (byte) 1),
                new BlockOpacityData("cactus", (byte) 1),
                new BlockOpacityData("crop", (byte) 2),
                new BlockOpacityData("potato", (byte) 2),
                new BlockOpacityData("beetroot", (byte) 2),
                new BlockOpacityData("flower", (byte) 1),
                new BlockOpacityData("rose", (byte) 1),
                new BlockOpacityData("long_grass", (byte) 1),
                new BlockOpacityData("plant", (byte) 1),
                new BlockOpacityData("mushroom", (byte) 1),
                new BlockOpacityData("diode", (byte) 2),
                new BlockOpacityData("plate", (byte) 2),
                new BlockOpacityData("end_rod", (byte) 2),
                new BlockOpacityData("sapling", (byte) 1),
                new BlockOpacityData("sugar", (byte) 1),
                new BlockOpacityData("spawner", (byte) 1),
                new BlockOpacityData("tripwire", (byte) 1),
                new BlockOpacityData("banner", (byte) 1)));
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
        for (BlockOpacityData l : blocks) {
            if (l.getTag().equalsIgnoreCase(tag)) {
                l.setLevel(value);
                return;
            }
        }
        blocks.add(new BlockOpacityData(tag, value));
    }

    /**
     * Set a list of tag values. If the tag has not been added already then a new
     * value will be added to the list otherwise the old one is updated.
     *
     * @param items list of all tags being set.
     */
    public void setAllTags(List<BlockOpacityData> items) {
        for (BlockOpacityData i : items)
            setTag(i.getTag(), i.getLevel());
    }

    /**
     * Return a tag from cache as an entire {@link BlockOpacityData}.
     *
     * @param tag what the index of the tag is.
     * @return the found item or null if it does not exist.
     */
    public BlockOpacityData getTag(String tag) {
        for (BlockOpacityData l : blocks) {
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
        Set<BlockOpacityData> remove = new HashSet<>();
        for (BlockOpacityData l : blocks) {
            if (l.getTag().matches(regex)) remove.add(l);
        }
        blocks.removeAll(remove);
    }
}
