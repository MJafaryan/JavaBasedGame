package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;
import models.user.User;

public class StoneMine extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;

    static {
        JSONObject farmsConfig = (JSONObject) SimplerJson.getDataFromJson(configFile, "farms");
        config = (JSONObject) farmsConfig.get("stoneMine");
    }

    public StoneMine(Texture texture, int x, int y, int width, int height, String stoneMine, Colony colony) throws Exception {
        super(texture , x, y, width, height, stoneMine, colony);
        this.maxLevel = 3;


        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.lvl = 1;
        int income = (int) (long) SimplerJson.getDataFromJson(config, "lvl1_output");
        this.colony.setIncome("stone", colony.getIncomes().get("stone") + income); // باید stone باشد
    }

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
    public int getLevel() { return lvl; }
    public int getMaxLevel() { return maxLevel; }
}
