package me.masstrix.eternallight.handle;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockKeyMat implements BlockKey {

    private Material mat;
    private SpawnValue value;

    public BlockKeyMat(Material mat, SpawnValue value) {
        this.mat = mat;
        this.value = value;
    }

    @Override
    public boolean equals(Block block) {
        return block != null && block.getType() == mat;
    }

    @Override
    public SpawnValue getValue() {
        return value;
    }
}
