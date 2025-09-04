package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import models.Basics;
import models.buildings.*;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class BuildingPlacementHandler {
    private GameScreen gameScreen;

    public BuildingPlacementHandler(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void placeBuilding(float x, float y) {
        String selectedBuildingType = gameScreen.getSelectedBuildingType();
        gameScreen.addMessage("Attempting to place " + selectedBuildingType + " at " + (int)x + ", " + (int)y);

        if (gameScreen.isTownHallPlaced() && selectedBuildingType.equals("townHall")) {
            gameScreen.addMessage("You can only have one Town Hall!");
            gameScreen.setSelectedBuildingType(null);
            return;
        }

        if (!isValidPosition(x, y)) {
            gameScreen.addMessage("Cannot build here - invalid position!");
            return;
        }

        if (!selectedBuildingType.equals("townHall")) {
            boolean success = checkAndDeductBuildingCost(selectedBuildingType);
            if (!success) {
                gameScreen.addMessage("Building failed: not enough resources.");
                gameScreen.setSelectedBuildingType(null);
                return;
            }
        }

        try {
            int roundedX = (int) (Math.floor(x / 64) * 64);
            int roundedY = (int) (Math.floor(y / 64) * 64);

            Building newBuilding = createBuilding(selectedBuildingType, roundedX, roundedY);
            gameScreen.getBuildings().add(newBuilding);

            if (selectedBuildingType.equals("townHall")) {
                gameScreen.setTownHallPlaced(true);
                gameScreen.setHasTownHall(true);
                gameScreen.createBuildingToolbar();
                gameScreen.createRightBottomToolbar();
                gameScreen.addMessage("Town Hall built successfully! Now you can build other buildings and attack.");
            } else {
                gameScreen.addMessage(selectedBuildingType + " built successfully at " + roundedX + ", " + roundedY);
            }

        } catch (Exception e) {
            gameScreen.addMessage("Failed to build: " + e.getMessage());
            Gdx.app.error("PlaceBuilding", "Error: " + e.getMessage(), e);
        }

        gameScreen.setSelectedBuildingType(null);
    }

    public boolean checkAndDeductBuildingCost(String buildingType) {
        try {
            String configKey = gameScreen.getConfigKey(buildingType);
            Gdx.app.log("CostDebug", "Building: " + buildingType + ", ConfigKey: " + configKey);

            JSONObject config = gameScreen.getNestedConfig(configKey);

            if (config == null) {
                gameScreen.addMessage("Error: Configuration not found for " + buildingType);
                return false;
            }

            JSONObject cost = gameScreen.getCostObject(config);

            if (cost == null) {
                if (config.containsKey("lvl1")) {
                    Object lvl1Obj = config.get("lvl1");
                    if (lvl1Obj instanceof JSONObject) {
                        JSONObject lvl1Config = (JSONObject) lvl1Obj;
                        cost = gameScreen.getCostObject(lvl1Config);
                    }
                }

                if (cost == null) {
                    gameScreen.addMessage("Error: No cost defined in config for " + buildingType);
                    return false;
                }
            }

            Gdx.app.log("CostDebug", "Cost object for " + buildingType + ": " + cost.toString());

            List<String> missingResources = new ArrayList<>();
            for (String material : Basics.MATERIALS_NAME) {
                Object materialCostObj = cost.get(material);
                if (materialCostObj != null) {
                    int costAmount = gameScreen.parseMaterialCost(materialCostObj);
                    Gdx.app.log("CostDebug", material + " cost: " + costAmount);
                    if (costAmount > 0 && gameScreen.getPlayerColony().getRecourse(material) < costAmount) {
                        missingResources.add(material + ": " + gameScreen.getPlayerColony().getRecourse(material) + "/" + costAmount);
                    }
                }
            }

            if (!missingResources.isEmpty()) {
                gameScreen.addMessage("Not enough resources: " + String.join(", ", missingResources));
                return false;
            }

            for (String material : Basics.MATERIALS_NAME) {
                Object materialCostObj = cost.get(material);
                if (materialCostObj != null) {
                    int costAmount = gameScreen.parseMaterialCost(materialCostObj);
                    if (costAmount > 0) {
                        gameScreen.getPlayerColony().updateRecourse(material, -costAmount);
                        gameScreen.addMessage("-" + costAmount + " " + material);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            gameScreen.addMessage("Error deducting resources for " + buildingType + ": " + e.getMessage());
            Gdx.app.error("CostDebug", "Error in checkAndDeductBuildingCost: " + e.getMessage(), e);
            return false;
        }
    }

    public Building createBuilding(String type, int x, int y) throws Exception {
        Texture texture = gameScreen.getBuildingTextures().get(type);
        int size = 64;

        switch (type) {
            case "townHall": return new TownHall(gameScreen.getPlayerColony(), new Vector2(x, y), size, size);
            case "house": return new House(gameScreen.getPlayerColony(), new Vector2(x, y), size, size);
            case "barracks": return new Barracks(gameScreen.getPlayerColony(), new Vector2(x, y), size, size);
            case "hospital": return new Hospital(gameScreen.getPlayerColony(), new Vector2(x, y), size, size);
            case "farm": case "stone": case "gold": case "iron": case "husbandry": case "lumbering":
                return new Mine(gameScreen.getPlayerColony(), new Vector2(x, y), size, size, type);
            case "market": return new Market(gameScreen.getPlayerColony(), new Vector2(x, y), size, size);
            case "tower": return new Tower(gameScreen.getPlayerColony(), new Vector2(x, y), size, size);
            default: throw new Exception("Unknown building type: " + type);
        }
    }

    public boolean isValidPosition(float x, float y) {
        return isBuildable(x, y) && !isOccupied(x, y);
    }

    public boolean isBuildable(float worldX, float worldY) {
        try {
            int tileWidth = gameScreen.getMap().getProperties().get("tilewidth", Integer.class);
            int tileHeight = gameScreen.getMap().getProperties().get("tileheight", Integer.class);
            int mapWidthInTiles = gameScreen.getMap().getProperties().get("width", Integer.class);
            int mapHeightInTiles = gameScreen.getMap().getProperties().get("height", Integer.class);

            int tileX = (int)(worldX / tileWidth);
            int tileY = (int)(worldY / tileHeight);

            if (tileX < 0 || tileX >= mapWidthInTiles || tileY < 0 || tileY >= mapHeightInTiles) return false;

            TiledMapTileLayer obstacleLayer = (TiledMapTileLayer) gameScreen.getMap().getLayers().get("mountain and tree");
            if (obstacleLayer != null) {
                TiledMapTileLayer.Cell cell = obstacleLayer.getCell(tileX, tileY);
                return cell == null || cell.getTile() == null;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOccupied(float x, float y) {
        for (Building building : gameScreen.getBuildings()) {
            Vector2 pos = building.getCoordinates().getLocation();
            if (Math.abs(pos.x - x) < 64 && Math.abs(pos.y - y) < 64) return true;
        }
        return false;
    }
}
