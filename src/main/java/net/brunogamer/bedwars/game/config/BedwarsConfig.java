package net.brunogamer.bedwars.game.config;

import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodule.MapModule;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BedwarsConfig extends MapModule {

    private int upgradeTime;
    private int generatorSpeed;
    public BedwarsConfig(JSONObject json) {
        super(json);

        this.upgradeTime = json.getInt("upgrade_time");
        this.generatorSpeed = json.getInt("generator_speed");
    }
    public int getUpgradeTime() {
        return upgradeTime;
    }
    public int getGeneratorSpeed() {
        return generatorSpeed;
    }
}
