package models.buildings;

import org.json.simple.JSONObject;

import com.badlogic.gdx.math.Vector2;

import datastructures.HashMap;
import datastructures.SimplerJson;
import org.json.simple.JSONArray;
import models.user.Colony;

public class Barracks extends Building implements Upgradable {
    private int lvl;
    private JSONArray unavailableUnits;

    public Barracks(Colony colony, Vector2 location, int height, int width) throws Exception {
        super(colony, location, height, width, "barracks");

        JSONObject buildingInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, "barracks");
        payCost((JSONObject) SimplerJson.getDataFromJson(buildingInfo, "lvl1_cost"));

        this.health = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "health");
        this.lvl = 1;
        this.unavailableUnits = (JSONArray) SimplerJson.getDataFromJson(buildingInfo, "lvl1_unavailableUnits");
    }

    @Override
    public void upgrade() throws Exception {
        if (lvl == 1) {
            JSONObject buildingInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, "barracks");
            payCost((JSONObject) SimplerJson.getDataFromJson(buildingInfo, "lvl2_cost"));

            this.lvl++;
            this.unavailableUnits = null;
        }
    }

    public JSONArray getUnavailableUnits() {
        return unavailableUnits;
    }

    public int getLevel() {
        return this.lvl;
    }

    public int getMaxLevel() {
        return 2;
    }

    public void destroy() {
        HashMap<Building> importantBuildings = this.colony.getImportantBuildings();
        HashMap<Building> buildings = this.colony.getBuildings();
        importantBuildings.delete("barracks");
        buildings.delete(getID().toString());
        this.colony.setBuildings(buildings);
        this.colony.setImportantBuildings(importantBuildings);
    }
}
