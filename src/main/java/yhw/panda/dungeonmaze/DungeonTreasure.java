package yhw.panda.dungeonmaze;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

public enum DungeonTreasure {
    COMMON("common"),
    UNCOMMON("uncommon"),
    RARE("rare");

    public final String treasureTier;

    DungeonTreasure(String treasureTier) {
        this.treasureTier = treasureTier;
    }

    public static Inventory generate(Player player,
                                     DungeonTreasure tier) {
        DungeonPlayer dungeonPlayer = DungeonMaze.getDungeonPlayer(player);
        int i = 1;
        if (dungeonPlayer.hardcoreEnabled) i = 2;
        Inventory inv = Bukkit.createInventory(player, 18);
        switch (tier) {
            case COMMON:
                inv.addItem(randomAdd(new ItemStack(Material.TORCH, 0),
                        0.6, 5, 20));
                inv.addItem(randomAdd(new ItemStack(Material.COAL, 0),
                        0.1, 3, 15));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_NUGGET,
                                0), 0.1,
                        4 * i, 20));
                inv.addItem(randomAdd(new ItemStack(Material.GOLD_NUGGET,
                                0), 0.1 * i,
                        3, 20));
                inv.addItem(randomAdd(new ItemStack(Material.DIAMOND,
                                0), 0.1,
                        1, 3 * i));
                inv.addItem(randomAdd(new ItemStack(Material.CLAY_BALL,
                                0), 0.01,
                        7, 30));
                inv.addItem(randomAdd(new ItemStack(Material.BREAD, 0),
                        0.3 * i, 2, 8));

                ItemStack chimkenJerky = new ItemStack(
                        Material.COOKED_CHICKEN, 0);
                Utility.rename(chimkenJerky,
                        ChatColor.GOLD + "Chimken Jerky");
                inv.addItem(randomAdd(chimkenJerky, 0.25,
                        1, 5 * i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.NIGHT_VISION, 3600,
                        1, Color.NAVY, ChatColor.GOLD +
                                "Potion of Dungeon Vision"
                ), 0.1, 1, i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.INCREASE_DAMAGE, 4800,
                        1, Color.MAROON,
                        ChatColor.DARK_RED +
                                "Tonic of Everlasting Strength"
                ), 0.1, 1, i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.REGENERATION, 2400,
                        1, Color.SILVER,
                        ChatColor.RED + "Elixir of Life"
                ), 0.1, 1, i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.CONFUSION, 600,
                        1, Color.fromRGB(217, 174, 8),
                        ChatColor.GOLD + "Beer"
                ), 0.1, 1, 2 * i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.CONFUSION, 600,
                        1, Color.fromRGB(86, 0, 12),
                        ChatColor.DARK_PURPLE + "Wine"
                ), 0.1, 1, 2 * i));

                ItemStack ironDagger = new ItemStack(
                        Material.IRON_SWORD, 0);
                Utility.rename(ironDagger,
                        ChatColor.WHITE + "Iron Dagger");
                inv.addItem(randomAdd(ironDagger, 0.03 * i,
                        1, 2));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_SWORD,
                                0), 0.03 * i,
                        1, 2));

                inv.addItem(randomAdd(new ItemStack(Material.LEATHER_HELMET,
                                0), 0.06,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.
                                LEATHER_CHESTPLATE, 0),
                        0.03, 1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.LEATHER_LEGGINGS,
                                0), 0.06,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.LEATHER_BOOTS,
                                0), 0.06,
                        1, 1));

                inv.addItem(randomAdd(new ItemStack(Material.CHAINMAIL_HELMET,
                                0), 0.04 * i,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.
                                CHAINMAIL_CHESTPLATE, 0),
                        0.04 * i, 1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.
                                CHAINMAIL_LEGGINGS, 0),
                        0.04 * i, 1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.CHAINMAIL_BOOTS,
                                0), 0.04 * i,
                        1, 1));

                inv.addItem(randomAdd(new ItemStack(Material.SADDLE,
                                0), 0.02,
                        1, i));
                inv.addItem(randomAdd(new ItemStack(Material.
                                LEATHER_HORSE_ARMOR, 0),
                        0.02, 1, i));
                break;
            case UNCOMMON:
                inv.addItem(randomAdd(new ItemStack(Material.TORCH, 0),
                        0.3, 5, 20));
                inv.addItem(randomAdd(new ItemStack(Material.LANTERN,
                                0), 0.3,
                        5, 20));
                inv.addItem(randomAdd(new ItemStack(Material.COAL, 0),
                        0.1, 3, 20));
                inv.addItem(randomAdd(new ItemStack(Material.LAPIS_LAZULI,
                                0), 0.1,
                        4, 25));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_NUGGET,
                                0), 0.1 * i,
                        8, 40));
                inv.addItem(randomAdd(new ItemStack(Material.GOLD_NUGGET,
                                0), 0.1 * i,
                        6, 40));
                inv.addItem(randomAdd(new ItemStack(Material.DIAMOND,
                                0), 0.1 * i,
                        1, 10));
                inv.addItem(randomAdd(new ItemStack(Material.EMERALD,
                                0), 0.1 * i,
                        1, 3));

                ItemStack beefJerky = new ItemStack(
                        Material.COOKED_BEEF, 0);
                Utility.rename(beefJerky, ChatColor.RED + "Beef Jerky");
                inv.addItem(randomAdd(beefJerky, 0.25,
                        2, 7 * i));

                chimkenJerky = new ItemStack(
                        Material.COOKED_CHICKEN, 0);
                Utility.rename(chimkenJerky,
                        ChatColor.GOLD + "Chimken Jerky");
                inv.addItem(randomAdd(chimkenJerky, 0.25,
                        2, 10 * i));

                inv.addItem(randomAdd(new ItemStack(Material.GOLDEN_APPLE),
                        0.25, 1, 3 * i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.NIGHT_VISION, 3600,
                        1, Color.NAVY, ChatColor.GOLD +
                                "Potion of Dungeon Vision"
                ), 0.1, 1, i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.INCREASE_DAMAGE, 4800,
                        1, Color.MAROON,
                        ChatColor.DARK_RED +
                                "Tonic of Everlasting Strength"
                ), 0.1, 1, i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.REGENERATION, 2400,
                        1, Color.SILVER,
                        ChatColor.RED + "Elixir of Life"
                ), 0.1, 1, i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.CONFUSION, 600,
                        1, Color.fromRGB(217, 174, 8),
                        ChatColor.GOLD + "Beer"
                ), 0.1, 1, 2 * i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.CONFUSION, 600,
                        1, Color.fromRGB(86, 0, 12),
                        ChatColor.DARK_PURPLE + "Wine"
                ), 0.1, 1, 2 * i));

                ironDagger = new ItemStack(Material.IRON_SWORD, 0);
                Utility.rename(ironDagger,
                        ChatColor.WHITE + "Iron Dagger");
                inv.addItem(randomAdd(ironDagger, 0.03 * i,
                        1, 2));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_SWORD,
                                0), 0.03 * i,
                        1, 2));

                inv.addItem(randomAdd(new ItemStack(Material.CHAINMAIL_HELMET,
                                0), 0.05 * i,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.
                                CHAINMAIL_CHESTPLATE, 0),
                        0.05 * i, 1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.
                                CHAINMAIL_LEGGINGS, 0),
                        0.05 * i, 1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.CHAINMAIL_BOOTS,
                                0), 0.05 * i,
                        1, 1));

                inv.addItem(randomAdd(new ItemStack(Material.IRON_HELMET,
                                0), 0.03,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_CHESTPLATE,
                                0), 0.03,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_LEGGINGS,
                                0), 0.03,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_BOOTS,
                                0), 0.03,
                        1, 1));

                inv.addItem(randomAdd(new ItemStack(Material.SADDLE,
                        0), 0.02, 1, i));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_HORSE_ARMOR,
                        0), 0.02, 1, i));
                break;
            case RARE:
                inv.addItem(randomAdd(new ItemStack(Material.IRON_INGOT,
                                0), 0.1 * i,
                        5, 25));
                inv.addItem(randomAdd(new ItemStack(Material.GOLD_INGOT,
                                0), 0.1 * i,
                        3, 25));
                inv.addItem(randomAdd(new ItemStack(Material.DIAMOND,
                                0), 0.1 * i,
                        4, 20));
                inv.addItem(randomAdd(new ItemStack(Material.EMERALD,
                                0), 0.1 * i,
                        1, 10));
                inv.addItem(randomAdd(new ItemStack(Material.NETHERITE_INGOT,
                                0), 0.5 * i,
                        1, 1));

                beefJerky = new ItemStack(Material.COOKED_BEEF, 0);
                Utility.rename(beefJerky, ChatColor.RED + "Beef Jerky");
                inv.addItem(randomAdd(beefJerky, 0.25,
                        2, 7 * i));

                chimkenJerky = new ItemStack(
                        Material.COOKED_CHICKEN, 0);
                Utility.rename(chimkenJerky,
                        ChatColor.GOLD + "Chimken Jerky");
                inv.addItem(randomAdd(chimkenJerky, 0.25,
                        2, 10 * i));

                inv.addItem(randomAdd(new ItemStack(
                                Material.ENCHANTED_GOLDEN_APPLE),
                        0.25, 1, 3 * i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.NIGHT_VISION, 3600,
                        1, Color.NAVY, ChatColor.GOLD +
                                "Potion of Dungeon Vision"
                ), 0.1, 1, i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.INCREASE_DAMAGE, 4800,
                        1, Color.MAROON,
                        ChatColor.DARK_RED +
                                "Tonic of Everlasting Strength"
                ), 0.1, 1, i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.REGENERATION, 2400,
                        1, Color.SILVER,
                        ChatColor.RED + "Elixir of Life"
                ), 0.1, 1, i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.CONFUSION, 600,
                        1, Color.fromRGB(217, 174, 8),
                        ChatColor.GOLD + "Beer"
                ), 0.1, 1, 3 * i));

                inv.addItem(randomAdd(Utility.generatePotion(
                        PotionEffectType.CONFUSION, 600,
                        1, Color.fromRGB(86, 0, 12),
                        ChatColor.DARK_PURPLE + "Wine"
                ), 0.1, 1, 3 * i));

                inv.addItem(randomAdd(Utility.generateEnchantedBook(
                        Enchantment.DAMAGE_ALL, 4),
                        0.1, 1, i));
                inv.addItem(randomAdd(Utility.generateEnchantedBook(
                        Enchantment.DAMAGE_ALL, 5),
                        0.1, 1, i));

                inv.addItem(randomAdd(Utility.generateEnchantedBook(
                        Enchantment.PROTECTION_ENVIRONMENTAL, 3),
                        0.1, 1, i));
                inv.addItem(randomAdd(Utility.generateEnchantedBook(
                        Enchantment.PROTECTION_ENVIRONMENTAL, 4),
                        0.1, 1, i));

                inv.addItem(randomAdd(Utility.generateEnchantedBook(
                        Enchantment.DIG_SPEED, 4),
                        0.1, 1, i));
                inv.addItem(randomAdd(Utility.generateEnchantedBook(
                        Enchantment.DIG_SPEED, 5),
                        0.1, 1, i));

                inv.addItem(randomAdd(Utility.generateEnchantedBook(
                        Enchantment.ARROW_DAMAGE, 4),
                        0.1, 1, i));
                inv.addItem(randomAdd(Utility.generateEnchantedBook(
                        Enchantment.ARROW_DAMAGE, 5),
                        0.1, 1, i));

                inv.addItem(randomAdd(Utility.generateEnchantedBook(
                        Enchantment.MENDING, 1),
                        0.2, 1, i));

                ironDagger = new ItemStack(Material.IRON_SWORD, 0);
                Utility.rename(ironDagger,
                        ChatColor.WHITE + "Iron Dagger");
                inv.addItem(randomAdd(ironDagger, 0.03 * i,
                        1, 2));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_SWORD,
                                0), 0.03 * i,
                        1, 2));
                inv.addItem(randomAdd(new ItemStack(Material.TRIDENT,
                                0), 0.1 * i,
                        1, 2));

                inv.addItem(randomAdd(new ItemStack(Material.IRON_HELMET,
                                0), 0.03,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_CHESTPLATE,
                                0), 0.03,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_LEGGINGS,
                                0), 0.03,
                        1, 1));
                inv.addItem(randomAdd(new ItemStack(Material.IRON_BOOTS,
                                0), 0.03,
                        1, 1));

                inv.addItem(randomAdd(new ItemStack(Material.SADDLE,
                        0), 0.02, 1, i));
                inv.addItem(randomAdd(new ItemStack(
                                Material.DIAMOND_HORSE_ARMOR, 0),
                        0.02, 1, i));
        }
        return inv;
    }

    private static ItemStack randomAdd(ItemStack itemStack, double percentage,
                                       int minNumToAdd, int maxNumToAdd) {
        for (int i = minNumToAdd; i <= maxNumToAdd; i++)
            if (Math.random() <= percentage)
                if (itemStack.getAmount() == 0)
                    itemStack.setAmount(minNumToAdd);
                else itemStack.setAmount(itemStack.getAmount() + 1);
        return itemStack;
    }
}
