package net.novauniverse.bedwars.game;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.config.BedwarsConfig;
import net.novauniverse.bedwars.game.config.ConfiguredBaseData;
import net.novauniverse.bedwars.game.object.base.BaseData;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Bedwars extends MapGame {

    public static int WEAPON_SLOT_DEFAULT = 0;
    public static int TRACKER_SLOT_DEFAULT = 8;

    private boolean started;
    private boolean ended;

    private BedwarsConfig config;
    private int timeToUpgrade;
    private Task timer;
    public List<BaseData> bases;

    public Bedwars() {
        super(NovaBedwars.getInstance());
        timeToUpgrade = 0;
        bases = new ArrayList<>();
    }

    public BedwarsConfig getConfig() {
        return config;
    }
    @Override
    public String getName() {
        return "mcf_bedwars";
    }

    @Override
    public String getDisplayName() {
        return "Bedwars";
    }

    @Override
    public PlayerQuitEliminationAction getPlayerQuitEliminationAction() {
        return PlayerQuitEliminationAction.DELAYED;
    }

    @Override
    public boolean eliminatePlayerOnDeath(Player player) {
        return false;
    }

    @Override
    public boolean isPVPEnabled() {
        return true;
    }

    @Override
    public boolean autoEndGame() {
        return false;
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public boolean hasEnded() {
        return ended;
    }

    @Override
    public boolean isFriendlyFireAllowed() {
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity attacker, LivingEntity target) {
        return false;
    }

    @Override
    public void onStart() {
        if (started)
            return;
        BedwarsConfig config =(BedwarsConfig) getActiveMap().getMapData().getMapModule(BedwarsConfig.class);
        if (config == null) {
            Log.fatal("NovaBedwars", "Map " + this.getActiveMap().getMapData().getMapName() + " has no config");
            Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Bedwars has gone into an error and has to be stopped");
            endGame(GameEndReason.ERROR);
            return;
        }
        this.config = config;

        List<ConfiguredBaseData> cfgBase =new ArrayList<>(config.getBases());
        TeamManager.getTeamManager().getTeams().forEach(team -> {
            if (cfgBase.size() > 0) {
                ConfiguredBaseData b = cfgBase.remove(0);
                BaseData base = b.toBaseData(getWorld(), team);
                bases.add(base);
            }
        });

    }

    @Override
    public void onEnd(GameEndReason reason) {
        if (ended) {
            return;
        }

    }
}
