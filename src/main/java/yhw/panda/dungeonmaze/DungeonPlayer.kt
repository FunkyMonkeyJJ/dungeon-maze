package yhw.panda

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType.*
import java.time.LocalDate

class DungeonPlayer(player: Player) : Comparable<Player> {
    var player: Player
    var hardcoreEnabled = false
    private var healingPoolTaskId = 0

    var commonTreasureLastOpened = ArrayList<LocalDate>()
    var uncommonTreasureLastOpened = ArrayList<LocalDate>()
    var rareTreasureLastOpened = ArrayList<LocalDate>()

    init {
        this.player = player
        val yesterday = LocalDate.now().minusDays(1)
        for (i in 0..3) commonTreasureLastOpened.add(yesterday)
        for (i in 0..3) uncommonTreasureLastOpened.add(yesterday)
        for (i in 0..0) rareTreasureLastOpened.add(yesterday)

        healingPoolTaskId = DungeonMaze.scheduler.scheduleSyncRepeatingTask(
            DungeonMaze.plugin, {
                for (dungeonPlayer in DungeonMaze.dungeonPlayers) {
                    if (dungeonPlayer.compareTo(player) != 0) continue
                    if (!player.isOnline)
                        DungeonMaze.scheduler.cancelTask(healingPoolTaskId)
                }

                val location = player.location.clone()
                val blockBelow = location.add(0.0, -1.0, 0.0).block
                if (DungeonMaze.dungeonListener.blockNotInMaze(blockBelow))
                    return@scheduleSyncRepeatingTask
                if (hardcoreEnabled) return@scheduleSyncRepeatingTask
                if (location.block.type != Material.WATER)
                    return@scheduleSyncRepeatingTask
                if (blockBelow.type != Material.GLOWSTONE)
                    return@scheduleSyncRepeatingTask
                if (DungeonMaze.dungeonListener.blockNotInMaze(blockBelow))
                    return@scheduleSyncRepeatingTask

                for (effect in player.activePotionEffects) {
                    if (effect.type !== REGENERATION) continue
                    if (effect.amplifier > 0) return@scheduleSyncRepeatingTask
                }
                player.addPotionEffect(
                    PotionEffect(REGENERATION, 60, 0)
                )
            }, 300L, 60L
        )
    }

    /**
     * Simply compares each others UUID's
     */
    override operator fun compareTo(other: Player): Int =
        player.uniqueId.compareTo(other.uniqueId)
}
