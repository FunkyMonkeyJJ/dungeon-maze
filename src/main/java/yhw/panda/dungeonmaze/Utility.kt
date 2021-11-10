package yhw.panda

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object Utility {
    /**
     * Simply renames the given [itemStack] with the given [name].
     */
    fun rename(itemStack: ItemStack, name: String) {
        val itemMeta = itemStack.itemMeta ?: return
        itemMeta.setDisplayName(name)
        itemStack.itemMeta = itemMeta
    }

    /**
     * Generates a potion ItemStack with the given parameters.
     * If the [color] is null, the color will be water bottle color.
     * If the [name] is null or "", the name will be Potion.
     *
     * @retyrn the potion that is created.
     */
    fun generatePotion(
        potionEffect: PotionEffectType, duration: Int,
        amplifier: Int, color: Color?, name: String?
    ): ItemStack {
        val potion = ItemStack(Material.POTION, 0)
        val potionMeta = (potion.itemMeta as PotionMeta?)!!
        potionMeta.addCustomEffect(
            PotionEffect(potionEffect, duration, amplifier), true
        )
        if (color != null) potionMeta.color = color
        if (name != null && name != "") potionMeta.setDisplayName(name)
        potion.itemMeta = potionMeta
        return potion
    }

    /**
     * Generates an enchanted book with the given [enchantment] and
     * [amplifier].
     *
     * @return the enchanted book that is created.
     */
    fun generateEnchantedBook(enchantment: Enchantment, amplifier: Int):
            ItemStack {
        val book = ItemStack(Material.ENCHANTED_BOOK, 0)
        val bookMeta = (book.itemMeta as EnchantmentStorageMeta?)!!
        bookMeta.addStoredEnchant(
            enchantment, amplifier, false
        )
        book.itemMeta = bookMeta
        return book
    }
}
