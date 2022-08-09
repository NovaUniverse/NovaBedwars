package net.brunogamer.bedwars.game.events;

import net.brunogamer.bedwars.game.enums.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemBuyEvent extends Event implements Cancellable {
    private final HandlerList HANDLERS_LIST = new HandlerList();
    private Result buy = Result.DEFAULT;

    private final Items item;
    private final Player player;
    private final int slot;

    public ItemBuyEvent(Items item, Player player, int slot) {
        this.item = item;
        this.player = player;
        this.slot = slot;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    public Player getPlayer() {
        return player;
    }
    public int getSlot() {
        return slot;
    }
    public Items getItem() {
        return item;
    }

    @Override
    public boolean isCancelled() {
        return this.buy() == Result.DENY;
    }
    public Result buy() {
        return buy;
    }
    public void setBuy(Result buy) {
        this.buy = buy;
    }

    @Override
    public void setCancelled(boolean b) {
        this.setBuy(b ? Result.DENY : (this.buy() == Result.DENY ? Result.DEFAULT : this.buy()));
    }
}
