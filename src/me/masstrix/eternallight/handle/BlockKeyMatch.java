package me.masstrix.eternallight.handle;

import org.bukkit.block.Block;

import java.util.regex.Pattern;

public class BlockKeyMatch implements BlockKey {

    private Pattern pattern;
    private SpawnValue value;

    public BlockKeyMatch(String pattern, SpawnValue value) {
        this.pattern = Pattern.compile(pattern);
        this.value = value;
    }

    @Override
    public boolean equals(Block block) {
        return block != null && pattern.matcher(block.getType().name().toLowerCase()).matches();
    }

    @Override
    public SpawnValue getValue() {
        return value;
    }
}
