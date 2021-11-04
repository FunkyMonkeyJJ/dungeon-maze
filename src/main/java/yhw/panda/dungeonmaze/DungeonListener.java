package yhw.panda.dungeonmaze;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.bukkit.Material.*;

public class DungeonListener implements Listener {
    private final Plugin plugin;

    DungeonListener(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (DungeonPlayer dungeonPlayer : DungeonMaze.dungeonPlayers)
            if (dungeonPlayer.compareTo(player) == 0) return;
        DungeonMaze.dungeonPlayers.add(new DungeonPlayer(player));
    }

    @EventHandler
    void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DungeonMaze.dungeonPlayers.removeIf(dungeonPlayer ->
                dungeonPlayer.compareTo(player) == 0);
    }

    @EventHandler
    void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        switch (block.getType()) {
            case CHEST:
            case TRAPPED_CHEST:
            case BARREL:
                if (blockNotInMaze(block)) return;
                event.setCancelled(true);
                openTreasure(DungeonMaze.getDungeonPlayer(
                        player), block.getLocation());
        }
    }

    boolean blockNotInMaze(Block block) {
        String worldName = plugin.getConfig().getString("maze.world");
        if (worldName == null) return true;
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) return true;

        try {
            return !locationInArea(block.getLocation(),
                    DungeonMaze.lowerMazeCorner,
                    DungeonMaze.upperMazeCorner);
        } catch (Exception ignored) {
            return true;
        }
    }

    boolean locationInArea(Location mainLocation, Location lowerBound,
                           Location upperBound) {
        return ((mainLocation.getX() >= lowerBound.getX() &&
                mainLocation.getX() <= upperBound.getX()) ||
                (mainLocation.getX() <= lowerBound.getX() &&
                        mainLocation.getX() >= upperBound.getX())) &&
                (mainLocation.getY() >= lowerBound.getY() &&
                        mainLocation.getY() <= upperBound.getY()) &&
                ((mainLocation.getZ() >= lowerBound.getZ() &&
                        mainLocation.getZ() <= upperBound.getZ()) ||
                        (mainLocation.getZ() <= lowerBound.getZ() &&
                                mainLocation.getZ() >= upperBound.getZ()));
    }

    void openTreasure(DungeonPlayer dungeonPlayer, Location location) {
        for (DungeonTreasure dungeonTreasure : DungeonTreasure.values())
            for (int i = 1; i < 5; i++) {
                double treasureX = plugin.getConfig().
                        getDouble(String.format("treasure.%s.%d.x",
                                dungeonTreasure.treasureTier, i));
                double treasureY = plugin.getConfig().
                        getDouble(String.format("treasure.%s.%d.y",
                                dungeonTreasure.treasureTier, i));
                double treasureZ = plugin.getConfig().
                        getDouble(String.format("treasure.%s.%d.z",
                                dungeonTreasure.treasureTier, i));

                if (location.getX() == treasureX &&
                        location.getY() == treasureY &&
                        location.getZ() == treasureZ) {
                    ArrayList<LocalDate> treasureOpened;

                    switch (dungeonTreasure) {
                        case RARE:
                            treasureOpened = dungeonPlayer.
                                    rareTreasureLastOpened;
                            break;
                        case UNCOMMON:
                            treasureOpened = dungeonPlayer.
                                    uncommonTreasureLastOpened;
                            break;
                        default:
                            treasureOpened = dungeonPlayer.
                                    commonTreasureLastOpened;
                    }

                    if (treasureOpened.get(i - 1).
                            equals(LocalDate.now())) {
                        dungeonPlayer.player.sendMessage(ChatColor.RED +
                                "You have already looted this booty today!");
                        return;
                    }

                    Player player = dungeonPlayer.player;
                    treasureOpened.set(i - 1, LocalDate.now());

                    player.openInventory(DungeonTreasure.generate(
                            player, dungeonTreasure));

                    ChatColor tierChatColor;
                    switch (dungeonTreasure) {
                        case RARE:
                            tierChatColor = ChatColor.DARK_PURPLE;
                            break;
                        case UNCOMMON:
                            tierChatColor = ChatColor.BLUE;
                            break;
                        case COMMON:
                        default:
                            tierChatColor = ChatColor.GREEN;
                    }

                    player.sendMessage(tierChatColor +
                            "You have opened a " + dungeonTreasure.
                            treasureTier + " chest.");
                    return;
                }
            }
    }

    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (blockNotInMaze(block)) return;
        switch (block.getType()) {
            case COBWEB:
            case TRIPWIRE:
            case STONE_PRESSURE_PLATE:
                block.getDrops().clear();
                Material originalBlockType = block.getType();
                DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                        () -> {
                            if (block.getType() == Material.AIR)
                                block.setType(originalBlockType);
                        }, 600L);
                // TODO: Timer to replace the block in 1 minute (240).
                //  Variable for Easy mode, maybe 1 minute.
                break;
            case SAND:
            case GRAVEL:
            case RED_SAND:
            case GRAY_CONCRETE_POWDER:
                // TODO: Just allows the player to break these from traps.
                //  Spawning them in will start a remove timer.
                break;
            default:
                if (player.isOp()) return;
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED +
                        "You cannot break blocks in the maze " +
                        "except traps that block the way.");
        }
    }

    @EventHandler
    void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (blockNotInMaze(block)) return;
        long i = 1;
        DungeonPlayer dungeonPlayer = DungeonMaze.getDungeonPlayer(player);
        if (dungeonPlayer.hardcoreEnabled) i = 2;
        switch (block.getType()) {
            case TORCH:
            case REDSTONE_TORCH:
            case WALL_TORCH:
            case REDSTONE_WALL_TORCH:
            case SOUL_TORCH:
            case SOUL_WALL_TORCH:
                DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                        () -> block.setType(Material.AIR), 600 * i);
                break;
            case LANTERN:
            case SOUL_LANTERN:
            case JACK_O_LANTERN:
            case CAMPFIRE:
            case SOUL_CAMPFIRE:
                DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                        () -> block.setType(Material.AIR), 1200 * i);
                // TODO: Might want to add particles for more realism.
                break;
            default:
                if (player.isOp()) return;
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED +
                        "You cannot place blocks in the maze " +
                        "except ones that will light the way.");
        }
    }

    @EventHandler
    void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.PHYSICAL ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
        if (blockNotInMaze(block)) return;
        Location blockLocation = block.getLocation();
        World world = blockLocation.getWorld();
        if (world == null) return;
        Player player = event.getPlayer();
        switch (event.getClickedBlock().getType()) {
            case OAK_BUTTON:
                if (blockNearby(blockLocation, 5, BEACON)) {
                    player.sendMessage(ChatColor.AQUA + "You find yourself " +
                            "very tired from your long journey...");
                    AtomicInteger count = new AtomicInteger(10);
                    DungeonMaze.scheduler.scheduleSyncRepeatingTask(plugin,
                            () -> {
                                if (count.getAndDecrement() > 0) {
                                    world.spawnParticle(Particle.CLOUD,
                                            blockLocation, 100);
                                }
                            }, 10, 10);
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            () -> {
                                if (player.getBedSpawnLocation() != null)
                                    player.teleport(
                                            player.getBedSpawnLocation());
                                else player.teleport(
                                        world.getSpawnLocation());
                            }, 100);
                    return;
                }
                DungeonPlayer dungeonPlayer = DungeonMaze.
                        getDungeonPlayer(player);
                dungeonPlayer.hardcoreEnabled =
                        !dungeonPlayer.hardcoreEnabled;
                if (dungeonPlayer.hardcoreEnabled)
                    player.sendMessage(ChatColor.RED + "The Hardcore " +
                            "Maze Protocol has been enabled.");
                else player.sendMessage(ChatColor.GREEN + "The Hardcore " +
                        "Maze Protocol has been disabled.");
                break;
            case TRIPWIRE:
                if (blockNearby(blockLocation, 4,
                        Material.OBSIDIAN)) {
                    Block explosiveChargeBlock = getBlockNearby(
                            blockLocation, 10,
                            Material.BLAST_FURNACE);
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            () -> world.createExplosion(
                                    explosiveChargeBlock.getLocation(),
                                    2F, false,
                                    false), 15L);
                }
                break;
            case STONE_PRESSURE_PLATE:
                if (blockNearby(blockLocation, 2,
                        Material.SANDSTONE)) {
                    int x = (int) blockLocation.getX();
                    int y = (int) blockLocation.getY();
                    int z = (int) blockLocation.getZ();
                    if (blockLocation.getWorld() == null) return;

                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            () -> {
                                Block potentialAir;
                                for (int i = -4; i < 5; i++)
                                    for (int j = 0; j < 5; j++)
                                        for (int k = -4; k < 5; k++) {
                                            if (Math.random() > 0.8)
                                                continue;
                                            potentialAir = blockLocation.
                                                    getWorld().getBlockAt(
                                                    x + i, y + j,
                                                    z + k);
                                            if (potentialAir.getType() !=
                                                    Material.AIR) continue;
                                            switch ((int)
                                                    (Math.random() * 3)) {
                                                case 0:
                                                    potentialAir.setType(
                                                            SAND);
                                                    break;
                                                case 1:
                                                    potentialAir.setType(
                                                            RED_SAND);
                                                    break;
                                                case 2:
                                                    potentialAir.setType(
                                                            GRAVEL);
                                            }
                                        }
                            }, 10L);
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            () -> {
                                Block potentialSand;
                                for (int i = -4; i < 5; i++)
                                    for (int j = 0; j < 5; j++)
                                        for (int k = -4; k < 5; k++) {
                                            potentialSand = blockLocation.
                                                    getWorld().getBlockAt(
                                                    x + i, y + j,
                                                    z + k);
                                            switch (potentialSand.
                                                    getType()) {
                                                case SAND:
                                                case RED_SAND:
                                                case GRAVEL:
                                                case GRAY_CONCRETE_POWDER:
                                                    potentialSand.setType(
                                                            Material.AIR);
                                            }
                                        }
                            }, 1200L);
                    return;
                }

                dungeonPlayer = DungeonMaze.getDungeonPlayer(player);
                if (!dungeonPlayer.hardcoreEnabled) return;

                if (blockNearby(blockLocation, 2,
                        Material.OBSIDIAN)) {
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            () -> world.createExplosion(blockLocation,
                                    2F, false,
                                    false), 10L);
                    return;
                }

                if (blockNearby(blockLocation, 2,
                        Material.BREWING_STAND)) {
                    ThrownPotion splashPotion =
                            (ThrownPotion) world.spawnEntity(blockLocation,
                                    EntityType.SPLASH_POTION);
                    switch ((int) (Math.random() * 4)) {
                        case 0:
                            ItemStack potion = new ItemStack(
                                    Material.SPLASH_POTION);
                            PotionMeta potionMeta =
                                    (PotionMeta) potion.getItemMeta();
                            assert potionMeta != null;
                            potionMeta.setColor(Color.BLACK);
                            potionMeta.addCustomEffect(new PotionEffect(
                                    PotionEffectType.BLINDNESS,
                                    400, 1), true);
                            potion.setItemMeta(potionMeta);
                            splashPotion.setItem(potion);
                            return;
                        case 1:
                            potion = new ItemStack(Material.SPLASH_POTION);
                            potionMeta = (PotionMeta) potion.getItemMeta();
                            assert potionMeta != null;
                            potionMeta.setColor(Color.ORANGE);
                            potionMeta.addCustomEffect(new PotionEffect(
                                    PotionEffectType.CONFUSION,
                                    400, 1), true);
                            potion.setItemMeta(potionMeta);
                            splashPotion.setItem(potion);
                            return;
                        case 2:
                            potion = new ItemStack(Material.SPLASH_POTION);
                            potionMeta = (PotionMeta) potion.getItemMeta();
                            assert potionMeta != null;
                            potionMeta.setColor(Color.GREEN);
                            potionMeta.addCustomEffect(new PotionEffect(
                                    PotionEffectType.POISON,
                                    300, 3), true);
                            potion.setItemMeta(potionMeta);
                            splashPotion.setItem(potion);
                            return;
                        case 3:
                            potion = new ItemStack(Material.SPLASH_POTION);
                            potionMeta = (PotionMeta) potion.getItemMeta();
                            assert potionMeta != null;
                            potionMeta.setColor(Color.BLACK);
                            potionMeta.addCustomEffect(new PotionEffect(
                                    PotionEffectType.WITHER,
                                    300, 2), true);
                            potion.setItemMeta(potionMeta);
                            splashPotion.setItem(potion);
                    }
                    return;
                }

                if (blockNearby(blockLocation, 2,
                        Material.FLETCHING_TABLE)) {
                    Block dispenserBlock = getBlockNearby(blockLocation,
                            10, Material.DISPENSER);
                    if (dispenserBlock == null) return;
                    if (dispenserBlock.getType() != Material.DISPENSER)
                        return;
                    Dispenser dispenser =
                            (Dispenser) dispenserBlock.getState();
                    dispenser.getInventory().addItem(
                            new ItemStack(Material.ARROW, 6));
                    dispenser.dispense();
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            dispenser::dispense, 5L);
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            dispenser::dispense, 10L);
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            dispenser::dispense, 15L);
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            dispenser::dispense, 20L);
                    DungeonMaze.scheduler.scheduleSyncDelayedTask(plugin,
                            dispenser::dispense, 25L);
                }
        }
    }

    boolean blockNearby(Location location, int radiusToCheck,
                        Material materialToCheck) {
        return getBlockNearby(location,
                radiusToCheck, materialToCheck) != null;
    }

    Block getBlockNearby(Location location, int radiusToCheck,
                         Material materialToCheck) {
        for (int i = -radiusToCheck; i < radiusToCheck; i++)
            for (int j = -radiusToCheck; j < radiusToCheck; j++)
                for (int k = -radiusToCheck; k < radiusToCheck; k++) {
                    World world = location.getWorld();
                    if (world == null) return null;
                    Block checkBlock = location.getWorld().getBlockAt(
                            location.clone().add(i, j, k));
                    if (checkBlock.getType() == materialToCheck)
                        return checkBlock;
                }
        return null;
    }

    @EventHandler
    void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        DungeonPlayer dungeonPlayer = DungeonMaze.getDungeonPlayer(player);
        if (dungeonPlayer.hardcoreEnabled && !(blockNotInMaze(
                player.getLocation().getBlock())) && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "You cannot use commands " +
                    "in the maze if you have hardcore enabled.");
            event.setCancelled(true);
        } else if (!(blockNotInMaze(player.getLocation().getBlock()))) {
            if (event.getMessage().equalsIgnoreCase("/fly") ||
                    event.getMessage().equalsIgnoreCase("/god") ||
                    event.getMessage().equalsIgnoreCase(
                            "/heal") ||
                    event.getMessage().equalsIgnoreCase(
                            "/feed")) {
                player.sendMessage(ChatColor.RED + "You cannot use " +
                        "/fly, /god or /heal in the maze.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Creeper)) return;
        if (blockNotInMaze(Objects.requireNonNull(
                event.getLocation()).getBlock())) return;
        event.blockList().clear();
    }

    @EventHandler
    void onTeleport(PlayerTeleportEvent event) {
        if (blockNotInMaze(Objects.requireNonNull(
                event.getTo()).getBlock())) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "You cannot " +
                "teleport into the maze!");
    }
}
