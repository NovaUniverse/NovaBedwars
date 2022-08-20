package net.novauniverse.bedwars.game.object;

public class TieredUpgrade {
    private Price price;
    public TieredUpgrade(Price price) {
        this.price = price;
    }

    public Price getPrice() {
        return price;
    }
}
