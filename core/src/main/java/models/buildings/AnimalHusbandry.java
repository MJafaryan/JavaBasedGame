package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;

public class AnimalHusbandry extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "farms_animalHusbandry");
    }

    public AnimalHusbandry(Texture texture, int x, int y, int width, int height, String animalHusbandry, Colony colony) throws Exception {
        super(texture , x, y, width, height, animalHusbandry, colony);
        this.maxLevel = 3;
        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.lvl = 1;
        int income = (int) (long) SimplerJson.getDataFromJson(config, "lvl1_output");
        this.colony.setIncome("food", colony.getIncomes().get("food") + income);
    }

    public int getLvl() { return lvl; }

    public int getMaxLevel() { return maxLevel; }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl < 3) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + (this.lvl + 1));
        }
        // Set Changes:
        this.lvl++;
        this.colony.setIncome("food",
                colony.getIncomes().get("food") + (int) (long) SimplerJson.getDataFromJson(newlvl, "output"));
    }
}
