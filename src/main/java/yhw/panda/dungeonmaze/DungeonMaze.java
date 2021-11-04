package yhw.panda.dungeonmaze;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class DungeonMaze extends JavaPlugin {
    protected static Plugin plugin;
    protected static DungeonListener dungeonListener;

    protected static List<DungeonPlayer> dungeonPlayers = new LinkedList<>();
    protected static Location hardcoreSwitchLocation;
    protected static Location upperMazeCorner;
    protected static Location lowerMazeCorner;
    protected static BukkitScheduler scheduler;

    @Override
    public void onEnable() {
        plugin = this;
        createConfig();
        try {
            World world = this.getServer().getWorld(Objects.requireNonNull(
                    this.getConfig().getString("maze.world")));
            hardcoreSwitchLocation = new Location(world,
                    this.getConfig().getInt("maze.hardcore-switch.x"),
                    this.getConfig().getInt("maze.hardcore-switch.y"),
                    this.getConfig().getInt("maze.hardcore-switch.z"));
            upperMazeCorner = new Location(world,
                    this.getConfig().getInt("maze.upper-limit.x"),
                    this.getConfig().getInt("maze.upper-limit.y"),
                    this.getConfig().getInt("maze.upper-limit.z"));
            lowerMazeCorner = new Location(world,
                    this.getConfig().getInt("maze.lower-limit.x"),
                    this.getConfig().getInt("maze.lower-limit.y"),
                    this.getConfig().getInt("maze.lower-limit.z"));
        } catch (Exception ignored) {
            System.out.println("A problem occured and the hardcore switch " +
                    "has not been established. Please fix this before you " +
                    "continue.");
            return;
        }
        scheduler = this.getServer().getScheduler();
        dungeonListener = new DungeonListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (label.equals("chestrefresh") && player.isOp()) {
            for (DungeonPlayer dungeonPlayer : dungeonPlayers)
                if (dungeonPlayer.compareTo(player) == 0) {
                    dungeonPlayers.remove(dungeonPlayer);
                    dungeonPlayers.add(new DungeonPlayer(player));
                    break;
                }
            player.sendMessage(ChatColor.AQUA +
                    "Your chests have been refreshed for the dungeon.");
        }
        return true;
    }

    private void createConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    protected static DungeonPlayer getDungeonPlayer(Player player) {
        for (DungeonPlayer dungeonPlayer : dungeonPlayers)
            if (dungeonPlayer.compareTo(player) == 0) return dungeonPlayer;
        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
        dungeonPlayers.add(dungeonPlayer);
        return dungeonPlayer;
    }
}
