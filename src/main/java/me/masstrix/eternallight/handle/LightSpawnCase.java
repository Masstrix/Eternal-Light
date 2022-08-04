package me.masstrix.eternallight.handle;

import java.awt.*;

public enum LightSpawnCase {
    NIGHT_SPAWN(Color.YELLOW), NEVER(Color.GREEN), ALWAYS(Color.RED);

    final Color color;

    LightSpawnCase(Color color) {
        this.color = color;
    }
}
