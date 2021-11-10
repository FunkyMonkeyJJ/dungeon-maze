package yhw.panda

import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.Color
import org.bukkit.Color.fromRGB
import org.bukkit.Material.*
import org.bukkit.enchantments.Enchantment.*
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType.*

enum class DungeonTreasure {
    COMMON, UNCOMMON, RARE;

    /**
     * Stores the [chance] that an ItemStack will be added somewhere
     * between [min] and [max] times to the DungeonTreasure.
     */
    data class Amount(val chance: Double, val min: Int, val max: Int)

    companion object {
        /**
         * Generates an inventory based on the given [tier] of rewards
         * and opens it for the given [player].
         *
         * @return the Inventory that was created so the rewards can be
         * extracted and used in their own ways.
         *
         * TODO: This will be updated when config.yml file is updated
         *  to give the user more control on rewards
         */
        fun generate(player: Player, tier: DungeonTreasure): Inventory {
            val dungeonPlayer = DungeonMaze.getDungeonPlayer(player)
            var i = 1
            if (dungeonPlayer.hardcoreEnabled) i = 2
            val inv = Bukkit.createInventory(player, 18)

            val chimkenJerky = ItemStack(COOKED_CHICKEN, 0)
            Utility.rename(chimkenJerky, "${GOLD}Chimken Jerky")

            val beefJerky = ItemStack(COOKED_BEEF, 0)
            Utility.rename(beefJerky, "${RED}Beef Jerky")

            val dungeonVision = Utility.generatePotion(
                NIGHT_VISION, 3600, 1, Color.NAVY,
                "${GOLD}Potion of Dungeon Vision"
            )

            val tonicOfStrength = Utility.generatePotion(
                INCREASE_DAMAGE, 4800, 1, Color.MAROON,
                "${DARK_RED}Tonic of Everlasting Strength"
            )

            val elixirOfLife = Utility.generatePotion(
                REGENERATION, 2400, 1, Color.SILVER,
                "${RED}Elixir of Life"
            )

            val beer = Utility.generatePotion(
                CONFUSION, 600, 1,
                fromRGB(217, 174, 8), "${GOLD}Beer"
            )

            val ironDagger = ItemStack(IRON_SWORD, 0)
            Utility.rename(ironDagger, "${WHITE}Iron Dagger")

            val wine = Utility.generatePotion(
                CONFUSION, 1800, 1,
                fromRGB(86, 0, 12), "${DARK_PURPLE}Wine"
            )

            val sharp4 = Utility.generateEnchantedBook(DAMAGE_ALL, 4)
            val sharp5 = Utility.generateEnchantedBook(DAMAGE_ALL, 5)
            val prot3 = Utility.generateEnchantedBook(
                PROTECTION_ENVIRONMENTAL, 3
            )
            val prot4 = Utility.generateEnchantedBook(
                PROTECTION_ENVIRONMENTAL, 4
            )
            val eff4 = Utility.generateEnchantedBook(DIG_SPEED, 4)
            val eff5 = Utility.generateEnchantedBook(DIG_SPEED, 5)
            val power4 =
                Utility.generateEnchantedBook(ARROW_DAMAGE, 4)
            val power5 =
                Utility.generateEnchantedBook(ARROW_DAMAGE, 5)
            val mending = Utility.generateEnchantedBook(MENDING, 1)

            when (tier) {
                COMMON -> {
                    val items = mapOf(
                        TORCH to Amount(0.6, 5, 20),
                        COAL to Amount(0.1, 3, 15),
                        IRON_NUGGET to
                                Amount(0.1, 4 * i, 20),
                        GOLD_NUGGET to
                                Amount(0.1 * i, 3, 20),
                        DIAMOND to Amount(0.1, 1, 3 * 1),
                        CLAY_BALL to Amount(0.1, 1, 3 * 1),
                        BREAD to Amount(0.3 * i, 2, 8),
                        IRON_SWORD to Amount(0.03 * i, 1, 2),
                        LEATHER_HELMET to Amount(0.06, 1, 1),
                        LEATHER_CHESTPLATE to
                                Amount(0.06, 1, 1),
                        LEATHER_LEGGINGS to
                                Amount(0.06, 1, 1),
                        LEATHER_BOOTS to Amount(0.06, 1, 1),
                        CHAINMAIL_HELMET to
                                Amount(0.04 * i, 1, 1),
                        CHAINMAIL_CHESTPLATE to
                                Amount(0.04 * i, 1, 1),
                        CHAINMAIL_LEGGINGS to
                                Amount(0.04 * i, 1, 1),
                        CHAINMAIL_BOOTS to
                                Amount(0.04 * i, 1, 1),
                        SADDLE to Amount(0.02, 1, i),
                        LEATHER_HORSE_ARMOR to
                                Amount(0.02, 1, i)
                    )

                    val customItems = mapOf(
                        chimkenJerky to
                                Amount(0.25, 1, 5 * 1),
                        dungeonVision to Amount(0.1, 1, i),
                        tonicOfStrength to Amount(0.1, 1, i),
                        elixirOfLife to Amount(0.1, 1, i),
                        beer to Amount(0.1, 1, 2 * i),
                        wine to Amount(0.1, 1, 2 * i),
                        ironDagger to Amount(0.03 * i, 1, 2),
                    )

                    items.forEach { (k, v) ->
                        val item = ItemStack(k, 0)
                        inv.addItem(randomAdd(item, v.chance, v.min, v.max))
                    }

                    customItems.forEach { (k, v) ->
                        inv.addItem(randomAdd(k, v.chance, v.min, v.max))
                    }
                }
                UNCOMMON -> {
                    val items = mapOf(
                        TORCH to Amount(0.3, 5, 20),
                        LANTERN to Amount(0.3, 5, 20),
                        COAL to Amount(0.1, 3, 20),
                        LAPIS_LAZULI to Amount(0.1, 4, 25),
                        IRON_NUGGET to
                                Amount(0.1 * i, 8, 40),
                        GOLD_NUGGET to
                                Amount(0.1 * i, 6, 40),
                        DIAMOND to Amount(0.1, 1, 10),
                        EMERALD to Amount(0.1 * i, 1, 5),
                        GOLDEN_APPLE to
                                Amount(0.25, 1, 3 * i),
                        BREAD to Amount(0.3, 2, 10),
                        IRON_SWORD to Amount(0.03 * i, 1, 2),
                        IRON_HELMET to Amount(0.03, 1, 1),
                        IRON_CHESTPLATE to Amount(0.03, 1, 1),
                        IRON_LEGGINGS to Amount(0.03, 1, 1),
                        IRON_BOOTS to Amount(0.03, 1, 1),
                        CHAINMAIL_HELMET to
                                Amount(0.05 * i, 1, 1),
                        CHAINMAIL_CHESTPLATE to
                                Amount(0.05 * i, 1, 1),
                        CHAINMAIL_LEGGINGS to
                                Amount(0.05 * i, 1, 1),
                        CHAINMAIL_BOOTS to
                                Amount(0.05 * i, 1, 1),
                        SADDLE to Amount(0.02, 1, i),
                        IRON_HORSE_ARMOR to
                                Amount(0.02, 1, i)
                    )

                    val customItems = mapOf(
                        chimkenJerky to
                                Amount(0.25, 2, 10 * 1),
                        beefJerky to
                                Amount(0.25, 2, 7 * 1),
                        dungeonVision to Amount(0.1, 1, i),
                        tonicOfStrength to Amount(0.1, 1, i),
                        elixirOfLife to Amount(0.1, 1, i),
                        beer to Amount(0.1, 1, 2 * i),
                        wine to Amount(0.1, 1, 2 * i),
                        ironDagger to Amount(0.03 * i, 1, 2),
                    )

                    items.forEach { (k, v) ->
                        val item = ItemStack(k, 0)
                        inv.addItem(randomAdd(item, v.chance, v.min, v.max))
                    }

                    customItems.forEach { (k, v) ->
                        inv.addItem(randomAdd(k, v.chance, v.min, v.max))
                    }
                }
                RARE -> {
                    val items = mapOf(
                        IRON_INGOT to Amount(0.1 * i, 5, 25),
                        GOLD_INGOT to Amount(0.1 * i, 3, 25),
                        DIAMOND to Amount(0.1 * i, 4, 20),
                        EMERALD to Amount(0.1 * i, 1, 10),
                        NETHERITE_INGOT to
                                Amount(0.25 * i, 1, 1),
                        NETHERITE_SCRAP to
                                Amount(0.5 * i, 1, 2),
                        GOLDEN_APPLE to
                                Amount(0.25 * i, 2, 10),
                        ENCHANTED_GOLDEN_APPLE to
                                Amount(0.25, 1, 3 * i),
                        IRON_SWORD to Amount(0.03 * i, 1, 2),
                        TRIDENT to Amount(0.1 * i, 1, 2),
                        IRON_HELMET to Amount(0.03, 1, 1),
                        IRON_CHESTPLATE to Amount(0.03, 1, 1),
                        IRON_LEGGINGS to Amount(0.03, 1, 1),
                        IRON_BOOTS to Amount(0.03, 1, 1),
                        SADDLE to Amount(0.02, 1, i),
                        IRON_HORSE_ARMOR to
                                Amount(0.02, 1, i)
                    )

                    val customItems = mapOf(
                        dungeonVision to Amount(0.1, 1, i),
                        tonicOfStrength to Amount(0.1, 1, i),
                        elixirOfLife to Amount(0.1, 1, i),
                        beer to Amount(0.1, 1, 3 * i),
                        wine to Amount(0.1, 1, 3 * i),
                        sharp4 to Amount(0.1, 1, i),
                        sharp5 to Amount(0.1, 1, i),
                        prot3 to Amount(0.1, 1, i),
                        prot4 to Amount(0.1, 1, i),
                        eff4 to Amount(0.1, 1, i),
                        eff5 to Amount(0.1, 1, i),
                        power4 to Amount(0.1, 1, i),
                        power5 to Amount(0.1, 1, i),
                        mending to Amount(0.25 * i, 1, i),
                        ironDagger to Amount(0.03 * i, 1, 2),
                    )

                    items.forEach { (k, v) ->
                        val item = ItemStack(k, 0)
                        inv.addItem(randomAdd(item, v.chance, v.min, v.max))
                    }

                    customItems.forEach { (k, v) ->
                        inv.addItem(randomAdd(k, v.chance, v.min, v.max))
                    }
                }
            }
            return inv
        }

        /**
         * Randomly adds [itemStack] between [minNumToAdd] times and
         * [maxNumToAdd] based on the given [percentage]. As long as
         * [percentage] is reached once, [minNumToAdd] is the minimum
         * number of [itemStack] will be given, incrementing by 1, until
         * [maxNumToAdd] is reached.
         */
        private fun randomAdd(
            itemStack: ItemStack, percentage: Double,
            minNumToAdd: Int, maxNumToAdd: Int
        ): ItemStack {
            for (i in minNumToAdd..maxNumToAdd)
                if (Math.random() <= percentage)
                    if (itemStack.amount == 0) itemStack.amount = minNumToAdd
                    else itemStack.amount = itemStack.amount + 1
            return itemStack
        }
    }

    override fun toString(): String = name.lowercase()
}
