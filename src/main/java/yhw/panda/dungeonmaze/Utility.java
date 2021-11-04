package yhw.panda.dungeonmaze;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Utility {
    public static void rename(ItemStack itemStack, String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
    }

    public static ItemStack generatePotion(PotionEffectType potionEffect,
                                           int duration, int amplifier,
                                           Color customColor,
                                           String customName) {
        ItemStack potion = new ItemStack(Material.POTION, 0);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        assert potionMeta != null;
        potionMeta.addCustomEffect(new PotionEffect(potionEffect,
                duration, amplifier), true);
        if (customColor != null) potionMeta.setColor(customColor);
        if (customName != null && !customName.equals(""))
            potionMeta.setDisplayName(customName);
        potion.setItemMeta(potionMeta);
        return potion;
    }

    public static ItemStack generateEnchantedBook(Enchantment enchantment,
                                                  int amplifier) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 0);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta)
                book.getItemMeta();
        assert bookMeta != null;
        bookMeta.addStoredEnchant(enchantment, amplifier,
                false);
        book.setItemMeta(bookMeta);
        return book;
    }
}
