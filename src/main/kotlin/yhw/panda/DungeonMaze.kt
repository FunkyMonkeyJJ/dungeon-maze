package yhw.panda

import org.bukkit.Bukkit
import org.bukkit.ChatColor.AQUA
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import java.io.File
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import kotlin.math.pow

object DungeonMaze : JavaPlugin() {
    val plugin: Plugin = this
    val dungeonPlayers: LinkedList<DungeonPlayer> = LinkedList()

    lateinit var treasureYml: YamlConfiguration
    lateinit var dungeonListener: DungeonListener
    lateinit var scheduler: BukkitScheduler

    private lateinit var hardcoreRewardBonus: String
    lateinit var hardcoreSwitchLocation: Location
    lateinit var upperMazeCorner: Location
    lateinit var lowerMazeCorner: Location

    override fun onEnable() {
        createConfig()
        treasureYml = createTreasure()
        try {
            upperMazeCorner = config.getLocation("maze.upper-limit")!!
            lowerMazeCorner = config.getLocation("maze.lower-limit")!!
            hardcoreSwitchLocation = config.getLocation(
                "maze.hardcore-switch"
            )!!
            hardcoreRewardBonus = config.getString(
                "maze.hardcore-protocol.reward-bonus"
            )!!
        } catch (ignored: Exception) {
            Bukkit.getLogger().info(
                "A problem occured with the config location values."
            )
            return
        }
        scheduler = server.scheduler
        dungeonListener = DungeonListener()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun onCommand(
        sender: CommandSender, command: Command,
        label: String, args: Array<out String>
    ): Boolean {
        if (sender !is Player) return false
        if (label == "chestrefresh" && sender.isOp) {
            for (dungeonPlayer: DungeonPlayer in dungeonPlayers)
                if (dungeonPlayer.compareTo(sender) == 0) {
                    dungeonPlayers.remove(dungeonPlayer)
                    dungeonPlayers.add(DungeonPlayer(sender))
                    break
                }
            sender.sendMessage(
                "${AQUA}Your chests have been refreshed for the dungeon."
            )
        }
        return true
    }

    /**
     * Creates the config.yml file if it doesn't exist.
     */
    private fun createConfig() {
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            saveResource("config.yml", false)
        }
        val config = YamlConfiguration()
        try {
            config.load(configFile)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    /**
     * Creates the treasure.yml file if it doesn't exist.
     *
     * @return the [YamlConfiguration] that is either created
     * or already existed.
     */
    private fun createTreasure(): YamlConfiguration {
        val treasureFile = File(dataFolder, "treasure.yml")
        if (!treasureFile.exists()) {
            treasureFile.parentFile.mkdirs()
            saveResource("treasure.yml", false)
        }
        return try {
            YamlConfiguration.loadConfiguration(treasureFile)
        } catch (e: Exception) {
            e.printStackTrace()
            YamlConfiguration()
        }
    }

    /**
     * Finds the function that the hardcore reward bonus should follow
     * and applies it to the given [stack].
     *
     * @return the value that is calculated.
     */
    fun hardcoreRewardBonus(stack: Int): Int {
        val function = Regex("[+\\-*/^]").find(hardcoreRewardBonus)
            ?: return stack
        val value = Regex("[\\d]+").find(hardcoreRewardBonus)
            ?: return stack
        val mathValue = value.value.toInt()

        return when (function.value) {
            "+" -> stack + mathValue
            "-" -> stack - mathValue
            "*" -> stack * mathValue
            "/" ->
                if (mathValue == 0) return stack
                else stack / mathValue
            "^" -> stack.toDouble().pow(mathValue).toInt()
            else -> stack
        }
    }

    /**
     * Finds the DungeonPlayer that correlates to the given [player].
     * If the DungeonPlayer doesn't exist, it creates one and adds
     * it to dungeonPlayers.
     */
    fun getDungeonPlayer(player: Player): DungeonPlayer {
        for (dungeonPlayer: DungeonPlayer in dungeonPlayers)
            if (dungeonPlayer.compareTo(player) == 0) return dungeonPlayer
        val dungeonPlayer = DungeonPlayer(player)
        dungeonPlayers.add(dungeonPlayer)
        return dungeonPlayer
    }
}
