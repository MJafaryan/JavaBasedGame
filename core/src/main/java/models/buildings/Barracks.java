package models.buildings;

import datastructures.HashMap;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import org.json.simple.JSONArray;
import models.Basics;
import models.user.Colony;

public class Barracks extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;
    private JSONArray unavailableUnits;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "barracks");
    }

    public Barracks(Colony colony) throws Exception {
        super(colony);
        HashMap<Integer> requiredMaterials = new HashMap<>();

        for (String material : Basics.MATERIALS_NAME) {
            if (SimplerJson.getDataFromJson(config, "lvl1_cost_" + material) != null) {
                requiredMaterials.put(material,
                        (int) (long) SimplerJson.getDataFromJson(config, "lvl1_cost_" + material));
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null
                    && requiredMaterials.get(material) > this.colony.getMaterial(material)) {
                System.out.println(material + ": " + this.colony.getMaterial(material));
                throw new Exception("No enough " + material);
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null) {
                this.colony.updateResourceAmount(material, requiredMaterials.get(material) * -1);
            }
        }

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.lvl = 1;
        this.unavailableUnits = (JSONArray) SimplerJson.getDataFromJson(config, "lvl1_unavailableUnits");
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl == 1) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + (this.lvl + 1));
        }

        HashMap<Integer> requiredMaterials = new HashMap<>();

        for (String material : Basics.MATERIALS_NAME) {
            if (SimplerJson.getDataFromJson(newlvl, "cost_" + material) != null) {
                requiredMaterials.put(material,
                        (int) (long) SimplerJson.getDataFromJson(newlvl, "cost_" + material));
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
        this.lvl = 2;
        this.unavailableUnits = null;
    }

    public JSONArray getUnavailableUnits() {
        return this.unavailableUnits;
    }
}
