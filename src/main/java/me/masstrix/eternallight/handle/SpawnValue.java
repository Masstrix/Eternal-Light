package me.masstrix.eternallight.handle;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Scaffolding;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;

import java.util.HashSet;
import java.util.Set;

public enum SpawnValue {
  /**
   * Block is ignored in spawning rules. For example Grass, Flowers.
   */
  TRANSPARENT,

  /**
   * Block cannot have mobs spawn on it.
   */
  NEVER,

  /**
   * Block is can have mobs spawn on it.
   */
  ALWAYS;

  private static Set<BlockKey> blocks = new HashSet<>();
  private static Set<BlockKey> nonSolids = new HashSet<>();

  static {
    blocks.add(new BlockKeyMat(Material.GLOWSTONE, NEVER));
    blocks.add(new BlockKeyMat(Material.LADDER, NEVER));
    blocks.add(new BlockKeyMat(Material.COBWEB, NEVER));
    blocks.add(new BlockKeyMat(Material.CAKE, NEVER));
    blocks.add(new BlockKeyMat(Material.END_PORTAL_FRAME, NEVER));
    blocks.add(new BlockKeyMat(Material.END_CRYSTAL, NEVER));
    blocks.add(new BlockKeyMat(Material.END_PORTAL, NEVER));
    blocks.add(new BlockKeyMat(Material.LANTERN, NEVER));

    blocks.add(new BlockKeyMat(Material.BELL, NEVER));
    blocks.add(new BlockKeyMat(Material.GRINDSTONE, NEVER));
    blocks.add(new BlockKeyMat(Material.ANVIL, NEVER));
    blocks.add(new BlockKeyMat(Material.COMPOSTER, NEVER));
    blocks.add(new BlockKeyMat(Material.BREWING_STAND, NEVER));
    blocks.add(new BlockKeyMat(Material.IRON_BARS, NEVER));
    blocks.add(new BlockKeyMat(Material.STONECUTTER, NEVER));
    blocks.add(new BlockKeyMat(Material.CAMPFIRE, NEVER));
    blocks.add(new BlockKeyMat(Material.LECTERN, NEVER));
    blocks.add(new BlockKeyMat(Material.CAULDRON, NEVER));
    blocks.add(new BlockKeyMat(Material.HOPPER, NEVER));
    blocks.add(new BlockKeyMat(Material.CONDUIT, NEVER));
    blocks.add(new BlockKeyMat(Material.BAMBOO, NEVER));
    blocks.add(new BlockKeyMat(Material.TURTLE_EGG, NEVER));
    blocks.add(new BlockKeyMat(Material.ENCHANTING_TABLE, NEVER));
    blocks.add(new BlockKeyMat(Material.BEDROCK, NEVER));
    blocks.add(new BlockKeyMat(Material.FARMLAND, NEVER));
    blocks.add(new BlockKeyMat(Material.BARRIER, NEVER));
    blocks.add(new BlockKeyMat(Material.GRASS_PATH, NEVER));
    blocks.add(new BlockKeyMat(Material.DRAGON_EGG, NEVER));

    // Redstone
    blocks.add(new BlockKeyMat(Material.TRIPWIRE, TRANSPARENT));
    blocks.add(new BlockKeyMat(Material.TRIPWIRE_HOOK, TRANSPARENT));
    blocks.add(new BlockKeyMat(Material.DAYLIGHT_DETECTOR, TRANSPARENT));

    // Greens
    blocks.add(new BlockKeyMat(Material.MYCELIUM, NEVER));
    blocks.add(new BlockKeyMat(Material.FLOWER_POT, NEVER));
    blocks.add(new BlockKeyMat(Material.VINE, TRANSPARENT));
    blocks.add(new BlockKeyMat(Material.RED_MUSHROOM, TRANSPARENT));
    blocks.add(new BlockKeyMat(Material.BROWN_MUSHROOM, TRANSPARENT));
    blocks.add(new BlockKeyMat(Material.GRASS, TRANSPARENT));
    blocks.add(new BlockKeyMat(Material.TALL_GRASS, TRANSPARENT));
    blocks.add(new BlockKeyMat(Material.FERN, TRANSPARENT));
    blocks.add(new BlockKeyMat(Material.LARGE_FERN, TRANSPARENT));
    blocks.add(new BlockKeyMat(Material.CACTUS, NEVER));
    blocks.add(new BlockKeyMat(Material.COCOA, NEVER));
    blocks.add(new BlockKeyMat(Material.SEA_PICKLE, TRANSPARENT));

    blocks.add(new BlockKeyMatch("(.*)chest", NEVER));
    blocks.add(new BlockKeyMatch("(.*)fence", NEVER));
    blocks.add(new BlockKeyMatch("(.*)bed", NEVER));
    blocks.add(new BlockKeyMatch("(.*)leaves", NEVER));
    blocks.add(new BlockKeyMatch("(.*)pressure_plate", NEVER));
    blocks.add(new BlockKeyMatch("(.*)door", TRANSPARENT));
    blocks.add(new BlockKeyMatch("(.*)sign", TRANSPARENT));
    blocks.add(new BlockKeyMatch("(.*)coral", NEVER));
    blocks.add(new BlockKeyMatch("(.*)coral_fan", TRANSPARENT));
    blocks.add(new BlockKeyMatch("(.*)fence_gate", TRANSPARENT));
    blocks.add(new BlockKeyMatch("(.*)banner", TRANSPARENT));
    blocks.add(new BlockKeyMatch("(.*)glass(.*)", NEVER));
    blocks.add(new BlockKeyMatch("(.*)wall(.*)", NEVER));

    nonSolids.add(new BlockKeyMatch("(.*)carpet", NEVER));
    nonSolids.add(new BlockKeyMatch("potted(.*)", NEVER));
    nonSolids.add(new BlockKeyMat(Material.FLOWER_POT, NEVER));
    nonSolids.add(new BlockKeyMat(Material.SNOW, ALWAYS));
  }

  /**
   * @param block block type to check for.
   * @return the specified value for the block.
   */
  public static SpawnValue get(Block block) {
    BlockData data = block.getBlockData();

    if (data instanceof TrapDoor) {
      TrapDoor trapDoor = (TrapDoor) data;
      if (trapDoor.isOpen()) {
        return SpawnValue.TRANSPARENT;
      } else {
        if (trapDoor.getHalf() == Bisected.Half.BOTTOM) {
          return SpawnValue.NEVER;
        } else {
          return SpawnValue.ALWAYS;
        }
      }
    } else if (data instanceof Stairs) {
      Stairs stair = (Stairs) data;
      if (stair.getHalf() == Bisected.Half.TOP) return ALWAYS;
      else return NEVER;
    } else if (data instanceof Slab) {
      Slab slab = (Slab) data;
      if (slab.getType().equals(Slab.Type.TOP) || slab.getType().equals(Slab.Type.DOUBLE)) return ALWAYS;
      else return NEVER;
    } else if (data instanceof Scaffolding) {
      return ALWAYS;
    }


    if (!block.getType().isSolid() || block.getType().name().contains("AIR")) {
      for (BlockKey key : nonSolids) {
        if (key.equals(block)) return key.getValue();
      }
      return TRANSPARENT;
    }
    for (BlockKey key : blocks) {
      if (key.equals(block)) return key.getValue();
    }
    return ALWAYS;
  }
}
