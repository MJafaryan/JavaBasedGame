package models.buildings;

import org.json.simple.JSONObject;

import datastructures.HashMap;
import datastructures.SimplerJson;
import models.user.Colony;
import models.Basics;

public class House extends Building {
    private int population;

    public House(Colony colony) throws Exception {
        super(colony);
        JSONObject config = (JSONObject) SimplerJson.getDataFromJson(configFile, "house");
        HashMap<Integer> requiredMaterials = new HashMap<>();

        for (String material : Basics.MATERIALS_NAME) {
            if (SimplerJson.getDataFromJson(config, "cost_" + material) != null) {
                requiredMaterials.put(material,
                        (int) (long) SimplerJson.getDataFromJson(config, "cost_" + material));
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
        this.colony.setMaximumPossiblePopulation((int) (long) SimplerJson.getDataFromJson(config, "capacity") + this.colony.getMaximumPossiblePopulation());
        this.population = 0;
    }

    public int getPopulation() {
        return population;
    }
}
