package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;

public class GoldMine extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;

    static {
        // اصلاح کلید config - باید farms.goldMine باشد
        JSONObject farmsConfig = (JSONObject) SimplerJson.getDataFromJson(configFile, "farms");
        config = (JSONObject) farmsConfig.get("goldMine");
    }

    public GoldMine(Texture texture, int x, int y, int width, int height, String type, Colony colony) throws Exception {
        super(texture, x, y, width, height, type, colony);
        this.maxLevel = 3;

        this.health = ((Long) config.get("health")).intValue();
        this.lvl = 1;

        JSONObject lvl1Config = (JSONObject) config.get("lvl1");
        int income = ((Long) lvl1Config.get("output")).intValue();
        this.colony.setIncome("coin", colony.getIncomes().get("coin") + income); // باید coin باشد نه food!
    }

    public void upgrade() throws Exception {
        if (lvl >= maxLevel) {
            throw new Exception("Gold Mine is already at maximum level");
        }

        String levelKey = "lvl" + (this.lvl + 1);
        JSONObject newLevelConfig = (JSONObject) config.get(levelKey);

        if (newLevelConfig == null) {
            throw new Exception("Configuration not found for level " + (this.lvl + 1));
        }

        // ابتدا درآمد سطح قبلی را کم کنید
        JSONObject currentLevelConfig = (JSONObject) config.get("lvl" + this.lvl);
        int currentIncome = ((Long) currentLevelConfig.get("output")).intValue();
        this.colony.setIncome("coin", colony.getIncomes().get("coin") - currentIncome);

        // سپس درآمد سطح جدید را اضافه کنید
        this.lvl++;
        int newIncome = ((Long) newLevelConfig.get("output")).intValue();
        this.colony.setIncome("coin", colony.getIncomes().get("coin") + newIncome);
    }

    public int getLevel() { return lvl; }
    public int getMaxLevel() { return maxLevel; }
}
