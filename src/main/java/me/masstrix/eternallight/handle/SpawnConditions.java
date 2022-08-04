package me.masstrix.eternallight.handle;

import me.masstrix.eternallight.EternalLight;
import me.masstrix.eternallight.util.EnumUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.*;

public class SpawnConditions {

    // Stores special cases for specific mob types.
    private static final Map<EntityType, SpawnConditions> SPECIAL_CASE = new HashMap<>();
    private static SpawnConditions DEFAULT = new SpawnConditions(0, 0, 0);

    // Stores a list of all the mobs that can spawn in each environment
    private static final Set<EntityType> MOBS_OVER_WORLD = new HashSet<>();
    private static final Set<EntityType> MOBS_NETHER = new HashSet<>();
    private static final Set<EntityType> MOBS_END = new HashSet<>();

    // Cache for if the server is legacy.
    private static boolean isLegacy = false;

    // Defines the light levels for where mobs will spawn up to. Any light level higher
    // than what is set here would stop mobs from spawning,
    // Any value that is -1 is unset and will go back to call default.
    private int normal;
    private int nether;
    private int end;
    private boolean isDefault = false;

    private SpawnConditions() {
    }

    private SpawnConditions(int normal, int nether, int end) {
        this.normal = normal;
        this.nether = nether;
        this.end = end;
    }

    /**
     * Loads a list of mobs into a set to categorize where mob types spawn
     * for selective mob visualizing.
     *
     * @param list list of entities to read in.
     * @param set set to put the entities into.
     */
    private static void loadMobsFromList(List<?> list, Set<EntityType> set) {
        if (list == null)
            return;
        for (Object obj : list) {

            // Add entity to list
            String name = String.valueOf(obj);
            EntityType type = EnumUtil.getValue(EntityType.class, name, true);
            if (type == null)
                continue;

            // Adds entity to the set
            set.add(type);

            // Creates a special case for the entity
            if (obj instanceof ConfigurationSection section) {
                int overWorld = section.getInt("overworld", -1);
                int nether = section.getInt("nether", -1);
                int end = section.getInt("end", -1);

                SpawnConditions special = SPECIAL_CASE.computeIfAbsent(
                        type, k -> SPECIAL_CASE.put(k, new SpawnConditions())
                );

                if (special == null)
                    continue;

                if (overWorld != -1)
                    special.normal = overWorld;
                if (nether != -1)
                    special.nether = nether;
                if (end != -1)
                    special.end = end;
            }
        }
    }

    /**
     * Reloads mob config from the config file.
     */
    public static void reload() {
        EternalLight plugin = EternalLight.getPlugin(EternalLight.class);
        FileConfiguration config = plugin.getConfig();

        // Set legacy cache.
        isLegacy = plugin.isLegacyServer();

        // Load light level settings
        if (config.contains("lightlevels")) {
            int overworld = config.getInt("lightlevel.overworld", 0);
            int nether = config.getInt("lightlevel.nether", 11);
            int end = config.getInt("lightlevel.end", 11);

            if (plugin.isLegacyServer()) {
                overworld = config.getInt("lightlevel.legacy.overworld", 7);
                nether = config.getInt("lightlevel.legacy.nether", 11);
                end = config.getInt("lightlevel.legacy.end", 11);
            }

            DEFAULT = new SpawnConditions(overworld, nether, end);
            DEFAULT.isDefault = true;
        }

        // Load all mob data
        if (config.contains("mobs")) {
            ConfigurationSection section = config.getConfigurationSection("mobs");
            if (section == null)
                return;

            loadMobsFromList(section.getList("overworld"), MOBS_OVER_WORLD);
            loadMobsFromList(section.getList("nether"), MOBS_NETHER);
            loadMobsFromList(section.getList("end"), MOBS_END);
        }
    }

    public static LightSpawnCase getSpawnCase(Block block) {
        World.Environment env = block.getWorld().getEnvironment();
        int light = block.getLightLevel();

        switch (env) {
            case NETHER -> {
                return light > DEFAULT.nether ? LightSpawnCase.NEVER : LightSpawnCase.ALWAYS;
            }
            case THE_END -> {
                return light > DEFAULT.end ? LightSpawnCase.NEVER : LightSpawnCase.ALWAYS;
            }
            default -> {
                if (block.getLightFromBlocks() > DEFAULT.normal)
                    return LightSpawnCase.NEVER;
                if (block.getLightFromSky() > DEFAULT.normal)
                    return LightSpawnCase.NIGHT_SPAWN;
                return LightSpawnCase.ALWAYS;
            }
        }
    }

    /**
     * Returns if that entity type can spawn at that location.
     *
     * @param type entity type.
     * @param loc location to check.
     * @return true if the light and environment is correct for the
     *         entity to spawn.
     */
    public static boolean canSpawnAt(EntityType type, Location loc) {
        if (type == null || loc == null)
            return false;

        boolean correctEnv;
        int lightRequired;
        int light = loc.getBlock().getLightLevel();
        SpawnConditions special = SPECIAL_CASE.get(type);

        switch (Objects.requireNonNull(loc.getWorld()).getEnvironment()) {
            case NETHER -> {
                correctEnv = MOBS_NETHER.contains(type);
                lightRequired = special != null ? special.nether : DEFAULT.nether;
            }
            case THE_END -> {
                correctEnv = MOBS_END.contains(type);
                lightRequired = special != null ? special.nether : DEFAULT.nether;
            }
            default -> {
                correctEnv = MOBS_OVER_WORLD.contains(type);
                lightRequired = special != null ? special.nether : DEFAULT.nether;
            }
        }

        return correctEnv && light <= lightRequired;
    }

    public static boolean canSpawnIn(EntityType type, World.Environment env) {
        switch (env) {
            case NETHER -> {
                return MOBS_NETHER.contains(type);
            }
            case THE_END -> {
                return MOBS_END.contains(type);
            }
            default -> {
                return MOBS_OVER_WORLD.contains(type);
            }
        }
    }

    public int getNormal() {
        if (normal == -1 && !isDefault)
            return DEFAULT.normal;
        return normal;
    }

    public int getNether() {
        if (nether == -1 && !isDefault)
            return DEFAULT.nether;
        return nether;
    }

    public int getEnd() {
        if (end == -1 && !isDefault)
            return DEFAULT.end;
        return end;
    }
}
