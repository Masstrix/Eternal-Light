package me.masstrix.eternallight.handle;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.regex.Pattern;

public class MaterialMatcher {

    private Pattern pattern;
    private SpawnValue value;

    public MaterialMatcher(String pattern, SpawnValue value) {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        this.value = value;
    }

    public String getKey() {
        return pattern.toString();
    }

    public boolean equals(Material mat) {
        return pattern.matcher(mat.name()).matches();
    }

    public SpawnValue getValue() {
        return value;
    }
}
