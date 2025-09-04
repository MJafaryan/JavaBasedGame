package models.buildings;

import org.json.simple.JSONObject;
import com.badlogic.gdx.math.Vector2;
import datastructures.SimplerJson;
import models.persons.Worker;
import models.user.Colony;
import models.Basics;

public class Mine extends Building implements Runnable, Upgradable {
    private String mineName;
    private Worker worker;
    private String outputMaterial;
    private int outputAmount;
    private int level;
    private boolean isAlive;

    public Mine(Colony colony, Vector2 location, int height, int width, String mineName) throws Exception {
        super(colony, location, height, width, mineName);
        JSONObject buildingInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, String.format("farms_%s", mineName));
        payCost((JSONObject) SimplerJson.getDataFromJson(buildingInfo, "lvl1_cost"));
        this.mineName = mineName;
        this.health = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "health");
        this.outputMaterial = (String) SimplerJson.getDataFromJson(buildingInfo, "outputMaterial");
        this.outputAmount = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "lvl1_output");
        this.level = 1;
        this.isAlive = true;
    }

    @Override
    public void upgrade() throws Exception {
        if (this.level < 3) {
            JSONObject newLevelInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, String.format("farms_%s_lvl%s", this.mineName, (this.level + 1)));
            payCost((JSONObject) SimplerJson.getDataFromJson(newLevelInfo, "cost"));
            this.outputAmount = (int) (long) SimplerJson.getDataFromJson(newLevelInfo, "output");
            this.level++;
        }
        else {
            throw new Exception("This level already is in maximum level of itself.");
        }
    }

    public Worker getWorker() {
        return this.worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public int getLevel() {
        return this.level;
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void destroy() {
        deleteFromBuildings();
        this.worker.setWorkSpace(null);
        this.worker =  null;
        this.isAlive = false;
    }

    @Override
    public void run() {
        while (this.isAlive) {
            try {
                if (this.worker != null) {
                    colony.updateRecourse(this.outputMaterial, this.outputAmount);
                } else {
                    House house = colony.getEmptyHouses().getNode(0);
                    this.worker = new Worker(colony, this, house);

                }
                Thread.sleep(Basics.BASE_TIME_PERIOD / colony.getTimeConfidence());
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
