package models.buildings;

import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONObject;
import datastructures.HashMap;
import datastructures.SimplerJson;
import models.user.Colony;
import java.util.Random;

public class Hospital extends Building {
    private int findCureCost;
    private float findingCureChance;
    private int healHeroCost;

    public Hospital(Colony colony, Vector2 location, int height, int width) throws Exception {
        super(colony, location, height, width, "hospital",1);

        JSONObject buildingInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, "hospital");
        payCost((JSONObject) SimplerJson.getDataFromJson(buildingInfo, "cost"));

        this.health = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "health");
        this.healHeroCost = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "findingCureForDiseaseCost");
        this.findingCureChance = (float) (double) SimplerJson.getDataFromJson(buildingInfo, "findingCureForDiseaseChance");
        this.healHeroCost = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "heroTreatmentCost");

        colony.addImportantBuilding("hospital", this);
    }

    public boolean findCureForDisease() throws Exception {
        this.colony.updateRecourse("coin", this.findCureCost);
        Random random = new Random();
        return random.nextDouble() <= this.findingCureChance;
    }

        public void healHero() throws Exception {
        this.colony.updateRecourse("coin", this.healHeroCost);
        // TODO: Other parts of healing process
    }

    @Override
    public void destroy() {
        HashMap<Building> importantBuildings = this.colony.getImportantBuildings();
        HashMap<Building> buildings = this.colony.getBuildings();
        importantBuildings.delete("hospital");
        buildings.delete(getID().toString());
        this.colony.setBuildings(buildings);
        this.colony.setImportantBuildings(importantBuildings);
    }
}
