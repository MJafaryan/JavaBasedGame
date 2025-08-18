package models.buildings;

import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import org.json.simple.JSONArray;
import models.user.Colony;
import models.user.User;

public class Barracks extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;
    private JSONArray unavailableUnits;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "barracks");
    }

    public Barracks(Colony colony) throws Exception {
        super(colony);

        payCost((JSONObject) SimplerJson.getDataFromJson(config, "lvl1_cost"));

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.lvl = 1;
        this.unavailableUnits = (JSONArray) SimplerJson.getDataFromJson(config, "lvl1_unavailableUnits");
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl == 1) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + (this.lvl + 1));
        }

        payCost((JSONObject) SimplerJson.getDataFromJson(newlvl, "cost"));

        // Set Changes:
        this.lvl = 2;
        this.unavailableUnits = null;
    }

    public JSONArray getUnavailableUnits() {
        return this.unavailableUnits;
    }
}
