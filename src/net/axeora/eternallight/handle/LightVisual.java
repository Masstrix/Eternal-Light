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

    private static final List<String> BLACKLIST = new ArrayList<>();
    private static final List<String> TRANSPARENT = new ArrayList<>();

    static {
        BLACKLIST.addAll(Arrays.asList("plant", "carpet", "long_grass",
                "rose", "flower", "rail", "leaves", "glass", "rod", "fence", "door", "pressure", "lily"));
        TRANSPARENT.addAll(Arrays.asList("glass", "leaves", "rod", "fence", "door", "pressure"));
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
                    if (isBlacklisted(type)) continue;
                    Block onTop = world.getBlockAt(px + x, (py + y) + 1, pz + z);
                    if (isTransparent(onTop.getType()) && onTop.getType() != Material.AIR)
                        continue;

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
                        particle.setRed(lightLevel > 10 ? (1D - p) : 1D);
                        particle.setGreen(p);
                    }
                    particle.send(player, (px + x) + 0.5, (py + y) + getBlockHeight(block) + 0.2, (pz + z) + 0.5);
                }
            }
        }
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

    private boolean isBlacklisted(Material material) {
        String n = material.name().toLowerCase();
        for (String s : BLACKLIST) {
            if (n.contains(s.toLowerCase())) return true;
        }
        return false;
    }

    private boolean isTransparent(Material material) {
        String n = material.name().toLowerCase();
        for (String s : TRANSPARENT) {
            if (n.contains(s.toLowerCase())) return true;
        }
        return false;
    }

    private double getBlockHeight(Block block) {
        Material material = block.getType();
        String n = material.name().toLowerCase();
        byte data = block.getData();
        if ((!n.contains("double") && n.contains("step")) && data < 8) return .5;
        return 1;
    }
}
