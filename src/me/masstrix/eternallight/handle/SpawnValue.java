package me.masstrix.eternallight.handle;

import me.masstrix.eternallight.EternalLight;
import org.bukkit.Material;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public enum SpawnValue {
    /**
     * Block is ignored in spawning rules. For example Grass and Flowers.
     */
    TRANSPARENT,

    /**
     * Block cannot have mobs spawn on it.
     */
    NEVER,

    /**
     * Block can have mobs spawn on it.
     */
    ALWAYS;

    public static SpawnValue findOrDefault(String s, SpawnValue def) {
        for (SpawnValue v : values()) {
            if (v.name().equalsIgnoreCase(s)) return v;
        }
        return def;
    }

    private static Map<Material, SpawnValue> mapping = new HashMap<>();

    /**
     * Reads the mapping.txt file in the plugins folder and applies them to each material in the game.
     * This saves on computation time when rendering light levels.
     *
     * @param plugin plugin instance.
     * @return if the mappings were successfully loaded and applied.
     */
    public static boolean loadMappings(EternalLight plugin) {
        plugin.getLogger().info("Loading material mappings...");

        Set<MaterialMatcher> blocks = new HashSet<>();
        AtomicInteger loadedMappings = new AtomicInteger(0);

        try {
            // Get mappings file
            File mappingsFile = new File(plugin.getDataFolder(), "mapping.txt");
            if (!mappingsFile.exists()) {
                plugin.saveResource("mapping.txt", false);
            }

            // Read and load mappings
            BufferedReader reader = new BufferedReader(new FileReader(mappingsFile));
            reader.lines().forEach(line -> {
                line = line.replaceAll(" ", "");
                String[] v = line.split(":");
                blocks.add(new MaterialMatcher(v[0].replaceAll("\\*", "(.*)"), findOrDefault(v[1], ALWAYS)));
                loadedMappings.incrementAndGet();
            });
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Cache materials to a mapping
        for (Material mat : Material.values()) {
            if (mat.name().startsWith("LEGACY")) continue; // Ignore legacy stuff
            if (mat.name().contains("AIR")) {
                mapping.put(mat, TRANSPARENT);
                continue;
            } else {
                boolean found = false;
                for (MaterialMatcher key : blocks) {
                    if (key.equals(mat)) {
                        found = true;
                        mapping.put(mat, key.getValue());
                        break;
                    }
                }
                if (found) continue;
            }
            mapping.put(mat, ALWAYS);
        }

        blocks.clear();
        plugin.getLogger().info(String.format("Loaded and applied %s mappings", loadedMappings.get()));
        return true;
    }

    /**
     * @param material material to check for.
     * @return the specified value for the block.
     */
    public static SpawnValue get(Material material) {
        return mapping.getOrDefault(material, ALWAYS);
    }
}
