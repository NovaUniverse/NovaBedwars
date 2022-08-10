package net.novauniverse.bedwars.game.object;

import org.bukkit.Material;

public class Price {
    private final Material material;
    private final int price;
    public Price(Material material, int price) {
        this.material = material;
        this.price = price;
    }

    public Material getMaterial() {
        return material;
    }

    public int getPrice() {
        return price;
    }
}
