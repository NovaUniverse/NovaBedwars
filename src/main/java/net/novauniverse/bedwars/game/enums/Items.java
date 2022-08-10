package net.novauniverse.bedwars.game.enums;

import net.novauniverse.bedwars.game.object.Price;
import net.novauniverse.bedwars.utils.PotionItemBuilder;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Locale;

public enum Items {
    WOOL(Material.WOOL,ColoredBlockType.WOOL,16, ItemCategory.BLOCK, new Price(Material.IRON_INGOT, 4)),CLAY(Material.STAINED_CLAY,ColoredBlockType.CLAY, 16, ItemCategory.BLOCK, new Price(Material.IRON_INGOT, 12)),
    ENDSTONE(VersionIndependentMaterial.END_STONE, 12, ItemCategory.BLOCK, new Price(Material.IRON_INGOT, 24)), LADDER(Material.LADDER, 6, ItemCategory.BLOCK, new Price(Material.IRON_INGOT, 4)),
    WOOD(Material.WOOD, 16, ItemCategory.BLOCK, new Price(Material.GOLD_INGOT, 4)),OBSIDIAN(Material.OBSIDIAN, 4, ItemCategory.BLOCK, new Price(Material.EMERALD, 4)),
    STONE_SWORD(Material.STONE_SWORD, 1, ItemCategory.COMBAT, new Price(Material.IRON_INGOT, 10)), IRON_SWORD(Material.IRON_SWORD, 1, ItemCategory.COMBAT, new Price(Material.GOLD_INGOT, 7)),
    DIAMOND_SWORD(Material.DIAMOND_SWORD, 1, ItemCategory.COMBAT, new Price(Material.EMERALD, 4)),
    KB_STICK(new ItemBuilder(Material.STICK).addEnchant(Enchantment.KNOCKBACK, 1).build(), ItemCategory.COMBAT, new Price(Material.GOLD_INGOT, 5)),
    GOLD_ARMOR(ArmorType.GOLD, ItemCategory.COMBAT, new Price(Material.IRON_INGOT, 10)), CHAINMAIL_ARMOR(ArmorType.CHAINMAIL, ItemCategory.COMBAT, new Price(Material.IRON_INGOT, 40)),
    IRON_ARMOR(ArmorType.IRON, ItemCategory.COMBAT, new Price(Material.GOLD_INGOT, 12)), DIAMOND_ARMOR(ArmorType.DIAMOND, ItemCategory.COMBAT, new Price(Material.EMERALD, 6)),
    SHEARS(Material.SHEARS, 1, ItemCategory.TOOLS, new Price(Material.IRON_INGOT,20)), WOOD_PICKAXE(VersionIndependentMaterial.WOODEN_PICKAXE, 1, ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 10)),
    STONE_PICKAXE(Material.STONE_PICKAXE, 1, ItemCategory.TOOLS, new Price(Material.IRON_INGOT,10)), IRON_PICKAXE(Material.IRON_PICKAXE, 1, ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 20)),
    GOLD_PICKAXE(VersionIndependentMaterial.GOLDEN_PICKAXE, 1, ItemCategory.TOOLS, new Price(Material.GOLD_INGOT, 3)), DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, 1, ItemCategory.TOOLS, new Price(Material.GOLD_INGOT, 6)),
    WOOD_AXE(VersionIndependentMaterial.WOODEN_AXE, 1, ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 10)), STONE_AXE(Material.STONE_AXE, 1, ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 10)),
    IRON_AXE(Material.IRON_AXE, 1, ItemCategory.TOOLS, new Price(Material.IRON_INGOT, 20)), GOLD_AXE(Material.GOLD_AXE, 1, ItemCategory.TOOLS, new Price(Material.GOLD_INGOT, 3)),
    DIAMOND_AXE(Material.DIAMOND_AXE, 1, ItemCategory.TOOLS, new Price(Material.GOLD_INGOT, 6)), ARROW(Material.ARROW, 8, ItemCategory.COMBAT, new Price(Material.GOLD_INGOT,2)),
    BOW(Material.BOW, 1, ItemCategory.COMBAT, new Price(Material.GOLD_INGOT,12)),
    BOW_POWER_1(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1).build(),ItemCategory.COMBAT, new Price(Material.GOLD_INGOT, 24)),
    BOW_PUNCH_1_POW_1(new ItemBuilder(Material.BOW).addEnchant(Enchantment.DAMAGE_ALL, 1).addEnchant(Enchantment.ARROW_KNOCKBACK, 1).build(), ItemCategory.COMBAT, new Price(Material.EMERALD,6)),
    INVISIBLE(new PotionItemBuilder(Material.POTION).setPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,30 * 20,0, false, false)).build(), ItemCategory.POTIONS, new Price(Material.EMERALD,2)),
    JUMP_BOOST(new PotionItemBuilder(Material.POTION).setPotionEffect(new PotionEffect(PotionEffectType.JUMP,45 * 20,4, false, false)).build(), ItemCategory.POTIONS, new Price(Material.EMERALD,1)),
    SPEED(new PotionItemBuilder(Material.POTION).setPotionEffect(new PotionEffect(PotionEffectType.SPEED,45 * 20,1, false, false)).build(), ItemCategory.POTIONS,new Price(Material.EMERALD, 1)),
    GOLDEN_APPLE(Material.GOLDEN_APPLE, 1, ItemCategory.MISC, new Price(Material.GOLD_INGOT,3)),FIREBALL(Material.FIREBALL, 1, ItemCategory.MISC, new Price(Material.IRON_INGOT, 40)),
    TNT(Material.TNT, 1, ItemCategory.MISC, new Price(Material.GOLD_INGOT,6)),ENDER_PEARL(Material.ENDER_PEARL, 1, ItemCategory.MISC, new Price(Material.EMERALD, 4)),
    WATER_BUCKET(Material.WATER_BUCKET, 1, ItemCategory.MISC, new Price(Material.GOLD_INGOT, 3)),SPONGE(Material.SPONGE, 2, ItemCategory.MISC, new Price(Material.GOLD_INGOT,4));

    private ColoredBlockType coloredBlockType = null;
    private ArmorType armorType = ArmorType.NO_ARMOR;
    private int amount = 0;
    private final ItemCategory category;
    private Material material = null;
    private ItemStack itemStack;
    private Price price;
    Items(Material material, ColoredBlockType colorMaterial, int amount, ItemCategory category, Price price) {
        this.coloredBlockType = colorMaterial;
        this.material = material;
        this.amount = amount;
        this.category = category;
        this.itemStack = new ItemStack(material, amount);
        this.price = price;
    }
    Items(VersionIndependentMaterial material, int amount, ItemCategory category, Price price) {
        this(material.toBukkitVersion(),amount,category, price);
    }
    Items(Material material, int amount, ItemCategory category, Price price) {
        this.material = material;
        this.amount = amount;
        this.category = category;
        this.itemStack = new ItemStack(material, amount);
        this.price = price;
    }
    Items(ItemStack itemStack, ItemCategory category, Price price) {
        this.material = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.category = category;
        this.itemStack = itemStack;
        this.price = price;
    }
    Items(ArmorType type, ItemCategory category, Price price) {
        this.armorType = type;
        this.category = category;
        this.price = price;
    }
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

    public ItemStack toShopItem() {
        if (getArmorType() == ArmorType.NO_ARMOR) {
            ItemStack item = getItemStack();
            if (getCategory() == ItemCategory.POTIONS) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                meta.setDisplayName(potionTypeToName(meta.getCustomEffects().get(0)));
                item.setItemMeta(meta);
            }
            ItemMeta meta = item.getItemMeta();
            meta.setLore(addLore(getPrice()));
            item.setItemMeta(meta);
            return item;
        } else {
            Material material = Material.valueOf(getArmorType() + "_BOOTS");
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(getArmorType().getShopName());
            meta.setLore(addLore(getPrice()));
            item.setItemMeta(meta);
            return item;
        }
    }
    private ArrayList<String> addLore(Price price) {
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
        ArrayList<String> lore = new ArrayList<>();
        lore.add(color + "" + price.getPrice() + " " + stringified);
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
            return "Invisibility " + roman + " Potion" + convertToSeconds(effect.getDuration());
        } else if (effect.getType() == PotionEffectType.JUMP) {
            return "Jump Boost " + roman + " Potion " + convertToSeconds(effect.getDuration());
        } else if (effect.getType() == PotionEffectType.SPEED) {
            return "Speed " + roman + " Potion " + convertToSeconds(effect.getDuration());
        } else {
            return ChatColor.RED + "POTION EFFECT COULD NOT BE FOUND";
        }
    }
    public String intToRoman(int num)
    {
        System.out.println("Integer: " + num);
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
        return roman.toString();
    }
}

