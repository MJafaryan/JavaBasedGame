package models.buildings;

import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.Basics;
import models.user.Colony;

public class TownHall extends Building {

    public TownHall(Colony colony, Vector2 location, int height, int width) throws Exception {
        super(colony, location, height, width, "townHall", 5);
        JSONObject buildingInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, "townHall");
        this.health = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "lvl1_health");
        this.colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(buildingInfo, "lvl1_capacity"));
        this.colony.addImportantBuilding("townHall", this);

        try {
            colony.updateRecourse("wood", 100);
            colony.updateRecourse("stone", 30);
            colony.updateRecourse("iron", 20);
            colony.updateRecourse("food", 50);
            colony.updateRecourse("coin", 500);
        } catch (Exception e) {
        }
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (this.level < getMaxLevel()) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(configFile, "townHall_lvl" + (this.level + 1));
        }

        payCost((JSONObject) SimplerJson.getDataFromJson(newlvl, "upgradeCost"));

        // Set Changes:
        this.level++;
        this.colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(newlvl, "capacity"));
        this.health = (int) (long) SimplerJson.getDataFromJson(newlvl, "health");
    }

    public void destroy() {
        this.colony.defeat();
    }
}
