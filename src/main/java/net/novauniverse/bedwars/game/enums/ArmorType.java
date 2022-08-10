package net.novauniverse.bedwars.game.enums;

public enum ArmorType {
    GOLD("Permanent Gold Armor"),CHAINMAIL("Permanent Chainmail Armor"),IRON("Permanent Iron Armor"),DIAMOND("Permanent Diamond Armor"),NO_ARMOR;

    private final String shopName;
    ArmorType(String shopName) {
        this.shopName = shopName;
    }
    ArmorType() {
        this.shopName = "hehe balls :)";
    }

    public String getShopName() {
        return shopName;
    }
}
