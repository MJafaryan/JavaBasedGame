package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;
import java.util.Random;

public class Hospital extends Building {
    private static JSONObject config;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "hospital");
    }

    public Hospital(Texture texture, int x, int y, int width, int height, String hospital, Colony colony) throws Exception {
        super(texture , x, y, width, height, hospital, colony);

        payCost((JSONObject) SimplerJson.getDataFromJson(config, "cost"));

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
