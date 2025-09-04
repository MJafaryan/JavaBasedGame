package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;
import models.user.User;

public class IronMine extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;

    static {
        JSONObject farmsConfig = (JSONObject) SimplerJson.getDataFromJson(configFile, "farms");
        config = (JSONObject) farmsConfig.get("ironMine");
    }
    public IronMine(Texture texture, int x, int y, int width, int height, String ironMine, Colony colony) throws Exception {
        super(texture , x, y, width, height, ironMine, colony);
        this.maxLevel = 3;


        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.lvl = 1;
        int income = (int) (long) SimplerJson.getDataFromJson(config, "lvl1_output");
        this.colony.setIncome("iron", colony.getIncomes().get("iron") + income); // باید iron باشد
    }

    public void upgrade() throws Exception {
        if (lvl >= maxLevel) {
            throw new Exception("Iron Mine is already at maximum level");
        }

        // ابتدا درآمد سطح فعلی را کم کنید
        JSONObject currentLevelConfig = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + this.lvl);
        int currentIncome = (int) (long) SimplerJson.getDataFromJson(currentLevelConfig, "output");
        this.colony.setIncome("iron", colony.getIncomes().get("iron") - currentIncome);

        // سپس سطح را افزایش دهید و درآمد جدید را اضافه کنید
        this.lvl++;

        JSONObject newLevelConfig = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + this.lvl);
        int newIncome = (int) (long) SimplerJson.getDataFromJson(newLevelConfig, "output");
        this.colony.setIncome("iron", colony.getIncomes().get("iron") + newIncome); // ✅ باید iron باشد
    }
    public int getLevel() { return lvl; }
    public int getMaxLevel() { return maxLevel; }
}
