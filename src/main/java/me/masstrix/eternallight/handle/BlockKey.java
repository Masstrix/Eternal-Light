package me.masstrix.eternallight.handle;

import org.bukkit.block.Block;

public interface BlockKey {

  boolean equals(Block block);

  SpawnValue getValue();
}
