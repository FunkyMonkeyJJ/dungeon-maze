package yhw.panda.dungeonmaze;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.bukkit.Material.GLOWSTONE;
import static org.bukkit.Material.WATER;

public class DungeonPlayer implements Comparable<Player> {
    public Player player;
    public boolean hardcoreEnabled;
    private int healingPoolTaskId = 0;

    public ArrayList<LocalDate> commonTreasureLastOpened = new ArrayList<>();
    public ArrayList<LocalDate> uncommonTreasureLastOpened =
            new ArrayList<>();
    public ArrayList<LocalDate> rareTreasureLastOpened = new ArrayList<>();

    DungeonPlayer(Player player) {
        this.player = player;
        for (int i = 0; i < 4; i++)
            commonTreasureLastOpened.add(LocalDate.now().minusDays(1));
        for (int i = 0; i < 4; i++)
            uncommonTreasureLastOpened.add(LocalDate.now().minusDays(1));
        for (int i = 0; i < 1; i++)
            rareTreasureLastOpened.add(LocalDate.now().minusDays(1));
        healingPoolTaskId = DungeonMaze.scheduler.scheduleSyncRepeatingTask(
                DungeonMaze.plugin,
                () -> {
                    for (DungeonPlayer dungeonPlayer :
                            DungeonMaze.dungeonPlayers) {
                        if (dungeonPlayer.compareTo(player) != 0) continue;
                        if (!player.isOnline()) DungeonMaze.scheduler.
                                cancelTask(healingPoolTaskId);
                    }
                    Location location = player.getLocation();
                    Block blockBelow = location.clone().
                            add(0, -1, 0).getBlock();
                    if (DungeonMaze.dungeonListener.
                            blockNotInMaze(blockBelow)) return;
                    if (hardcoreEnabled) return;
                    if (location.getBlock().getType() != WATER) return;
                    if (blockBelow.getType() != GLOWSTONE) return;
                    if (DungeonMaze.dungeonListener.
                            blockNotInMaze(blockBelow)) return;
                    for (PotionEffect effect :
                            player.getActivePotionEffects()) {
                        if (effect.getType() !=
                                PotionEffectType.REGENERATION) continue;
                        if (effect.getAmplifier() > 0) return;
                    }
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.REGENERATION,
                            60, 0));
                }, 300L, 60L);
    }

    @Override
    public int compareTo(Player o) {
        return (player.getUniqueId().compareTo(o.getUniqueId()));
    }
}
