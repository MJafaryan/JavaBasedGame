package models.buildings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import models.Basics;
import my.game.BuildingConfigManager;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import datastructures.SimplerJson;
import models.user.Colony;

public class Barracks extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;
    private JSONArray unavailableUnits;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "barracks");
    }

    public Barracks(Texture texture, int x, int y, int width, int height, String barracks, Colony colony) throws Exception {
        super(texture, x, y, width, height, barracks, colony);
        this.maxLevel = 2;
        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.lvl = 1;
        this.unavailableUnits = (JSONArray) SimplerJson.getDataFromJson(config, "lvl1_unavailableUnits");
    }

    public void upgrade() throws Exception {
        if (lvl >= maxLevel) {
            throw new Exception("سربازخانه قبلاً به حداکثر سطح ارتقا یافته است.");
        }

        JSONObject newlvl = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + (this.lvl + 1));
        this.lvl = 2;
        this.unavailableUnits = null;
        // اعمال ویژگی‌های جدید سطح ۲
    }

    public void trainUnit(String unitType) throws Exception {
        Gdx.app.log("TrainUnit", "Training unit: " + unitType);
        Colony colony = getColony();

        if (colony == null) {
            throw new Exception("Colony is null");
        }

        Gdx.app.log("TrainUnit", "Colony population: " + colony.getPopulation());
        Gdx.app.log("TrainUnit", "Colony bread type: " + colony.getBread());

        if (colony.getPopulation() <= 0) {
            throw new Exception("جمعیتی برای آموزش نیروی جدید باقی نمانده است.");
        }

        if (unavailableUnits != null && unavailableUnits.contains(unitType.toLowerCase())) {
            throw new Exception("نیروی " + unitType + " در سطح فعلی سربازخانه قابل آموزش نیست.");
        }

        JSONObject unitInfo = null;
        String breadType = colony.getBread();

        if (Colony.config == null) {
            throw new Exception("Colony configuration is not loaded");
        }

        JSONObject military = (JSONObject) Colony.config.get("military");
        if (military == null) {
            throw new Exception("Military section not found in config");
        }

        // برای واحد spy ساختار متفاوت است
        if ("spy".equalsIgnoreCase(unitType)) {
            JSONObject spyConfig = (JSONObject) military.get("spy");
            if (spyConfig != null) {
                unitInfo = (JSONObject) spyConfig.get("price");
            }
        } else {
            // برای سایر واحدها، بر اساس breadType
            JSONObject breadMilitary = (JSONObject) military.get(breadType);
            if (breadMilitary == null) {
                throw new Exception("Bread type not found in military config: " + breadType);
            }

            JSONObject unitConfig = (JSONObject) breadMilitary.get(unitType.toLowerCase());
            if (unitConfig != null) {
                unitInfo = (JSONObject) unitConfig.get("price");
            }
        }

        if (unitInfo == null) {
            throw new Exception("Price information not found for unit: " + unitType);
        }

        Gdx.app.log("TrainUnit", "Unit cost: " + unitInfo.toJSONString());

        // پرداخت هزینه
        if (!payCost(unitInfo)) {
            throw new Exception("منابع کافی برای آموزش نیرو وجود ندارد.");
        }

        // افزایش تعداد نیرو
        if (colony.getMilitaries() == null) {
            throw new Exception("Military map is not initialized");
        }

        Integer currentCount = colony.getMilitaries().get(unitType);
        if (currentCount == null) {
            colony.getMilitaries().put(unitType, 1);
        } else {
            colony.getMilitaries().put(unitType, currentCount + 1);
        }

        // کاهش جمعیت
        colony.setPopulation(colony.getPopulation() - 1);

        Gdx.app.log("TrainUnit", "Unit trained successfully!");
        Gdx.app.log("TrainUnit", "New military count: " + colony.getMilitaries());
        Gdx.app.log("TrainUnit", "New population: " + colony.getPopulation());
    }

    public boolean payCost(JSONObject cost) {
        Colony playerColony = getColony();
        if (playerColony == null || cost == null) return false;

        // بررسی منابع کافی
        for (String material : Basics.MATERIALS_NAME) {
            Object materialCostObj = cost.get(material);
            if (materialCostObj != null) {
                int costAmount = parseMaterialCost(materialCostObj);
                int available = playerColony.getMaterial(material);

                if (costAmount > 0 && available < costAmount) {
                    return false; // منابع کافی نیست
                }
            }
        }

        // کسر منابع
        for (String material : Basics.MATERIALS_NAME) {
            Object materialCostObj = cost.get(material);
            if (materialCostObj != null) {
                int costAmount = parseMaterialCost(materialCostObj);
                if (costAmount > 0) {
                    playerColony.updateResourceAmount(material, -costAmount);
                }
            }
        }

        return true;
    }

    private int parseMaterialCost(Object costObj) {
        if (costObj instanceof Long) return ((Long) costObj).intValue();
        if (costObj instanceof Integer) return (Integer) costObj;
        if (costObj instanceof String) {
            try { return Integer.parseInt((String) costObj); }
            catch (NumberFormatException e) { return 0; }
        }
        return 0;
    }

    public JSONArray getUnavailableUnits() {
        return this.unavailableUnits;
    }

    public int getLevel() {
        return lvl;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
