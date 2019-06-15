package me.masstrix.eternallight.handle;

import me.masstrix.eternallight.EternalLight;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Projector {

  protected EternalLight plugin;
  private Map<UUID, LightVisual> visualMap = new HashMap<>();

  public Projector(EternalLight plugin) {
    this.plugin = plugin;
    new BukkitRunnable() {
      @Override
      public void run() {
        for (LightVisual visual : visualMap.values()) {
          visual.update();
        }
      }
    }.runTaskTimer(plugin, 20, 5);
  }

  /**
   * @param player who is being added to the projector.
   */
  public void add(Player player) {
    if (player == null || this.contains(player.getUniqueId())) return;
    LightVisual visual = new LightVisual(this, player.getUniqueId());
    visualMap.put(player.getUniqueId(), visual);
  }

  /**
   * @param player who is being removed from the projector.
   */
  public void remove(Player player) {
    if (player == null) return;
    this.remove(player.getUniqueId());
  }

  /**
   * @param uuid who is being removed from the projector.
   */
  void remove(UUID uuid) {
    if (visualMap.containsKey(uuid)) {
      visualMap.get(uuid).hide();
      visualMap.remove(uuid);
    }
  }

  /**
   * @param player who's visual to return.
   * @return the players visual or null if none exists.
   */
  public LightVisual getVisual(Player player) {
    return player == null ? null : getVisual(player.getUniqueId());
  }

  /**
   * @param uuid who's visual to return.
   * @return the players visual or null if none exists.
   */
  private LightVisual getVisual(UUID uuid) {
    return visualMap.getOrDefault(uuid, null);
  }

  /**
   * @param player who to check.
   * @return if the player currently has a visual.
   */
  public boolean canSee(Player player) {
    return player != null && this.contains(player.getUniqueId());
  }

  /**
   * @param uuid who to check if the projector contains.
   * @return if the projector contains {@code uuid, player}.
   */
  public boolean contains(UUID uuid) {
    return visualMap.containsKey(uuid);
  }
}
