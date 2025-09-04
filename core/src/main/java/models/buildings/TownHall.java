package models.buildings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;

public class TownHall extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "townHall");
        Gdx.app.log("TownHallDebug", "Static block - Config loaded: " + (config != null));
        if (config != null) {
            Gdx.app.log("TownHallDebug", "Config keys: " + config.keySet().toString());
        }
    }

    public TownHall(Texture texture, int x, int y, int width, int height, String type, Colony colony) {
        super(texture, x, y, width, height, type, colony);
        this.maxLevel = 5;

        Gdx.app.log("TownHallDebug", "Constructor called - Type: " + type);
        Gdx.app.log("TownHallDebug", "Config available: " + (config != null));

        if (config != null) {
            // خواندن صحیح از JSON
            JSONObject lvl1Config = (JSONObject) config.get("lvl1");
            Gdx.app.log("TownHallDebug", "lvl1Config: " + (lvl1Config != null));

            if (lvl1Config != null) {
                Gdx.app.log("TownHallDebug", "lvl1Config keys: " + lvl1Config.keySet().toString());

                Object healthObj = lvl1Config.get("health");
                Object capacityObj = lvl1Config.get("capacity");

                Gdx.app.log("TownHallDebug", "health object: " + healthObj + " (type: " + (healthObj != null ? healthObj.getClass().getSimpleName() : "null") + ")");
                Gdx.app.log("TownHallDebug", "capacity object: " + capacityObj + " (type: " + (capacityObj != null ? capacityObj.getClass().getSimpleName() : "null") + ")");

                if (healthObj instanceof Long) {
                    this.health = ((Long) healthObj).intValue();
                } else if (healthObj instanceof Integer) {
                    this.health = (Integer) healthObj;
                } else {
                    Gdx.app.error("TownHallDebug", "Invalid health type: " + (healthObj != null ? healthObj.getClass().getSimpleName() : "null"));
                    this.health = 1000; // مقدار پیش‌فرض
                }

                if (capacityObj instanceof Long) {
                    colony.setStorageCapacity(((Long) capacityObj).intValue());
                } else if (capacityObj instanceof Integer) {
                    colony.setStorageCapacity((Integer) capacityObj);
                } else {
                    Gdx.app.error("TownHallDebug", "Invalid capacity type: " + (capacityObj != null ? capacityObj.getClass().getSimpleName() : "null"));
                    colony.setStorageCapacity(300); // مقدار پیش‌فرض
                }

                this.lvl = 1;

                Gdx.app.log("TownHallDebug", "Initialized - Level: " + lvl + ", Health: " + health + ", Capacity: " + colony.getStorageCapacity());
            } else {
                Gdx.app.error("TownHallDebug", "lvl1Config is null!");
            }
        } else {
            Gdx.app.error("TownHallDebug", "Config is null in constructor!");
        }
    }

    public void upgrade() throws Exception {
        Gdx.app.log("TownHallDebug", "=== Upgrade called ===");
        Gdx.app.log("TownHallDebug", "Current level: " + lvl + ", Max level: " + maxLevel);

        if (lvl >= maxLevel) {
            Gdx.app.error("TownHallDebug", "Already at maximum level!");
            throw new Exception("Town Hall is already at maximum level");
        }

        String levelKey = "lvl" + (this.lvl + 1);
        Gdx.app.log("TownHallDebug", "Looking for level key: " + levelKey);

        if (config != null && config.containsKey(levelKey)) {
            JSONObject newLevelConfig = (JSONObject) config.get(levelKey);
            Gdx.app.log("TownHallDebug", "New level config found: " + (newLevelConfig != null));

            if (newLevelConfig != null) {
                Gdx.app.log("TownHallDebug", "New level config keys: " + newLevelConfig.keySet().toString());

                // خواندن health
                Object healthObj = newLevelConfig.get("health");
                Object capacityObj = newLevelConfig.get("capacity");

                Gdx.app.log("TownHallDebug", "New health: " + healthObj);
                Gdx.app.log("TownHallDebug", "New capacity: " + capacityObj);

                if (healthObj instanceof Long) {
                    this.health = ((Long) healthObj).intValue();
                } else if (healthObj instanceof Integer) {
                    this.health = (Integer) healthObj;
                }

                if (capacityObj instanceof Long) {
                    this.colony.setStorageCapacity(((Long) capacityObj).intValue());
                } else if (capacityObj instanceof Integer) {
                    this.colony.setStorageCapacity((Integer) capacityObj);
                }

                // اعمال تغییرات
                this.lvl++;

                Gdx.app.log("TownHallDebug", "Upgrade successful! New level: " + lvl + ", Health: " + health + ", Capacity: " + colony.getStorageCapacity());
            } else {
                Gdx.app.error("TownHallDebug", "New level config is null!");
                throw new Exception("Configuration not found for level " + (this.lvl + 1));
            }
        } else {
            Gdx.app.error("TownHallDebug", "Level key not found in config!");
            Gdx.app.error("TownHallDebug", "Config available: " + (config != null));
            if (config != null) {
                Gdx.app.error("TownHallDebug", "Available keys: " + config.keySet().toString());
            }
            throw new Exception("Configuration not found for level " + (this.lvl + 1));
        }
    }

    public int getLevel() {
        Gdx.app.log("TownHallDebug", "getLevel() called: " + lvl);
        return lvl;
    }

    public int getMaxLevel() {
        Gdx.app.log("TownHallDebug", "getMaxLevel() called: " + maxLevel);
        return maxLevel;
    }
}
