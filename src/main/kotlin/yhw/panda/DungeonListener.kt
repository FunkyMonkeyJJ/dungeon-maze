package yhw.panda

import org.bukkit.*
import org.bukkit.ChatColor.*
import org.bukkit.Material.*
import org.bukkit.block.Block
import org.bukkit.block.Dispenser
import org.bukkit.entity.Creeper
import org.bukkit.entity.EntityType
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action.PHYSICAL
import org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType.*
import yhw.panda.DungeonMaze.getDungeonPlayer
import yhw.panda.DungeonMaze.plugin
import yhw.panda.DungeonTreasure.*
import java.time.LocalDate

class DungeonListener : Listener {
    init {
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    /**
     * Checks if [DungeonMaze.dungeonPlayers] has a DungeonPlayer
     * that correlates to the player that is joining. If there
     * isn't one, it makes a new DungeonPlayer and adds it to
     * dungeonPlayers.
     */
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        for (dungeonPlayer: DungeonPlayer in DungeonMaze.dungeonPlayers)
            if (dungeonPlayer.compareTo(player) == 0) return
        DungeonMaze.dungeonPlayers.add(DungeonPlayer(player))
    }

    /**
     * Removes the DungeonPlayer that correlates to the player that
     * is leaving.
     */
    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        DungeonMaze.dungeonPlayers.removeIf { dungeonPlayer ->
            dungeonPlayer.compareTo(event.player) == 0
        }
    }

    /**
     * Handles the player opening treasure chests.
     */
    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        if (event.action != RIGHT_CLICK_BLOCK) return
        if (event.clickedBlock == null) return
        val block = event.clickedBlock ?: return
        if (blockNotInMaze(block)) return
        when (block.type) {
            CHEST, TRAPPED_CHEST, BARREL -> {
                event.isCancelled = true
                openTreasure(getDungeonPlayer(event.player), block.location)
            }
            else -> return
        }
    }

    /**
     * Checks if the given [block] is not in the maze coorinates.
     */
    fun blockNotInMaze(block: Block): Boolean {
        val worldName = plugin.config.getString("maze.world")
            ?: return true
        plugin.server.getWorld(worldName) ?: return true
        return try {
            !locationInArea(
                block.location,
                DungeonMaze.lowerMazeCorner, DungeonMaze.upperMazeCorner
            )
        } catch (ignored: Exception) {
            true
        }
    }

    /**
     * Checks if the given [location] is between [lower] and [upper].
     */
    private fun locationInArea(
        location: Location, lower: Location, upper: Location
    ): Boolean {
        return ((location.x >= lower.x && location.x <= upper.x) ||
                (location.x <= lower.x && location.x >= upper.x)) &&
                (location.y >= lower.y && location.y <= upper.y) &&
                ((location.z >= lower.z && location.z <= upper.z) ||
                        (location.z <= lower.z && location.z >= upper.z))
    }

    /**
     * Checks whether the given [location] is a valid treasure location
     * and checks if the given [dungeonPlayer] has opened said treasure
     * within the past day. If these are both true, a GUI with all the
     * treasure will be opened for the player.
     */
    private fun openTreasure(
        dungeonPlayer: DungeonPlayer, location: Location
    ) {
        for (dungeonTreasure: DungeonTreasure in DungeonTreasure.values())
            for (i in 1..4) {
                val treasureX = plugin.config.getDouble(
                    String.format("treasure.%s.%d.x", dungeonTreasure, i)
                )
                val treasureY = plugin.config.getDouble(
                    String.format("treasure.%s.%d.y", dungeonTreasure, i)
                )
                val treasureZ = plugin.config.getDouble(
                    String.format("treasure.%s.%d.z", dungeonTreasure, i)
                )

                // If location is a valid treasure location
                if (!((location.x == treasureX) &&
                            (location.y == treasureY) &&
                            (location.z == treasureZ))
                ) {
                    // Determine when treasure was last opened
                    val player = dungeonPlayer.player
                    val treasureOpened = when (dungeonTreasure) {
                        RARE -> dungeonPlayer.rareTreasureLastOpened
                        UNCOMMON -> dungeonPlayer.uncommonTreasureLastOpened
                        else -> dungeonPlayer.commonTreasureLastOpened
                    }

                    if (treasureOpened[i - 1] == LocalDate.now()) {
                        player.sendMessage(
                            "${RED}You have already looted this booty today!"
                        )
                        return
                    }

                    // Player opens treasure GUI
                    treasureOpened[i - 1] = LocalDate.now()
                    player.openInventory(
                        DungeonTreasure.generate(player, dungeonTreasure)
                    )

                    val tierChatColor = when (dungeonTreasure) {
                        RARE -> DARK_PURPLE
                        UNCOMMON -> BLUE
                        COMMON -> GREEN
                    }
                    player.sendMessage(
                        "${tierChatColor}You have opened a " +
                                "$dungeonTreasure chest."
                    )
                    return
                }
            }
    }

    /**
     * Stops the player from breaking blocks unless they are from the
     * sand traps.
     */
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        val player = event.player
        if (blockNotInMaze(block)) return

        if (!block.hasMetadata("trap")) {
            if (player.isOp) return
            event.isCancelled = true
            player.sendMessage(
                "${RED}You cannot break blocks in the maze except " +
                        "traps that block the way."
            )
            return
        }
        block.drops.clear()

        when (block.type) {
            COBWEB, TRIPWIRE, STONE_PRESSURE_PLATE -> {
                val originalBlockType = block.type
                DungeonMaze.scheduler.scheduleSyncDelayedTask(
                    plugin, {
                        if (block.type == AIR) block.type = originalBlockType
                    }, 600L
                )
            }
            else -> return
        }
    }

    /**
     * Stops the player from placing anything in the maze apart from
     * blocks that help light the way.
     */
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val block = event.block
        val player = event.player
        if (blockNotInMaze(block)) return

        var i = 1
        val dungeonPlayer = getDungeonPlayer(player)
        if (dungeonPlayer.hardcoreEnabled) i = 2

        when (block.type) {
            TORCH, REDSTONE_TORCH, WALL_TORCH,
            REDSTONE_WALL_TORCH, SOUL_TORCH,
            SOUL_WALL_TORCH ->
                DungeonMaze.scheduler.scheduleSyncDelayedTask(
                    plugin, { block.type = AIR }, 600L * i
                )
            LANTERN, SOUL_LANTERN, JACK_O_LANTERN,
            CAMPFIRE, SOUL_CAMPFIRE ->
                DungeonMaze.scheduler.scheduleSyncDelayedTask(
                    plugin, { block.type = AIR }, 1200L * i
                )
            else -> {
                if (player.isOp) return
                event.isCancelled = true
                player.sendMessage(
                    "${RED}You cannot place blocks in the maze except " +
                            "ones that will light the way."
                )
            }
        }
    }

    /**
     * Handles the traps and the way to exit the maze.
     *
     * TODO: Need to update this to not only include certain blocks
     *  when the config.yml file is updated appropriately.
     */
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val action = event.action
        if (!(action == PHYSICAL || action == RIGHT_CLICK_BLOCK)) return
        val block = event.clickedBlock ?: return
        if (blockNotInMaze(block)) return
        val blockLocation = block.location
        val world = blockLocation.world ?: return
        val player = event.player

        when (block.type) {
            OAK_BUTTON -> {
                if (blockNearby(blockLocation, 5, BEACON)) {
                    player.sendMessage(
                        "${AQUA}You find yourself very tired " +
                                "from your long journey..."
                    )

                    var count = 10
                    DungeonMaze.scheduler.scheduleSyncRepeatingTask(
                        plugin, {
                            count -= 1
                            if (count > 0) {
                                world.spawnParticle(
                                    Particle.CLOUD,
                                    blockLocation, 100
                                )
                            }
                        }, 10, 10
                    )

                    DungeonMaze.scheduler.scheduleSyncDelayedTask(
                        plugin, {
                            if (player.bedSpawnLocation != null)
                                player.teleport(player.bedSpawnLocation!!)
                            else player.teleport(world.spawnLocation)
                        }, 100
                    )
                    return
                }
                val dungeonPlayer = getDungeonPlayer(player)
                dungeonPlayer.hardcoreEnabled = !dungeonPlayer.hardcoreEnabled
                if (dungeonPlayer.hardcoreEnabled) player.sendMessage(
                    "${RED}The Hardcore Maze Protocol has been enabled."
                ) else player.sendMessage(
                    "${GREEN}The Hardcore Maze Protocol has been disabled."
                )
            }
            TRIPWIRE -> if (blockNearby(blockLocation, 4, OBSIDIAN)) {
                val explosiveChargeBlock = getBlockNearby(
                    blockLocation, 10, BLAST_FURNACE
                ) ?: return
                DungeonMaze.scheduler.scheduleSyncDelayedTask(
                    plugin, {
                        world.createExplosion(
                            explosiveChargeBlock.location,
                            2f, false, false
                        )
                    }, 15L
                )
            }
            STONE_PRESSURE_PLATE -> {
                // Sand Fall Trap
                if (blockNearby(blockLocation, 2, SANDSTONE)) {
                    val x = blockLocation.x.toInt()
                    val y = blockLocation.y.toInt()
                    val z = blockLocation.z.toInt()

                    // Spawns Sand, Gravel, etc. around the player
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(
                        plugin, {
                            world.spawnParticle(
                                Particle.FALLING_DUST,
                                player.location, 100
                            )
                            var potentialAir: Block
                            var i = -4
                            while (i < 5) {
                                var j = 0
                                while (j < 5) {
                                    var k = -4
                                    while (k < 5) {
                                        if (Math.random() > 0.8) {
                                            k++
                                            continue
                                        }

                                        potentialAir = world.getBlockAt(
                                            x + i, y + j, z + k
                                        )
                                        if (potentialAir.type != AIR) {
                                            k++
                                            continue
                                        }

                                        when ((Math.random() * 3).toInt()) {
                                            0 -> potentialAir.type = SAND
                                            1 -> potentialAir.type = RED_SAND
                                            2 -> potentialAir.type = GRAVEL
                                        }
                                        val value = FixedMetadataValue(
                                            plugin, true
                                        )
                                        potentialAir.setMetadata(
                                            "trap", value
                                        )
                                        k++
                                    }
                                    j++
                                }
                                i++
                            }
                        }, 10L
                    )

                    // Removes the Sand, Gravel, etc. after 60 seconds
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(
                        plugin, {
                            var potentialSand: Block
                            var i = -4
                            while (i < 5) {
                                var j = 0
                                while (j < 5) {
                                    var k = -4
                                    while (k < 5) {
                                        potentialSand = world.getBlockAt(
                                            x + i, y + j, z + k
                                        )
                                        if (potentialSand.hasMetadata(
                                                "trap"
                                            )
                                        ) when (potentialSand.type) {
                                            SAND, RED_SAND, GRAVEL,
                                            GRAY_CONCRETE_POWDER ->
                                                potentialSand.type = AIR
                                            else -> Unit
                                        }
                                        potentialSand.removeMetadata(
                                            "trap", plugin
                                        )
                                        k++
                                    }
                                    j++
                                }
                                i++
                            }
                        }, 1200L
                    )
                }

                val dungeonPlayer = getDungeonPlayer(player)
                if (!dungeonPlayer.hardcoreEnabled) return

                // Explosion Trap
                if (blockNearby(blockLocation, 2, OBSIDIAN)) {
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(
                        plugin,
                        {
                            world.createExplosion(
                                blockLocation, 2f,
                                false, false
                            )
                        }, 10L
                    )
                }

                // Potion Trap
                if (blockNearby(blockLocation, 2, BREWING_STAND)) {
                    val splashPotion = world.spawnEntity(
                        blockLocation, EntityType.SPLASH_POTION
                    ) as ThrownPotion
                    val potion = ItemStack(SPLASH_POTION)
                    val potionMeta = potion.itemMeta as PotionMeta
                    when ((Math.random() * 4).toInt()) {
                        0 -> {
                            potionMeta.color = Color.BLACK
                            potionMeta.addCustomEffect(
                                PotionEffect(
                                    BLINDNESS, 400, 1
                                ), true
                            )
                        }
                        1 -> {
                            potionMeta.color = Color.ORANGE
                            potionMeta.addCustomEffect(
                                PotionEffect(
                                    CONFUSION, 400, 1
                                ), true
                            )
                        }
                        2 -> {
                            potionMeta.color = Color.GREEN
                            potionMeta.addCustomEffect(
                                PotionEffect(POISON, 300, 3),
                                true
                            )
                        }
                        3 -> {
                            potionMeta.color = Color.BLACK
                            potionMeta.addCustomEffect(
                                PotionEffect(WITHER, 300, 2),
                                true
                            )
                        }
                    }
                    potion.itemMeta = potionMeta
                    splashPotion.item = potion
                }

                // Arrow Trap
                if (blockNearby(blockLocation, 2, FLETCHING_TABLE)) {
                    val dispenserBlock = getBlockNearby(
                        blockLocation, 10, DISPENSER
                    ) ?: return
                    if (dispenserBlock.type != DISPENSER) return

                    val dispenser = dispenserBlock.state as Dispenser
                    dispenser.inventory.addItem(ItemStack(ARROW, 6))

                    // Fires every 1/4 second
                    dispenser.dispense()
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(
                        plugin, { dispenser.dispense() }, 5L
                    )
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(
                        plugin, { dispenser.dispense() }, 10L
                    )
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(
                        plugin, { dispenser.dispense() }, 15L
                    )
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(
                        plugin, { dispenser.dispense() }, 20L
                    )
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(
                        plugin, { dispenser.dispense() }, 25L
                    )
                }
            }
            else -> return
        }
    }

    /**
     * Searches the cubic [radius] from [location] for the
     * given [material] and returns whether it found it.
     *
     * @return whether the given [material] was nearby.
     */
    private fun blockNearby(
        location: Location, radius: Int, material: Material
    ): Boolean = getBlockNearby(location, radius, material) != null

    /**
     * Searches the cubic [radius] from [location] for the
     * given [material] and returns the block that it finds.
     *
     * @return the block that it finds; null if nothing is found.
     */
    private fun getBlockNearby(
        location: Location, radius: Int, material: Material
    ): Block? {
        val world = location.world ?: return null
        for (i in -radius until radius)
            for (j in -radius until radius)
                for (k in -radius until radius) {
                    val newLoc = location.clone().add(
                        i.toDouble(), j.toDouble(), k.toDouble()
                    )
                    val checkBlock = world.getBlockAt(newLoc)
                    if (checkBlock.type == material) return checkBlock
                }
        return null
    }

    /**
     * Prevents the player from using commands in the maze. Players
     * who have enabled hardcore protocol cannot use any commands.
     * Players who are playing normally cannot use commands that are
     * listed in the config.yml.
     *
     * TODO: Update this when blacklisted commands are added to the config.yml
     */
    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val dungeonPlayer = getDungeonPlayer(player)
        if (blockNotInMaze(player.location.block)) return

        if (dungeonPlayer.hardcoreEnabled) {
            player.sendMessage(
                "${RED}You cannot use commands in the maze if you have " +
                        "hardcore enabled."
            )
            event.isCancelled = true
        } else {
            val command = event.message.lowercase().substring(1)
            if ((command == "fly" || command == "god" ||
                        command == "heal" || command == "feed")
            ) {
                player.sendMessage(
                    "${RED}You cannot use /fly, /god or /heal in the maze."
                )
                event.isCancelled = true
            }
        }
    }

    /**
     * Stops creepers from blowing up blocks in the maze.
     */
    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (event.entity !is Creeper) return
        if (blockNotInMaze(event.location.block)) return
        event.blockList().clear()
    }

    /**
     * Stops players from teleporting into the maze.
     */
    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        if (event.to == null) return
        if (blockNotInMaze(event.to!!.block)) return
        event.isCancelled = true
        event.player.sendMessage("${RED}You cannot teleport into the maze!")
    }
}
