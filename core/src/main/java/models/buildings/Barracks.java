package models.buildings;

import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import org.json.simple.JSONArray;
import models.user.Colony;

public class Barracks extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;
    private String[] unavailableUnits;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "barracks");
    }

    public Barracks(Colony colony) throws Exception {
        super(colony);
        if (colony.getImportantBuildingsCode().get("barracks") != null) {
            throw new Exception();
        }

        payCost((JSONObject) SimplerJson.getDataFromJson(config, "lvl1_cost"));

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.lvl = 1;
        JSONArray unavailableUnitsJsonArray = (JSONArray) SimplerJson.getDataFromJson(config, "lvl1_unavailableUnits");
        this.unavailableUnits = new String[unavailableUnitsJsonArray.size()];
        for (int i = 0; i < unavailableUnitsJsonArray.size(); i++) {
            this.unavailableUnits[i] = (String) unavailableUnitsJsonArray.get(i);
        }
        colony.setImportantBuilding("barracks", this.id.toString());
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

    public String[] getUnavailableUnits() {
        return this.unavailableUnits;
    }
}
