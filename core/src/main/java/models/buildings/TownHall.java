package models.buildings;

import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;

public class TownHall extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "townHall");
    }

    public TownHall(Colony colony) {
        super(colony);
        this.health = (int) (long) SimplerJson.getDataFromJson(config, "lvl1_health");
        this.lvl = 1;
        colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(config, "lvl1_capacity"));
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl < 5) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + (this.lvl + 1));
        }

        payCost((JSONObject) SimplerJson.getDataFromJson(config, "upgradeCost"));

        // Set Changes:
        this.lvl++;
        this.colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(newlvl, "capacity"));
        this.health = (int) (long) SimplerJson.getDataFromJson(newlvl, "health");
    }
}
