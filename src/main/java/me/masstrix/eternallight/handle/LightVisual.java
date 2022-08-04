package me.masstrix.eternallight.handle;

import me.masstrix.eternallight.EternalLightConfig;
import me.masstrix.eternallight.util.RGBParticle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.util.*;

/**
 * Light Visual is a class that handles displaying the visual display to a player.
 * Each player has their own individual LightVisual instance.
 */
public class LightVisual {

    private final UUID PLAYER_UUID;
    private Projector projector;
    private Player player;
    private boolean enabled = false;
    private DisplayMethod method;
    private EternalLightConfig config;
    private EntityType targetType = null;

    public LightVisual(Projector projector, UUID uuid) {
        this.projector = projector;
        PLAYER_UUID = uuid;
        player = Bukkit.getPlayer(uuid);
        config = projector.plugin.getPluginConfig();
        method = config.getDefaultMode();
    }

    /**
     * @return if the visual display is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return the selected target entity type or null if none.
     */
    public EntityType getTargetType() {
        return targetType;
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
     * Sets a target entity type to only displace spawning conditions for
     * that entity. Set to null to disable entity targeting.
     *
     * @param type entity type to target the display at.
     */
    public void setTargetEntityType(EntityType type) {
        targetType = type;
    }

    /**
     * Send an update to the player of the surrounding systems.
     */
    public void update() {
        if (enabled) render();
    }

    /**
     * Sets the display type of the projector.
     *
     * @param method what display method will be used to render light levels.
     */
    public void setMethod(DisplayMethod method) {
        this.method = method;
    }

    /**
     * Toggle between display modes.
     */
    public void toggleType() {
        this.method = method.next();
    }

    /**
     * @return the current display method being used.
     */
    public DisplayMethod getMethod() {
        return method;
    }

    /**
     * Scans al nearby blocks in the given radius and height values defined in
     * the config and displays there light levels as particles.
     */
    private void render() {
        if (this.player == null) {
            projector.remove(PLAYER_UUID);
            return;
        }
        Location loc = player.getLocation().clone();
        World world = loc.getWorld();
        int px = loc.getBlockX(), py = loc.getBlockY(), pz = loc.getBlockZ();
        int radA = config.getScannerRadius();
        int radH = config.getScannerRadius();

        RGBParticle particle = new RGBParticle(0, 0, 0);

        boolean spherical = config.isScannerSpherical();

        for (int z = -radA; z <= radA; z++) {
            for (int x = -radA; x <= radA; x++) {
                for (int y = -radH; y <= radH; y++) {
                    if (spherical && Math.sqrt((x * x) + (y * y) + (z * z)) > radA) continue;
                    assert world != null;
                    Block block = world.getBlockAt(px + x, py + y, pz + z);
                    SpawnValue spawnValue = SpawnValue.get(block.getType());
                    BlockData blockData = block.getBlockData();

                    // Check if block is solid or closed (for fence gates, doors etc.)
                    if (blockData instanceof Openable && !((Openable) blockData).isOpen()) continue;
                    if (block.isPassable()) continue;

                    // Update for stairs
                    if (blockData instanceof Stairs) {
                        if (isStairInSpawnRotation(block)) spawnValue = SpawnValue.ALWAYS;
                        else continue;
                    }

                    // Check slab position
                    if (this.method != DisplayMethod.LIGHTLEVEL && blockData instanceof Slab) {
                        Slab slab = (Slab) blockData;
                        if (slab.getType() == Slab.Type.BOTTOM) continue;
                    }

                    if (spawnValue != SpawnValue.ALWAYS) continue;

                    // Validate if there is a 2 block gap above for mobs to spawn.
                    boolean valid = true;
                    for (int yValid = 1; yValid <= 2; yValid++) {
                        Block above = world.getBlockAt(px + x, (py + y) + yValid, pz + z);
                        if (above.getType() == Material.AIR || above.getType() == Material.CAVE_AIR) continue;
                        if (above.isPassable()) continue;
                        SpawnValue sv = SpawnValue.get(above.getType());
                        if (sv != SpawnValue.TRANSPARENT) {
                            valid = false;
                            break;
                        }
                    }
                    if (!valid) continue;

                    Block onTop = world.getBlockAt(px + x, (py + y) + 1, pz + z);

                    // Render the particles depending on the selected method.
                    switch (this.method) {
                        case ALL -> {
                            LightSpawnCase spawnCase;
                            if (targetType != null) {
                                spawnCase = SpawnConditions.canSpawnAt(targetType, onTop);
                            } else {
                                spawnCase = SpawnConditions.getSpawnCase(onTop);
                            }
                            particle.setColor(spawnCase.color);
                        }
                        case SPAWNABLE -> {
                            LightSpawnCase spawnCase;
                            if (targetType != null) {
                                spawnCase = SpawnConditions.canSpawnAt(targetType, onTop);
                            } else {
                                spawnCase = SpawnConditions.getSpawnCase(onTop);
                            }
                            if (spawnCase == LightSpawnCase.NEVER) continue;
                            particle.setColor(spawnCase.color);
                        }
                        case LIGHTLEVEL -> {
                            float p = (float) onTop.getLightFromBlocks() / 14F;
                            Color c = new Color(255, 0, 6);

                            // Get saturation and brightness.
                            float[] hsbVals = new float[3];
                            Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbVals);

                            // Shift the hue around by 25%
                            c = new Color(Color.HSBtoRGB((0.25f * p), hsbVals[1], hsbVals[2]));
                            particle.setColor(c);
                        }
                    }
                    particle.send(player, (px + x) + 0.5, (py + y) + getBlockHeight(block) + 0.2, (pz + z) + 0.5);
                }
            }
        }
    }

    /**
     * @param block block to get height of.
     * @return the height of the given block.
     */
    private double getBlockHeight(Block block) {
        BlockData data = block.getBlockData();
        if (data instanceof Slab) {
            Slab slab = (Slab) data;
            return slab.getType() == Slab.Type.BOTTOM ? 0.5 : 1;
        }
        else if (data instanceof Snow) {
            Snow snow = (Snow) data;
            return (double) snow.getLayers() / (double) snow.getMaximumLayers();
        }
        return 1;
    }

    private boolean isStairInSpawnRotation(Block block) {
        BlockData data = block.getBlockData();
        if (data instanceof Stairs) {
            Stairs stair = (Stairs) data;
            return stair.getHalf() == Bisected.Half.TOP;
        }
        return false;
    }
}
