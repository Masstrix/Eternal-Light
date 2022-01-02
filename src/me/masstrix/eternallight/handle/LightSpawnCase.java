package me.masstrix.eternallight.handle;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.awt.*;

public enum LightSpawnCase {
    NIGHT_SPAWN(Color.YELLOW), NEVER(Color.GREEN), ALWAYS(Color.RED);

    Color color;

    LightSpawnCase(Color color) {
        this.color = color;
    }

    public static LightSpawnCase getCase(Block block) {
        World.Environment environment = block.getWorld().getEnvironment();
        int base = environment == World.Environment.NETHER ? 11 : 0;

        if (block.getLightFromBlocks() > base) return NEVER;
        if (block.getLightFromSky() > base) return NIGHT_SPAWN;
        else return ALWAYS;
    }
}
