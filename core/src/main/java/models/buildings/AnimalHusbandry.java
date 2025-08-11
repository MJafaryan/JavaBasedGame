package models.buildings;

import datastructures.HashMap;

import org.json.simple.JSONObject;

import datastructures.SimplerJson;
import models.Basics;
import models.user.Colony;

public class AnimalHusbandry extends Building implements Upgradable {
    private int lvl;
    JSONObject cost = SimplerJson.readJson(String.format("%sconfigs/building-config.json",
            Basics.DATA_DIR));

    public AnimalHusbandry(Colony colony) {
        super(colony);
        this.health = (int) (long) SimplerJson.getDataFromJson(cost, "farms_animalHusbandry_health");
        this.lvl = 1;
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl < 3) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(this.cost, "farms_animalHusbandry_lvl" + (this.lvl + 1));
        }

        HashMap<Integer> requieredMaterials = new HashMap<>();

        for (String material : Basics.MATERIALS_NAME) {
            requieredMaterials.put(material,
                    (int) (long) SimplerJson.getDataFromJson(newlvl, "cost_" + material));
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
        // به جای این خط پایین باید مقدار خروجی تعیین شود
        // this.colony.setStorageCapacity((int) (long)
        // SimplerJson.getDataFromJson(newlvl, "capacity"));
    }

}
