package models.buildings;

import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;

public class TownHall extends Building implements Upgradable {
    private int lvl;

    public TownHall(Colony colony, Vector2 location, int height, int width) throws Exception {
        super(colony, location, height, width, "townHall");
        JSONObject buildingInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, "townHall");
        this.health = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "lvl1_health");
        this.colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(buildingInfo, "lvl1_capacity"));
        this.colony.addImportantBuilding("townHall", this);
    }

    public int getLevel() {
        return this.lvl;
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl < 5) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(configFile, "townHall_lvl" + (this.lvl + 1));
        }

        payCost((JSONObject) SimplerJson.getDataFromJson(newlvl, "upgradeCost"));

        // Set Changes:
        this.lvl++;
        this.colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(newlvl, "capacity"));
        this.health = (int) (long) SimplerJson.getDataFromJson(newlvl, "health");
    }

    public void destroy() {
        this.colony.defeat();
    }
}
