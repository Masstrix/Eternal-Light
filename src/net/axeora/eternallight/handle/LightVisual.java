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
        Set<ListItem> blocks = EternalLight.getInstance().getAPI().getBlocks();
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
            byte[] valid = EternalLight.getInstance().getAPI().getValidStairRotations();
            for (byte b : valid)
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
