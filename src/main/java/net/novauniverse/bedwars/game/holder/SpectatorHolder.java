package net.novauniverse.bedwars.game.holder;

import net.novauniverse.bedwars.utils.BedwarsTextures;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpectatorHolder extends GUIReadOnlyHolder {

    private List<UUID> players;
    private int page;
    private boolean paged;
    private int pagesAmmount;


    private static ItemStack RIGHT = ItemBuilder.getPlayerSkullWithBase64TextureAsBuilder(BedwarsTextures.ARROW_RIGHT).setName(ChatColor.AQUA + "Next page").build();

    private static ItemStack LEFT = ItemBuilder.getPlayerSkullWithBase64TextureAsBuilder(BedwarsTextures.ARROW_LEFT).setName(ChatColor.AQUA + "Previous page").build();
    public SpectatorHolder(List<UUID> players) {
        this.players = players;
        page = 0;
        paged = false;
        if (getInventorySize(players.size()) > 54) {
            paged = true;
            pagesAmmount = (int) Math.floor(players.size() / 45f);
        }


    }

    public static void update(List<UUID> players) {
        for (Player player :Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof SpectatorHolder) {
                SpectatorHolder spectatorHolder = (SpectatorHolder) player.getOpenInventory().getTopInventory().getHolder();
                spectatorHolder.setPlayers(players);
                spectatorHolder.display(player);
                spectatorHolder.setPaged(false);
                if (getInventorySize(players.size()) > 54) {
                    spectatorHolder.setPaged(true);
                    spectatorHolder.setPagesAmmount((int) Math.floor(players.size() / 45f));
                }
                spectatorHolder.display(player);
            }
        }
    }

    public List<UUID> getPlayers() {
        return players;
    }

    private void setPlayers(List<UUID> players) {
        this.players = players;
    }

    public int getPage() {
        return page;
    }


    public void addPage(Player player) {
        page++;
        display(player);
    }

    public void decreasePage(Player player) {
        page--;
        display(player);
    }

    public void setPaged(boolean paged) {
        this.paged = paged;
    }

    public boolean isPaged() {
        return paged;
    }

    public void display(Player player) {
        List<UUID> toSort = new ArrayList<>(this.players);

        List<Player> sorted = new ArrayList<>();

        for (UUID id : toSort)  {
            OfflinePlayer op = Bukkit.getOfflinePlayer(id);
            if (op.isOnline()) {
                sorted.add(op.getPlayer());
            }
        }
        sorted.sort((o1, o2) -> {
            Team team1 = TeamManager.getTeamManager().getPlayerTeam(o1);
            Team team2 = TeamManager.getTeamManager().getPlayerTeam(o2);
            int teamNames = (team1.getTeamColor() + team1.getDisplayName()).compareTo(team2.getTeamColor() + team2.getDisplayName());
            if (teamNames == 0) {
                return o1.getName().compareTo(o2.getDisplayName());
            } else {
                return teamNames;
            }
        });

        Inventory inventory = Bukkit.createInventory(this, getPageSize(page, sorted), ChatColor.GREEN + "Select Player to Teleport");
        if (isPaged()) {
            if (page == 0) {
                inventory.setItem(53, RIGHT);
            } else if (page == pagesAmmount) {
                inventory.setItem(45, LEFT);
            } else {
                inventory.setItem(45, LEFT);
                inventory.setItem(53, RIGHT);
            }

            for (int i = 0; i < getPageSize(page, sorted) -9; i++) {
                int start = page * 45;
                int selected = i + start;
                if (selected >= sorted.size()) {
                    break;
                }
                Player selPlayer = sorted.get(selected);

                Team team = TeamManager.getTeamManager().getPlayerTeam(selPlayer);

                ItemStack item = VersionIndependentUtils.get().getPlayerSkullitem();
                SkullMeta sm = (SkullMeta) item.getItemMeta();
                sm.setDisplayName(team.getTeamColor() + team.getDisplayName() + ChatColor.RESET + " " + team.getTeamColor() + selPlayer.getName());
                sm.setOwner(selPlayer.getName());
                item.setItemMeta(sm);
                inventory.setItem(i, item);
            }
        } else {
            for (int i = 0; i < getPageSize(page, sorted) ; i++) {
                if (i >= sorted.size()) {
                    break;
                }
                Player selPlayer = sorted.get(i);

                Team team = TeamManager.getTeamManager().getPlayerTeam(selPlayer);

                ItemStack item = VersionIndependentUtils.get().getPlayerSkullitem();
                SkullMeta sm = (SkullMeta) item.getItemMeta();
                sm.setDisplayName(team.getTeamColor() + "" + ChatColor.BOLD + team.getDisplayName() + ChatColor.RESET + " " + team.getTeamColor() + selPlayer.getName());
                sm.setOwner(selPlayer.getName());
                item.setItemMeta(sm);
                inventory.setItem(i, item);
            }
        }

        player.openInventory(inventory);
        this.addClickCallback(e -> {
            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {
                if (e.getCurrentItem().getItemMeta().equals(RIGHT.getItemMeta())) {
                    addPage(player);
                } else if (e.getCurrentItem().getItemMeta().equals(LEFT.getItemMeta())) {
                    decreasePage(player);
                } else if (e.getCurrentItem().getType() == Material.SKULL_ITEM) {
                    player.closeInventory();
                    Player clicked = Bukkit.getPlayerExact(((SkullMeta) e.getCurrentItem().getItemMeta()).getOwner());
                    player.teleport(clicked);
                } else {
                    return GUIAction.CANCEL_INTERACTION;
                }
            }

            return GUIAction.NONE;
        });

    }

    public int getPageSize(int page, List<Player> players) {
        if (paged && page < getPagesAmmount()) {
            return 54;
        } else if (paged && page == getPagesAmmount()) {
            return getInventorySize((players.size() % 45) + 9);
        } else {
            return getInventorySize(players.size());
        }
    }

    private static int getInventorySize(int ammount) {
        int initial = 9;
        while (ammount <= 0) {
            ammount -= 9;
            if (ammount > 0) {
                initial += 9;
            }
        }
        return initial;
    }
    public int getPagesAmmount() {
        return pagesAmmount;
    }
    private void setPagesAmmount(int pagesAmmount) {
        this.pagesAmmount = pagesAmmount;
    }
}
