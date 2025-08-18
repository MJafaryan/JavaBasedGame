package models.buildings;

import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;
import models.user.User;

public class StoneMine extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "farms_stoneMine");
    }

    public StoneMine(Colony colony) throws Exception {
        super(colony);

        payCost((JSONObject) SimplerJson.getDataFromJson(config, "lvl1_cost"));

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.lvl = 1;
        int income = (int) (long) SimplerJson.getDataFromJson(config, "lvl1_output");
        this.colony.setIncome("food", colony.getIncomes().get("food") + income);
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl < 3) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + (this.lvl + 1));
        }

        payCost((JSONObject) SimplerJson.getDataFromJson(newlvl, "cost"));

        // Set Changes:
        this.lvl++;
        this.colony.setIncome("food",
                colony.getIncomes().get("food") + (int) (long) SimplerJson.getDataFromJson(newlvl, "output"));
    }
}
