package models.buildings;

import datastructures.HashMap;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.Basics;
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

        HashMap<Integer> requiredMaterials = new HashMap<>();

        for (String material : Basics.MATERIALS_NAME) {
            if (SimplerJson.getDataFromJson(newlvl, "upgradeCost_" + material) != null) {
                requiredMaterials.put(material,
                        (int) (long) SimplerJson.getDataFromJson(newlvl, "upgradeCost_" + material));
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null
                    && requiredMaterials.get(material) > colony.getMaterial(material)) {
                throw new Exception("No enough " + material);
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null) {
                this.colony.updateResourceAmount(material, requiredMaterials.get(material) * -1);
            }
        }

        // Set Changes:
        this.lvl++;
        this.colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(newlvl, "capacity"));
        this.health = (int) (long) SimplerJson.getDataFromJson(newlvl, "health");
    }
}
