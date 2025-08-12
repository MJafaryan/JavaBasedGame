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
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl < 5) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + (this.lvl + 1));
        }

        HashMap<Integer> requieredMaterials = new HashMap<>();

        for (String material : Basics.MATERIALS_NAME) {
            requieredMaterials.put(material,
                    (int) (long) SimplerJson.getDataFromJson(newlvl, "upgradeCost_" + material));
        }
        for (String material : Basics.MATERIALS_NAME) {
            if (requieredMaterials.get(material) != null
                    && requieredMaterials.get(material) > colony.getResources().get(material)) {
                throw new Exception();
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requieredMaterials.get(material) != null) {
                colony.setResource(material, colony.getResources().get(material) - requieredMaterials.get(material));
            }
        }

        // Set Changes:
        this.lvl++;
        this.health = (int) (long) SimplerJson.getDataFromJson(newlvl, "health");
        this.colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(newlvl, "capacity"));
    }

    // public static void main(String[] args) {
    //     User user = new User("Al", "123");
    //     Colony obj = new Colony("iran", user, "persian", 0, 0);

    //     TownHall town = new TownHall(obj);

    //     obj.setResource("wood", 50);
    //     obj.setResource("stone", 80);
    //     obj.setResource("coin", 50);
    //     // obj.setResource("wood", 50);

    //     try {
    //         town.upgrade();
    //     } catch (Exception e) {
    //     }
    //     System.err.println(obj.getResources().get("wood"));
    //     System.err.println(obj.getStorageCapacity());
    // }
}
