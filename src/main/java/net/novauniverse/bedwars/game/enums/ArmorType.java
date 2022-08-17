package net.novauniverse.bedwars.game.enums;

public enum ArmorType {
    GOLD("Permanent Gold Armor", 1),CHAINMAIL("Permanent Chainmail Armor", 2),IRON("Permanent Iron Armor", 3),
    DIAMOND("Permanent Diamond Armor", 4),NO_ARMOR;

    private final String shopName;
    private final int tier;
    ArmorType(String shopName, int tier) {
        this.shopName = shopName;
        this.tier = tier;
    }
    ArmorType() {
        this.shopName = "hehe balls :)";
        this.tier = 0;
    }

    public int getTier() {
        return tier;
    }

    public String getShopName() {
        return shopName;
    }
}
