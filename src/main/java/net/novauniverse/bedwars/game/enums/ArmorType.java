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
        this.shopName = "no armor, if you see this report as a bug and how you did it";
        this.tier = 0;
    }

    public boolean tierIsEqual(ArmorType armor) {
        return armor.getTier() == tier;
    }

    public boolean tierIsEqualOrSmallerTo(ArmorType armor) {
        return tier <= armor.getTier();
    }

    public boolean tierIsEqualOrBigger(ArmorType armor) {
        return tier >= armor.getTier();
    }

    public int getTier() {
        return tier;
    }

    public String getShopName() {
        return shopName;
    }
}
