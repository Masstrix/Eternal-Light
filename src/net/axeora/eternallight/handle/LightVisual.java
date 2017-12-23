package net.axeora.eternallight.handle;

import net.axeora.eternallight.EternalLight;
import net.axeora.eternallight.EternalLightConfig;
import net.axeora.eternallight.util.RGBParticle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.util.*;

public class LightVisual {

    private static Set<ListItem> blocks = new HashSet<>();
    private final byte[] VALID_STAIR = new byte[] {4, 5, 6, 7};

    static {
        // 2 = transparent & solid
        // 1 = transparent
        // 0 = opaque
        blocks.addAll(Arrays.asList(
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

    private final UUID PLAYER_UUID;
    private Player player;
    private boolean enabled = false;
    private DisplayMethod type = DisplayMethod.NORMAL;
    private EternalLightConfig config = EternalLight.getInstance().getPluginConfig();

    public LightVisual(UUID uuid) {
        PLAYER_UUID = uuid;
        player = Bukkit.getPlayer(uuid);
    }

    /**
     * @return if the visual display is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enable the and show the light levels as a visualized display.
     */
    public void show() {
        enabled = true;
        update();
    }

    /**
     * Disable the light level display.
     */
    public void hide() {
        if (!enabled) return;
        enabled = false;
        update();
    }

    /**
     * @return the state of the visual before it was toggled.
     */
    public boolean toggle() {
        enabled = !enabled;
        if (enabled) show();
        else hide();
        return !enabled;
    }

    /**
     * Send an update to the player of the surrounding systems.
     */
    public void update() {
        if (enabled) send();
    }

    /**
     * Sets the display type of the projector.
     *
     * @param type what display method will be used to render light levels.
     */
    public void setType(DisplayMethod type) {
        this.type = type;
    }

    /**
     * Toggle between display modes.
     */
    public void toggleType() {
        this.type = type.next();
    }

    /**
     * @return the current display method being used.
     */
    public DisplayMethod getType() {
        return type;
    }

    private void send() {
        if (this.player == null) {
            EternalLight.getInstance().getProjector().remove(PLAYER_UUID);
            return;
        }
        Location loc = player.getLocation().clone();
        World world = loc.getWorld();
        int px = loc.getBlockX(), py = loc.getBlockY(), pz = loc.getBlockZ();
        int RAD = config.getRadius();
        for (int z = -RAD; z <= RAD; z++) {
            for (int x = -RAD; x <= RAD; x++) {
                for (int y = -RAD; y <= RAD; y++) {
                    Block block = world.getBlockAt(px + x, py + y, pz + z);
                    Material type = block.getType();
                    if (type == Material.AIR  || block.isLiquid()) continue;
                    if (block.getType() == Material.SNOW && block.getData() < 7) continue;
                    int opacity = getBlockOpacity(type);
                    if (opacity > 0) continue;
                    if (!isStairInSpawnRotation(block)) continue;
                    if (getBlockHeight(block) == 0.5) continue;

                    // Validate if there is a 2 block gap above for mobs to spawn.
                    boolean valid = true;
                    for (int yValid = 1; yValid <= 2; yValid++) {
                        Block above = world.getBlockAt(px + x, (py + y) + yValid, pz + z);
                        if (above.getType() == Material.AIR) continue;
                        int val = getBlockOpacity(above.getType());
                        if (val == 0 || val == -1 || val == 2) {
                            valid = false;
                            break;
                        }
                    }
                    if (!valid) continue;

                    Block onTop = world.getBlockAt(px + x, (py + y) + 1, pz + z);

                    RGBParticle particle = new RGBParticle();
                    if (this.type == DisplayMethod.NORMAL) {
                        LightSpawnCase spawnCase = LightSpawnCase.getCase(onTop);
                        if (spawnCase == LightSpawnCase.NEVER) continue;
                        particle.setColor(spawnCase.color);
                    }
                    else if (this.type == DisplayMethod.INCLUSIVE) {
                        LightSpawnCase spawnCase = LightSpawnCase.getCase(onTop);
                        particle.setColor(spawnCase.color);
                    } else if (this.type == DisplayMethod.SMOOTH) {
                        double lightLevel = onTop.getLightFromBlocks();

                        double p = lightLevel / 14;
                        particle.setRed(lightLevel > 10 ? (1D - ((lightLevel - 10)  / 4D)) : 1D);
                        particle.setGreen(p);
                    }
                    particle.send(player, (px + x) + 0.5, (py + y) + getBlockHeight(block) + 0.2, (pz + z) + 0.5);
                }
            }
        }
    }

    private int getBlockOpacity(Material material) {
        String n = material.name().toLowerCase();
        for (ListItem s : blocks) {
            if (n.contains(s.getTag().toLowerCase())) {
                return s.getLevel();
            }
        }
        return -1;
    }

    private double getBlockHeight(Block block) {
        Material material = block.getType();
        String n = material.name().toLowerCase();
        byte data = block.getData();
        if (!n.contains("double") && (n.contains("step") || n.contains("slab"))) {
            if (data < 8) return .5;
        }
        return 1;
    }

    private boolean isStairInSpawnRotation(Block block) {
        String n = block.getType().name();
        byte data = block.getData();
        if (n.toLowerCase().contains("stairs")) {
            for (byte b : VALID_STAIR)
                if (data == b) return true;
        } else {
            return true;
        }
        return false;
    }

    private enum LightSpawnCase {
        NIGHT_SPAWN(Color.YELLOW), NEVER(Color.GREEN), ALWAYS(Color.RED);

        Color color;

        LightSpawnCase(Color color) {
            this.color = color;
        }

        public static LightSpawnCase getCase(Block block) {
            if (block.getLightFromBlocks() > 7) return NEVER;
            if (block.getLightFromSky() > 7) return NIGHT_SPAWN;
            else return ALWAYS;
        }
    }
}
