package models.buildings;

import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;

public class House extends Building {
    private int population;

    public House(Colony colony) throws Exception {
        super(colony);
        JSONObject config = (JSONObject) SimplerJson.getDataFromJson(configFile, "house");

        payCost((JSONObject) SimplerJson.getDataFromJson(config, "cost"));

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.colony.setMaximumPossiblePopulation((int) (long) SimplerJson.getDataFromJson(config, "capacity") + this.colony.getMaximumPossiblePopulation());
        this.population = 0;
    }

    public int getPopulation() {
        return population;
    }
}
