package me.masstrix.eternallight.handle;

import me.masstrix.eternallight.EternalLight;
import me.masstrix.eternallight.util.EnumUtil;
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
    private static final Set<String> ALL = new HashSet<>();

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
            ALL.add(type.name().toLowerCase());
            set.add(type);

            System.out.println("Loading in " + type.name());

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

            // Reset
            ALL.clear();
            MOBS_END.clear();
            MOBS_NETHER.clear();
            MOBS_OVER_WORLD.clear();

            // Load
            loadMobsFromList(section.getList("overworld"), MOBS_OVER_WORLD);
            System.out.println(Arrays.toString(MOBS_OVER_WORLD.toArray()));
            loadMobsFromList(section.getList("nether"), MOBS_NETHER);
            System.out.println(Arrays.toString(MOBS_NETHER.toArray()));
            loadMobsFromList(section.getList("end"), MOBS_END);
            System.out.println(Arrays.toString(MOBS_END.toArray()));
        }
    }

    public static List<String> tabEntityTypes() {
        return ALL.stream().toList();
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
     * @param block block to check.
     * @return true if the light and environment is correct for the
     *         entity to spawn.
     */
    public static LightSpawnCase canSpawnAt(EntityType type, Block block) {
        if (type == null || block == null)
            return LightSpawnCase.NEVER;

        boolean correctEnv;
        int lightRequired;
        int light = block.getLightLevel();
        SpawnConditions special = SPECIAL_CASE.get(type);

        switch (block.getWorld().getEnvironment()) {
            case NETHER -> {
                correctEnv = MOBS_NETHER.contains(type);
                lightRequired = special != null ? special.nether : DEFAULT.nether;
            }
            case THE_END -> {
                correctEnv = MOBS_END.contains(type);
                lightRequired = special != null ? special.end : DEFAULT.end;
            }
            default -> {
                // Special case for default to use world night cycles.
                correctEnv = MOBS_OVER_WORLD.contains(type);
                lightRequired = special != null ? special.normal : DEFAULT.normal;

                if (!correctEnv)
                    return LightSpawnCase.NEVER;

                // Handle switching of day night cycle.
                if (block.getLightFromBlocks() > lightRequired)
                    return LightSpawnCase.NEVER;
                if (block.getLightFromSky() > lightRequired)
                    return LightSpawnCase.NIGHT_SPAWN;
                return LightSpawnCase.ALWAYS;
            }
        }

        return correctEnv && light <= lightRequired ? LightSpawnCase.ALWAYS : LightSpawnCase.NEVER;
    }

    /**
     * Returns if en entity can spawn in the given world environment.
     *
     * @param type entity type.
     * @param env environment type.
     * @return true if the entity can spawn in that environment.
     */
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
