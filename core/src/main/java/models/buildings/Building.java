package models.buildings;

import models.Basics;
import models.user.Colony;
import org.json.simple.JSONObject;
import datastructures.HashMap;
import datastructures.SimplerJson;
import java.util.UUID;

public abstract class Building {
    protected UUID id;
    protected int health;
    protected Colony colony;
    protected static JSONObject configFile;

    static {
        configFile = SimplerJson.readJson(String.format("%sconfigs/building-config.json",
                Basics.DATA_DIR));
    }

    public Building(int health, Colony colony) { // TODO: deploy map logic
        this(colony);
        this.health = health;
    }

    public Building(Colony colony) {
        this.id = UUID.randomUUID();
        this.colony = colony;
        this.colony.addBuilding(this);
    }

    public int getHealth() {
        return health;
    }

    public Colony getColony() {
        return colony;
    }

    public UUID getID() {
        return this.id;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public void payCost(JSONObject costsJSON) throws Exception {
        HashMap<Integer> requiredMaterials = new HashMap<>();

        for (String material : Basics.MATERIALS_NAME) {
            if (SimplerJson.getDataFromJson(costsJSON, material) != null) {
                requiredMaterials.put(material,
                        (int) (long) SimplerJson.getDataFromJson(costsJSON, material));
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null
                    && requiredMaterials.get(material) > this.colony.getMaterial(material)) {
                throw new Exception("No enough " + material);
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null) {
                this.colony.updateResourceAmount(material, requiredMaterials.get(material) * -1);
            }
        }
    }
}
