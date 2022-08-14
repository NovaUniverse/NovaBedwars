package net.novauniverse.bedwars.game.enums;

import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.utils.PotionItemBuilder;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum Items {
    WOOL(Material.WOOL,ColoredBlockType.WOOL,16, ItemCategory.BLOCK, new Price(Material.IRON_INGOT, 4), "wool"),
    CLAY(Material.STAINED_CLAY,ColoredBlockType.CLAY, 16, ItemCategory.BLOCK, new Price(Material.IRON_INGOT, 12), "hardened_clay"),
    ENDSTONE(VersionIndependentMaterial.END_STONE, 12, ItemCategory.BLOCK, new Price(Material.IRON_INGOT, 24), "end_stone"),
    LADDER(Material.LADDER, 6, ItemCategory.BLOCK, new Price(Material.IRON_INGOT, 4), "ladder"),
    WOOD(Material.WOOD, 16, ItemCategory.BLOCK, new Price(Material.GOLD_INGOT, 4), "oak_wood_planks"),
    OBSIDIAN(Material.OBSIDIAN, 4, ItemCategory.BLOCK, new Price(Material.EMERALD, 4), "obsidian"),
    STONE_SWORD(Material.STONE_SWORD, 1, ItemCategory.COMBAT, new Price(Material.IRON_INGOT, 10), "stone_sword"),
    IRON_SWORD(Material.IRON_SWORD, 1, ItemCategory.COMBAT, new Price(Material.GOLD_INGOT, 7), "iron_sword"),
    DIAMOND_SWORD(Material.DIAMOND_SWORD, 1, ItemCategory.COMBAT, new Price(Material.EMERALD, 4), "diamond_sword"),
    KB_STICK(new ItemBuilder(Material.STICK).addEnchant(Enchantment.KNOCKBACK, 1).setAmount(1).build(), ItemCategory.COMBAT, new Price(Material.GOLD_INGOT, 5), "stick_(knockback_i)"),
    GOLD_ARMOR(ArmorType.GOLD, ItemCategory.COMBAT, new Price(Material.IRON_INGOT, 10), null),
    CHAINMAIL_ARMOR(ArmorType.CHAINMAIL, ItemCategory.COMBAT, new Price(Material.IRON_INGOT, 40), "chainmail_boots"),
    IRON_ARMOR(ArmorType.IRON, ItemCategory.COMBAT, new Price(Material.GOLD_INGOT, 12),"iron_boots"),
    DIAMOND_ARMOR(ArmorType.DIAMOND, ItemCategory.COMBAT, new Price(Material.EMERALD, 6),"diamond_boots"),
    SHEARS(Material.SHEARS, 1, ItemCategory.TOOLS, new Price(Material.IRON_INGOT,20),"shears"),
    WOOD_PICKAXE(new ItemBuilder(VersionIndependentMaterial.WOODEN_PICKAXE).addEnchant(Enchantment.DIG_SPEED,1).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 10),"wooden_pickaxe", 1),
    STONE_PICKAXE(new ItemBuilder(Material.STONE_PICKAXE).addEnchant(Enchantment.DIG_SPEED,1).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.IRON_INGOT,10),"wooden_pickaxe", 2),
    IRON_PICKAXE(new ItemBuilder(Material.IRON_PICKAXE).addEnchant(Enchantment.DIG_SPEED,2).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 20), "wooden_pickaxe",3),
    GOLD_PICKAXE(new ItemBuilder(VersionIndependentMaterial.GOLDEN_PICKAXE).addEnchant(Enchantment.DIG_SPEED,3).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.GOLD_INGOT, 3),"wooden_pickaxe", 4),
    DIAMOND_PICKAXE(new ItemBuilder(Material.DIAMOND_PICKAXE).addEnchant(Enchantment.DIG_SPEED,3).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.GOLD_INGOT, 6), "wooden_pickaxe", 5),
    WOOD_AXE(new ItemBuilder(VersionIndependentMaterial.WOODEN_AXE).addEnchant(Enchantment.DIG_SPEED,1).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 10), "wooden_axe",1),
    STONE_AXE(new ItemBuilder(Material.STONE_AXE).addEnchant(Enchantment.DIG_SPEED,1).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 10), "wooden_axe", 2),
    IRON_AXE(new ItemBuilder(Material.IRON_AXE).addEnchant(Enchantment.DIG_SPEED,2).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 20), "wooden_axe", 3),
    GOLD_AXE(new ItemBuilder(VersionIndependentMaterial.GOLDEN_AXE).addEnchant(Enchantment.DIG_SPEED,3).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.GOLD_INGOT, 3), "wooden_axe", 4),
    DIAMOND_AXE(new ItemBuilder(Material.DIAMOND_AXE).addEnchant(Enchantment.DIG_SPEED,3).setUnbreakable(true).build(), ItemCategory.TOOLS, new Price(Material.GOLD_INGOT, 6), "wooden_axe", 5),
    ARROW(Material.ARROW, 8, ItemCategory.COMBAT, new Price(Material.GOLD_INGOT,2), "arrow"),
    BOW(Material.BOW, 1, ItemCategory.COMBAT, new Price(Material.GOLD_INGOT,12), "bow"),
    BOW_POWER_1(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1).build(),ItemCategory.COMBAT, new Price(Material.GOLD_INGOT, 24), "bow_(power_i)"),
    BOW_PUNCH_1_POWER_1(new ItemBuilder(Material.BOW).addEnchant(Enchantment.DAMAGE_ALL, 1).addEnchant(Enchantment.ARROW_KNOCKBACK, 1).build(), ItemCategory.COMBAT, new Price(Material.EMERALD,6), "bow_(power_i__punch_i)"),
    INVISIBLE(new PotionItemBuilder(Material.POTION).setPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,30 * 20,0, false, false)).build(), ItemCategory.POTIONS, new Price(Material.EMERALD,2), "invisibility_potion_(30_seconds)"),
    JUMP_BOOST(new PotionItemBuilder(Material.POTION).setPotionEffect(new PotionEffect(PotionEffectType.JUMP,45 * 20,4, false, false)).build(), ItemCategory.POTIONS, new Price(Material.EMERALD,1), "jump_v_potion_(45_seconds)"),
    SPEED(new PotionItemBuilder(Material.POTION).setPotionEffect(new PotionEffect(PotionEffectType.SPEED,45 * 20,1, false, false)).build(), ItemCategory.POTIONS,new Price(Material.EMERALD, 1), "speed_ii_potion_(45_seconds)"),
    GOLDEN_APPLE(Material.GOLDEN_APPLE, 1, ItemCategory.MISC, new Price(Material.GOLD_INGOT,3), "golden_apple")
    ,FIREBALL(Material.FIREBALL, 1, ItemCategory.MISC, new Price(Material.IRON_INGOT, 40), "fireball"),
    TNT(Material.TNT, 1, ItemCategory.MISC, new Price(Material.GOLD_INGOT,6), "tnt"),
    ENDER_PEARL(Material.ENDER_PEARL, 1, ItemCategory.MISC, new Price(Material.EMERALD, 4), "ender_pearl"),
    WATER_BUCKET(Material.WATER_BUCKET, 1, ItemCategory.MISC, new Price(Material.GOLD_INGOT, 3), "water_bucket"),
    SPONGE(Material.SPONGE, 2, ItemCategory.MISC, new Price(Material.GOLD_INGOT,4), "sponge"),

    // for quick buy
    NO_ITEM(VersionIndependentUtils.getInstance().getColoredItem(DyeColor.RED, ColoredBlockType.GLASS_PANE),ItemCategory.QUICK_BUY);

    private ColoredBlockType coloredBlockType = null;
    private ArmorType armorType = ArmorType.NO_ARMOR;
    private int amount = 0;
    private final ItemCategory category;
    private final Material material;
    private final ItemStack itemStack;
    private final Price price;
    private final String hypixelCounterpart;
    private final ItemStack shopItem;
    private int tier = 0;
    Items(Material material, ColoredBlockType colorMaterial, int amount, ItemCategory category, Price price, String hypixelCounterpart) {
        this.coloredBlockType = colorMaterial;
        this.material = material;
        this.amount = amount;
        this.category = category;
        this.itemStack = new ItemStack(material, amount);
        this.price = price;
        this.hypixelCounterpart = hypixelCounterpart;
        this.shopItem = toShopItem();
    }
    Items(ItemStack item, ItemCategory category) {
        // NO ITEM ENUM
        this.category = category;
        this.material = item.getType();
        this.itemStack = item;
        this.price = null;
        this.hypixelCounterpart = null;
        ItemStack shopItem = toShopItem();
        ItemMeta meta = shopItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "No item");
        shopItem.setItemMeta(meta);
        this.shopItem = shopItem;

    }
    Items(VersionIndependentMaterial material, int amount, ItemCategory category, Price price, String hypixelCounterpart) {
        this(material.toBukkitVersion(),amount,category, price, hypixelCounterpart);
    }
    Items(Material material, int amount, ItemCategory category, Price price, String hypixelCounterpart) {
        this.material = material;
        this.amount = amount;
        this.category = category;
        this.itemStack = new ItemStack(material, amount);
        this.price = price;
        this.hypixelCounterpart = hypixelCounterpart;
        this.shopItem = toShopItem();

    }
    Items(ItemStack itemStack, ItemCategory category, Price price, String hypixelCounterpart) {
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.category = category;
        this.itemStack = itemStack;
        this.price = price;
        this.hypixelCounterpart = hypixelCounterpart;
        this.shopItem = toShopItem();

    }
    Items(ArmorType type, ItemCategory category, Price price, String hypixelCounterpart) {
        this.armorType = type;
        this.category = category;
        this.price = price;
        this.hypixelCounterpart = hypixelCounterpart;
        this.material = Material.valueOf(getArmorType() + "_BOOTS");
        this.itemStack = new ItemStack(material, 1);
        this.shopItem = toShopItem();
    }
    Items(VersionIndependentMaterial material, int amount, ItemCategory category, Price price, String hypixelCounterpart, int tier) {
        this(material.toBukkitVersion(),amount,category, price, hypixelCounterpart, tier);
    }
    Items(Material material, int amount, ItemCategory category, Price price, String hypixelCounterpart, int tier) {
        this.material = material;
        this.amount = amount;
        this.category = category;
        this.itemStack = new ItemStack(material, amount);
        this.price = price;
        this.hypixelCounterpart = hypixelCounterpart;
        this.tier = tier;
        this.shopItem = toShopItem();
    }
    Items(ItemStack itemStack, ItemCategory category, Price price, String hypixelCounterpart, int tier) {
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.category = category;
        this.itemStack = itemStack;
        this.price = price;
        this.hypixelCounterpart = hypixelCounterpart;
        this.tier = tier;
        this.shopItem = toShopItem();

    }

    @Nullable
    public static Items toItemEnum(ItemStack item) {

        for (Items items: Items.values()) {
            if (item.equals(items.asShopItem())) {
                return items;
            }
        }
        return null;
    }
    @Nullable
    public ColoredBlockType getColoredBlockType() {
        return coloredBlockType;
    }

    public ArmorType getArmorType() {
        return armorType;
    }

    public int getAmount() {
        return amount;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Price getPrice() {
        return price;
    }

    public String getHypixelCounterpart() {
        return hypixelCounterpart;
    }

    public int getTier() {
        return tier;
    }
    public boolean isTiered() {
        return tier != 0;
    }

    public ItemStack asShopItem() {
        return shopItem;
    }
    private ItemStack toShopItem() {
            ItemStack item = getItemStack();
        ItemMeta meta = item.getItemMeta();
            if (getCategory() == ItemCategory.POTIONS) {
                PotionMeta potmeta = (PotionMeta) item.getItemMeta();
                potmeta.setMainEffect(potmeta.getCustomEffects().get(0).getType());
                potmeta.setDisplayName(potionTypeToName(potmeta.getCustomEffects().get(0)));
                item.setItemMeta(meta);
            }
            if (getPrice() != null) {
                meta.setLore(addLore(getPrice()));
            }
            item.setItemMeta(meta);
            return item;
    }
    private List<String> addLore(Price price) {
        ChatColor color = null;
        if (price.getMaterial() == Material.EMERALD) {
            color = ChatColor.GREEN;
        } else if (price.getMaterial() == Material.IRON_INGOT) {
            color = ChatColor.GRAY;
        } else if (price.getMaterial() == Material.GOLD_INGOT) {
            color = ChatColor.GOLD;
        }
        String stringified = price.getMaterial().name().toLowerCase(Locale.ROOT).replace('_', ' ');
        if (price.getPrice() >= 2) {
            stringified += "s";
        }
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.BOLD + "" +color + "" + price.getPrice() + " " + stringified);
        return lore;
    }
    private String convertToSeconds(int duration) {
        return " (" + duration / 20 + " Seconds)";
    }
    private String potionTypeToName(PotionEffect effect) {
        String roman = "";
        if (effect.getAmplifier() >= 1) {
            roman = intToRoman(effect.getAmplifier() + 1);
        }
        if (effect.getType() == PotionEffectType.INVISIBILITY) {
            return "Invisibility" + roman + " Potion" + convertToSeconds(effect.getDuration());
        } else if (effect.getType() == PotionEffectType.JUMP) {
            return "Jump Boost" + roman + " Potion" + convertToSeconds(effect.getDuration());
        } else if (effect.getType() == PotionEffectType.SPEED) {
            return "Spee " + roman + " Potion" + convertToSeconds(effect.getDuration());
        } else {
            return ChatColor.RED + "POTION EFFECT COULD NOT BE FOUND";
        }
    }
    public String intToRoman(int num) {
        //System.out.println("Integer: " + num);
        int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] romanLetters = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        StringBuilder roman = new StringBuilder();
        for(int i=0;i<values.length;i++)
        {
            while(num >= values[i])
            {
                num = num - values[i];
                roman.append(romanLetters[i]);
            }
        }
        return " " + roman;
    }
}

