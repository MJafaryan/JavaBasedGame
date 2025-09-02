package my.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.*;
import models.buildings.*;
import models.user.Colony;
import models.user.User;
import models.Basics;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;

import java.util.*;

public class GameScreen implements Screen {
    private final MyGame game;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private Stage uiStage;
    private Viewport uiViewport;

    private List<Building> buildings = new ArrayList<>();
    private String selectedBuildingType = null;

    private Map<String, Texture> buildingTextures = new HashMap<>();

    private Colony playerColony;
    private User playerUser;

    private static final float WORLD_WIDTH = 1000;
    private static final float WORLD_HEIGHT = 1000;

    private Label resourcesLabel;
    private Label messagesLabel;
    private ScrollPane messagesScrollPane;
    private StringBuilder messagesBuilder = new StringBuilder();
    private InputMultiplexer inputMultiplexer;

    private InputManager inputManager;
    private boolean hasTownHall = false;
    private boolean townHallPlaced = false;

    public GameScreen(MyGame game, Colony playerColony) {
        this.game = game;
        this.playerColony = playerColony;
        try {
            initializeGame();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Fatal error: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize GameScreen", e);
        }
    }

    private void initializeGame() {
        hasTownHall = playerColony.getImportantBuildings().get("townHall") != null;

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        try {
            map = new TmxMapLoader().load("Map/Map1/Map1.tmx");
            renderer = new OrthogonalTiledMapRenderer(map);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to load map: " + e.getMessage());
            map = new TiledMap();
            renderer = new OrthogonalTiledMapRenderer(map);
        }

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);

        uiViewport = new ScreenViewport();
        uiStage = new Stage(uiViewport, batch);
        createInfoToolbar();

        try {
            int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);
            int mapWidthInTiles = map.getProperties().get("width", Integer.class);
            int mapHeightInTiles = map.getProperties().get("height", Integer.class);
            float mapWidth = mapWidthInTiles * tileWidth;
            float mapHeight = mapHeightInTiles * tileHeight;
            inputManager = new InputManager(camera, mapWidth, mapHeight, viewport.getWorldWidth(), viewport.getWorldHeight());
        } catch (Exception e) {
            inputManager = new InputManager(camera, WORLD_WIDTH, WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
        }

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                handleGameInput(screenX, screenY, button);
                return true;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                inputManager.handleMouseScroll(amountY);
                return true;
            }
        });
        Gdx.input.setInputProcessor(inputMultiplexer);

        String[] textureNames = {"house", "barracks", "farm", "hospital", "stone", "gold", "iron", "husbandry", "townHall", "market", "tower", "lumbering"};
        for (String name : textureNames) {
            buildingTextures.put(name, loadTextureSafe(name + ".png"));
        }

        createBuildingToolbar();
        initializeColonyResources();
        addMessage("Game started successfully!");

        // دیباگ config برای مارکت و هاسپیتال
        debugBuildingConfig("market");
        debugBuildingConfig("hospital");

        if (hasTownHall) {
            townHallPlaced = true;
            addMessage("Town Hall already exists in your colony.");
        } else {
            addMessage("Please place your Town Hall first! Click anywhere to place it for free.");
            selectedBuildingType = "townHall";
        }
    }

    private void debugBuildingConfig(String buildingType) {
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

                // بررسی برای ساختمان‌های چند سطحی
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

    private void createInfoToolbar() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;

        Table resourcesTable = new Table();
        resourcesTable.setFillParent(true);
        resourcesTable.top().left().pad(10);
        resourcesLabel = new Label("", labelStyle);
        resourcesTable.add(resourcesLabel).expandX().left().pad(10);

        Table messagesTable = new Table();
        messagesTable.setFillParent(true);
        messagesTable.top().right().pad(10);
        messagesLabel = new Label("", labelStyle);
        messagesLabel.setWrap(true);
        messagesLabel.setAlignment(Align.right);
        messagesScrollPane = new ScrollPane(messagesLabel);
        messagesScrollPane.setScrollingDisabled(true, false);
        messagesTable.add(messagesScrollPane).width(300).height(100).pad(10);

        uiStage.addActor(resourcesTable);
        uiStage.addActor(messagesTable);
    }

    private void createBuildingToolbar() {
        Table toolbar = new Table();
        toolbar.setFillParent(true);
        toolbar.bottom();

        Table buttonRow = new Table();
        for (String buildingType : buildingTextures.keySet()) {
            if (!townHallPlaced) {
                if (buildingType.equals("townHall")) {
                    ImageButton button = createBuildingButton(buildingTextures.get(buildingType), buildingType);
                    buttonRow.add(button).size(80, 80).pad(10);
                }
                continue;
            }

            if (!buildingType.equals("townHall")) {
                ImageButton button = createBuildingButton(buildingTextures.get(buildingType), buildingType);
                buttonRow.add(button).size(80, 80).pad(10);
            }
        }

        toolbar.add(buttonRow).padBottom(20);
        uiStage.addActor(toolbar);
    }

    private ImageButton createBuildingButton(Texture texture, String buildingType) {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!townHallPlaced && !buildingType.equals("townHall")) {
                    addMessage("You must place Town Hall first!");
                    return;
                }

                selectedBuildingType = selectedBuildingType != null && selectedBuildingType.equals(buildingType) ?
                    null : buildingType;
                addMessage(selectedBuildingType == null ?
                    "Building deselected: " + buildingType :
                    "Building selected: " + buildingType + " | Cost: " + getBuildingInfo(buildingType));
            }
        });
        return button;
    }

    private void addMessage(String message) {
        if (messagesLabel == null) return;

        messagesBuilder.append("• ").append(message).append("\n");
        messagesLabel.setText(messagesBuilder.toString());

        if (messagesScrollPane != null) messagesScrollPane.scrollTo(0, 0, 0, 0);
        if (messagesBuilder.length() > 1000) messagesBuilder.delete(0, 200);
    }

    private void updateResourcesDisplay() {
        if (resourcesLabel == null) return;

        String resourcesText = String.format(
            "Wood: %d | Stone: %d | Coin: %d | Iron: %d | Food: %d | Pop: %d/%d | Storage: %d/%d",
            playerColony.getMaterial("wood"), playerColony.getMaterial("stone"),
            playerColony.getMaterial("coin"), playerColony.getMaterial("iron"),
            playerColony.getMaterial("food"),
            playerColony.getPopulation(), playerColony.getMaximumPossiblePopulation(),
            playerColony.getUsedCapacity(), playerColony.getStorageCapacity()
        );
        resourcesLabel.setText(resourcesText);
    }

    private void handleGameInput(int screenX, int screenY, int button) {
        if (isClickOnUI(screenX, screenY)) return;

        Vector3 screenPos = new Vector3(screenX, screenY, 0);
        Vector3 worldPos = camera.unproject(screenPos.cpy());

        float roundedX = (int)(worldPos.x / 64) * 64;
        float roundedY = (int)(worldPos.y / 64) * 64;

        if (selectedBuildingType != null && button == Input.Buttons.LEFT) {
            placeBuilding(roundedX, roundedY);
        } else if (button == Input.Buttons.RIGHT) {
            selectedBuildingType = null;
            addMessage("Selection cancelled");
        }

        if (!townHallPlaced && selectedBuildingType == null) {
            selectedBuildingType = "townHall";
            placeBuilding(roundedX, roundedY);
        }
    }

    private Texture loadTextureSafe(String path) {
        try {
            return new Texture(Gdx.files.internal(path));
        } catch (Exception e) {
            Gdx.app.error("Texture", "Failed to load: " + path);
            Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.MAGENTA);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            return texture;
        }
    }

    private void initializeColonyResources() {
        String[] resources = {"wood", "stone", "coin", "iron", "food"};
        int[] amounts = {1000, 1000, 500, 500, 2000};

        for (int i = 0; i < resources.length; i++) {
            try {
                playerColony.updateResourceAmount(resources[i], amounts[i]);
            } catch (Exception e) {
                Gdx.app.error("Resource", "FAILED to update " + resources[i]);
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        inputManager.update();
        camera.update();

        renderer.setView(camera);
        renderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderBuildings();
        renderSelectionInfo();
        batch.end();

        updateResourcesDisplay();

        uiStage.act(delta);
        uiStage.draw();

        if (!townHallPlaced) {
            batch.begin();
            font.draw(batch, "Click anywhere to place your Town Hall (FREE)", 50, 50);
            batch.end();
        }
    }

    private void placeBuilding(float x, float y) {
        addMessage("Attempting to place " + selectedBuildingType + " at " + (int)x + ", " + (int)y);

        if (townHallPlaced && selectedBuildingType.equals("townHall")) {
            addMessage("You can only have one Town Hall!");
            selectedBuildingType = null;
            return;
        }

        if (!isValidPosition(x, y)) {
            addMessage("Cannot build here - invalid position!");
            return;
        }

        if (!selectedBuildingType.equals("townHall")) {
            boolean success = checkAndDeductBuildingCost(selectedBuildingType);
            if (!success) {
                addMessage("Building failed: not enough resources.");
                selectedBuildingType = null;
                return;
            }
        }

        try {
            int roundedX = (int) (Math.floor(x / 64) * 64);
            int roundedY = (int) (Math.floor(y / 64) * 64);

            Building newBuilding = createBuilding(selectedBuildingType, roundedX, roundedY);
            buildings.add(newBuilding);

            if (selectedBuildingType.equals("townHall")) {
                townHallPlaced = true;
                hasTownHall = true;
                createBuildingToolbar();
                addMessage("Town Hall built successfully! Now you can build other buildings.");
            } else {
                addMessage(selectedBuildingType + " built successfully at " + roundedX + ", " + roundedY);
            }

        } catch (Exception e) {
            addMessage("Failed to build: " + e.getMessage());
            Gdx.app.error("PlaceBuilding", "Error: " + e.getMessage(), e);
        }

        selectedBuildingType = null;
    }

    private boolean checkAndDeductBuildingCost(String buildingType) {
        try {
            String configKey = getConfigKey(buildingType);
            Gdx.app.log("CostDebug", "Building: " + buildingType + ", ConfigKey: " + configKey);

            JSONObject config = getNestedConfig(configKey);

            if (config == null) {
                addMessage("Config not found for " + buildingType + ", using default costs");
                return checkAndDeductDefaultCost(buildingType);
            }

            JSONObject cost = getCostObject(config);

            if (cost == null) {
                // برای ساختمان‌های چند سطحی مثل farm که cost در lvl1 قرار دارد
                if (config.containsKey("lvl1")) {
                    Object lvl1Obj = config.get("lvl1");
                    if (lvl1Obj instanceof JSONObject) {
                        JSONObject lvl1Config = (JSONObject) lvl1Obj;
                        cost = getCostObject(lvl1Config);
                    }
                }

                if (cost == null) {
                    addMessage("No cost defined in config for " + buildingType + ", using default costs");
                    return checkAndDeductDefaultCost(buildingType);
                }
            }

            Gdx.app.log("CostDebug", "Cost object for " + buildingType + ": " + cost.toString());

            List<String> missingResources = new ArrayList<>();
            for (String material : Basics.MATERIALS_NAME) {
                Object materialCostObj = cost.get(material);
                if (materialCostObj != null) {
                    int costAmount = parseMaterialCost(materialCostObj);
                    Gdx.app.log("CostDebug", material + " cost: " + costAmount);
                    if (costAmount > 0 && playerColony.getMaterial(material) < costAmount) {
                        missingResources.add(material + ": " + playerColony.getMaterial(material) + "/" + costAmount);
                    }
                }
            }

            if (!missingResources.isEmpty()) {
                addMessage("Not enough resources: " + String.join(", ", missingResources));
                return false;
            }

            for (String material : Basics.MATERIALS_NAME) {
                Object materialCostObj = cost.get(material);
                if (materialCostObj != null) {
                    int costAmount = parseMaterialCost(materialCostObj);
                    if (costAmount > 0) {
                        playerColony.updateResourceAmount(material, -costAmount);
                        addMessage("-" + costAmount + " " + material);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            addMessage("Error deducting resources for " + buildingType + ": " + e.getMessage());
            Gdx.app.error("CostDebug", "Error in checkAndDeductBuildingCost: " + e.getMessage(), e);
            return false;
        }
    }

    private boolean checkAndDeductDefaultCost(String buildingType) {
        Map<String, Integer> defaultCosts = getDefaultCosts(buildingType);

        if (defaultCosts == null) {
            addMessage("No cost information available for " + buildingType);
            return false;
        }

        List<String> missingResources = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : defaultCosts.entrySet()) {
            if (playerColony.getMaterial(entry.getKey()) < entry.getValue()) {
                missingResources.add(entry.getKey() + ": " + playerColony.getMaterial(entry.getKey()) + "/" + entry.getValue());
            }
        }

        if (!missingResources.isEmpty()) {
            addMessage("Not enough resources: " + String.join(", ", missingResources));
            return false;
        }

        for (Map.Entry<String, Integer> entry : defaultCosts.entrySet()) {
            playerColony.updateResourceAmount(entry.getKey(), -entry.getValue());
            addMessage("-" + entry.getValue() + " " + entry.getKey() + " (default)");
        }

        return true;
    }

    private Map<String, Integer> getDefaultCosts(String buildingType) {
        Map<String, Map<String, Integer>> defaultCosts = new HashMap<>();
        defaultCosts.put("townHall", Map.of("wood", 50, "stone", 30, "coin", 100));
        defaultCosts.put("house", Map.of("wood", 8, "coin", 10));
        defaultCosts.put("barracks", Map.of("wood", 20, "stone", 10, "coin", 20));
        defaultCosts.put("farm", Map.of("wood", 15, "coin", 8));
        defaultCosts.put("hospital", Map.of("wood", 40, "stone", 30, "iron", 16, "coin", 60));
        defaultCosts.put("stone", Map.of("wood", 15, "coin", 15));
        defaultCosts.put("gold", Map.of("stone", 8, "wood", 10, "iron", 5, "coin", 5));
        defaultCosts.put("iron", Map.of("wood", 25, "stone", 15, "coin", 25));
        defaultCosts.put("husbandry", Map.of("wood", 20, "coin", 10));
        defaultCosts.put("market", Map.of("wood", 25, "stone", 20, "coin", 25));
        defaultCosts.put("tower", Map.of("wood", 30, "stone", 15, "coin", 30));
        defaultCosts.put("lumbering", Map.of("coin", 8));

        return defaultCosts.get(buildingType);
    }

    private String getConfigKey(String buildingType) {
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

    private JSONObject getNestedConfig(String configKey) {
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

                // گرفتن اولین سطح - استفاده از پارامتر صحیح برای getDataFromJson
                JSONObject current = (JSONObject) SimplerJson.getDataFromJson(Building.configFile, keys[0]);
                if (current == null) {
                    Gdx.app.error("ConfigDebug", "First level not found: " + keys[0]);
                    return null;
                }

                // رفتن به سطوح تو در تو
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
                // برای کلیدهای ساده - استفاده از پارامتر صحیح برای getDataFromJson
                JSONObject buildingConfig = (JSONObject) SimplerJson.getDataFromJson(Building.configFile, configKey);

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

    private int parseMaterialCost(Object costObj) {
        if (costObj instanceof Long) return ((Long) costObj).intValue();
        if (costObj instanceof Integer) return (Integer) costObj;
        if (costObj instanceof String) {
            try { return Integer.parseInt((String) costObj); }
            catch (NumberFormatException e) { return 0; }
        }
        return 0;
    }

    private JSONObject getCostObject(JSONObject config) {
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

    private Building createBuilding(String type, int x, int y) throws Exception {
        Texture texture = buildingTextures.get(type);
        int size = 64;

        switch (type) {
            case "townHall": return new TownHall(texture, x, y, size, size, "townHall", playerColony);
            case "house": return new House(texture, x, y, size, size, "house", playerColony);
            case "barracks": return new Barracks(texture, x, y, size, size, "barracks", playerColony);
            case "farm": return new Farm(texture, x, y, size, size, "farm", playerColony);
            case "hospital": return new Hospital(texture, x, y, size, size, "hospital", playerColony);
            case "stone": return new StoneMine(texture, x, y, size, size, "stone_mine", playerColony);
            case "gold": return new GoldMine(texture, x, y, size, size, "gold_mine", playerColony);
            case "iron": return new IronMine(texture, x, y, size, size, "iron_mine", playerColony);
            case "husbandry": return new AnimalHusbandry(texture, x, y, size, size, "animal_husbandry", playerColony);
            case "market": return new Market(texture, x, y, size, size, "market", playerColony);
            case "tower": return new Tower(texture, x, y, size, size, "tower", playerColony);
            case "lumbering": return new Lumbering(texture, x, y, size, size, "lumbering", playerColony);
            default: throw new Exception("Unknown building type: " + type);
        }
    }

    private boolean isValidPosition(float x, float y) {
        return isBuildable(x, y) && !isOccupied(x, y);
    }

    private boolean isBuildable(float worldX, float worldY) {
        try {
            int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);
            int mapWidthInTiles = map.getProperties().get("width", Integer.class);
            int mapHeightInTiles = map.getProperties().get("height", Integer.class);

            int tileX = (int)(worldX / tileWidth);
            int tileY = (int)(worldY / tileHeight);

            if (tileX < 0 || tileX >= mapWidthInTiles || tileY < 0 || tileY >= mapHeightInTiles) return false;

            TiledMapTileLayer obstacleLayer = (TiledMapTileLayer) map.getLayers().get("mountain and tree");
            if (obstacleLayer != null) {
                TiledMapTileLayer.Cell cell = obstacleLayer.getCell(tileX, tileY);
                return cell == null || cell.getTile() == null;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isOccupied(float x, float y) {
        for (Building building : buildings) {
            Vector2 pos = building.getPosition();
            if (Math.abs(pos.x - x) < 64 && Math.abs(pos.y - y) < 64) return true;
        }
        return false;
    }

    private void renderBuildings() {
        for (Building building : buildings) {
            try {
                Texture texture = building.getTexture();
                Vector2 position = building.getPosition();
                if (texture != null) {
                    batch.draw(texture, position.x, position.y, texture.getWidth(), texture.getHeight());
                }
            } catch (Exception e) {
                Gdx.app.error("RenderBuildings", "Error rendering building: " + e.getMessage(), e);
            }
        }
    }

    private void renderSelectionInfo() {
        if (selectedBuildingType == null && townHallPlaced) return;
        if (!townHallPlaced) selectedBuildingType = "townHall";

        try {
            Vector3 screenPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 worldPos = camera.unproject(screenPos.cpy());

            Texture texture = buildingTextures.get(selectedBuildingType);

            if (texture != null) {
                float drawX = (int)(worldPos.x / 64) * 64;
                float drawY = (int)(worldPos.y / 64) * 64;

                boolean canBuild = isValidPosition(drawX, drawY);
                batch.setColor(1, 1, 1, canBuild ? 0.7f : 0.3f);
                batch.draw(texture, drawX, drawY, texture.getWidth(), texture.getHeight());
                batch.setColor(1, 1, 1, 1);

                if (!townHallPlaced) {
                    font.draw(batch, "FREE Town Hall Placement", drawX, drawY - 10);
                }
            }
        } catch (Exception e) {
            addMessage("Selection render error: " + e.getMessage());
        }
    }

    private String getBuildingInfo(String buildingType) {
        if (buildingType.equals("townHall") && !townHallPlaced) {
            return "FREE (First building)";
        }

        try {
            JSONObject config = getNestedConfig(getConfigKey(buildingType));
            if (config == null) return "Config not found";

            JSONObject cost = getCostObject(config);
            if (cost == null) {
                // بررسی برای ساختمان‌های چند سطحی
                if (config.containsKey("lvl1")) {
                    Object lvl1Obj = config.get("lvl1");
                    if (lvl1Obj instanceof JSONObject) {
                        JSONObject lvl1Config = (JSONObject) lvl1Obj;
                        cost = getCostObject(lvl1Config);
                    }
                }
                if (cost == null) return "No cost information";
            }

            StringBuilder info = new StringBuilder();
            for (String material : Basics.MATERIALS_NAME) {
                Object materialCost = cost.get(material);
                if (materialCost != null) {
                    int costAmount = parseMaterialCost(materialCost);
                    if (costAmount > 0) info.append(material).append(": ").append(costAmount).append(" ");
                }
            }
            return info.toString().isEmpty() ? "Cost info unavailable" : info.toString();
        } catch (Exception e) {
            return "Error loading cost";
        }
    }

    private boolean isClickOnUI(int screenX, int screenY) {
        Vector2 stageCoords = uiStage.screenToStageCoordinates(new Vector2(screenX, screenY));
        return uiStage.hit(stageCoords.x, stageCoords.y, true) != null;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);

        try {
            int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);
            int mapWidthInTiles = map.getProperties().get("width", Integer.class);
            int mapHeightInTiles = map.getProperties().get("height", Integer.class);
            float mapWidth = mapWidthInTiles * tileWidth;
            float mapHeight = mapHeightInTiles * tileHeight;
            inputManager = new InputManager(camera, mapWidth, mapHeight, viewport.getWorldWidth(), viewport.getWorldHeight());
        } catch (Exception e) {
            inputManager = new InputManager(camera, WORLD_WIDTH, WORLD_HEIGHT, viewport.getWorldWidth(), viewport.getWorldHeight());
        }

        addMessage("Screen resized to: " + width + "x" + height);
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        batch.dispose();
        font.dispose();
        uiStage.dispose();

        for (Texture texture : buildingTextures.values()) {
            if (texture != null) texture.dispose();
        }

        playerColony.save();
        addMessage("Game saved on dispose");
    }

    @Override
    public void pause() {
        playerColony.save();
        addMessage("Game saved on pause");
    }

    @Override
    public void resume() {
        addMessage("Game resumed");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        addMessage("Game screen shown");
    }

    @Override
    public void hide() {
        playerColony.save();
        addMessage("Game saved on hide");
    }
}
