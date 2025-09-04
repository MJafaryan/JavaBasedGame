package my.game;

import com.badlogic.gdx.Gdx;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.buildings.Building;
import java.util.HashMap;
import java.util.Map;

public class BuildingConfigManager {
    public void debugBuildingConfig(String buildingType) {
        try {
            String configKey = getConfigKey(buildingType);
            Gdx.app.log("ConfigDebug", "=== Debugging " + buildingType + " ===");
            Gdx.app.log("ConfigDebug", "Config key: " + configKey);

            JSONObject config = getNestedConfig(configKey);
            if (config == null) {
                Gdx.app.error("ConfigDebug", "Config is null for " + buildingType);
                return;
            }

            Gdx.app.log("ConfigDebug", "Full config: " + config.toJSONString());

            JSONObject cost = getCostObject(config);
            if (cost != null) {
                Gdx.app.log("ConfigDebug", "Cost found: " + cost.toJSONString());
            } else {
                Gdx.app.error("ConfigDebug", "No cost found in config for " + buildingType);

                if (config.containsKey("lvl1")) {
                    Object lvl1Obj = config.get("lvl1");
                    if (lvl1Obj instanceof JSONObject) {
                        JSONObject lvl1Config = (JSONObject) lvl1Obj;
                        cost = getCostObject(lvl1Config);
                        if (cost != null) {
                            Gdx.app.log("ConfigDebug", "Cost found in lvl1: " + cost.toJSONString());
                        }
                    }
                }
            }

        } catch (Exception e) {
            Gdx.app.error("ConfigDebug", "Error debugging " + buildingType + ": " + e.getMessage(), e);
        }
    }

    public String getConfigKey(String buildingType) {
        Map<String, String> configKeys = new HashMap<>();
        configKeys.put("house", "house");
        configKeys.put("barracks", "barracks");
        configKeys.put("farm", "farms.farm");
        configKeys.put("hospital", "hospital");
        configKeys.put("stone", "farms.stoneMine");
        configKeys.put("gold", "farms.goldMine");
        configKeys.put("iron", "farms.ironMine");
        configKeys.put("husbandry", "farms.animalHusbandry");
        configKeys.put("townHall", "townHall");
        configKeys.put("market", "market");
        configKeys.put("tower", "tower");
        configKeys.put("lumbering", "farms.lumbering");
        return configKeys.getOrDefault(buildingType, buildingType);
    }

    public JSONObject getNestedConfig(String configKey) {
        try {
            Gdx.app.log("ConfigDebug", "Looking for config key: " + configKey);

            if (configKey == null || configKey.isEmpty()) {
                Gdx.app.error("ConfigDebug", "Config key is null or empty");
                return null;
            }

            if (configKey.contains(".")) {
                String[] keys = configKey.split("\\.");
                if (keys.length == 0) {
                    Gdx.app.error("ConfigDebug", "Invalid config key format: " + configKey);
                    return null;
                }

                JSONObject current = (JSONObject) SimplerJson.getDataFromJson(Building.getConfigFile(), keys[0]);
                if (current == null) {
                    Gdx.app.error("ConfigDebug", "First level not found: " + keys[0]);
                    return null;
                }

                for (int i = 1; i < keys.length; i++) {
                    if (current == null) {
                        Gdx.app.error("ConfigDebug", "Current is null at key: " + keys[i]);
                        return null;
                    }

                    Object nextLevel = current.get(keys[i]);
                    if (nextLevel instanceof JSONObject) {
                        current = (JSONObject) nextLevel;
                    } else {
                        Gdx.app.error("ConfigDebug", "Key '" + keys[i] + "' is not a JSONObject, it's: " +
                            (nextLevel != null ? nextLevel.getClass().getSimpleName() : "null"));
                        return null;
                    }
                }
                return current;

            } else {
                JSONObject buildingConfig = (JSONObject) SimplerJson.getDataFromJson(Building.getConfigFile(), configKey);

                if (buildingConfig == null) {
                    Gdx.app.error("ConfigDebug", "Direct config not found: " + configKey);
                    return null;
                }

                return buildingConfig;
            }
        } catch (Exception e) {
            Gdx.app.error("ConfigDebug", "Error getting nested config for '" + configKey + "': " + e.getMessage(), e);
            return null;
        }
    }

    public int parseMaterialCost(Object costObj) {
        if (costObj instanceof Long) return ((Long) costObj).intValue();
        if (costObj instanceof Integer) return (Integer) costObj;
        if (costObj instanceof String) {
            try { return Integer.parseInt((String) costObj); }
            catch (NumberFormatException e) { return 0; }
        }
        return 0;
    }

    public JSONObject getCostObject(JSONObject config) {
        if (config == null) {
            Gdx.app.error("CostDebug", "Config is null in getCostObject");
            return null;
        }

        try {
            if (config.containsKey("cost")) {
                Object costObj = config.get("cost");
                if (costObj instanceof JSONObject) {
                    return (JSONObject) costObj;
                } else {
                    Gdx.app.error("CostDebug", "Cost is not a JSONObject: " + costObj);
                    return null;
                }
            }

            return null;
        } catch (Exception e) {
            Gdx.app.error("CostDebug", "Error in getCostObject: " + e.getMessage(), e);
            return null;
        }
    }
}
