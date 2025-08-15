package models.buildings;

import datastructures.HashMap;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.Basics;
import models.user.Colony;
import java.util.Random;

public class Hospital extends Building {
    private static JSONObject config;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "hospital");
    }

    public Hospital(Colony colony) throws Exception {
        super(colony);
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
    }

    public boolean findCureForDisease() throws Exception {
        this.colony.updateResourceAmount("coin",
                (int) (long) SimplerJson.getDataFromJson(config, "findingCureForDiseaseCost"));
        Random random = new Random();
        return random.nextDouble() <= (double) SimplerJson.getDataFromJson(config, "findingCureForDiseaseChance");
    }

    public void healHero() throws Exception {
        this.colony.updateResourceAmount("coin", (int) (long) SimplerJson.getDataFromJson(config, "heroTreatmentCost"));
        // TODO: Other parts of healing process
    }
}
