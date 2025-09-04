package my.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.*;
import models.buildings.Building;
import models.user.Colony;
import models.user.User;
import models.Basics;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;

import java.util.*;
import java.util.List;

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
    private Building selectedBuilding = null;

    private Map<String, Texture> buildingTextures = new HashMap<>();
    private Texture attackTexture;

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

    private Table rightBottomToolbar;
    private boolean rightBottomToolbarCreated = false;

    private BuildingInfoPanel buildingInfoPanel;
    private Skin skin;

    private BuildingInputHandler inputHandler;
    private BuildingPlacementHandler placementHandler;
    private BuildingUIManager uiManager;
    private BuildingConfigManager configManager;

    public GameScreen(MyGame game , Colony colony) {
        this.game = game;
        try {
            playerColony = colony;
        } catch (IllegalArgumentException e) {
            playerColony = new Colony(playerUser);
        }
        try {
            initializeGame();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Fatal error: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize GameScreen", e);
        }

    }

    private void initializeGame() {


        hasTownHall = playerColony.hasBuilding("townHall");

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        try {
            String bread = playerColony.getBread();
            switch (bread) {
                case "iran":
                    map = new TmxMapLoader().load("Map/Map1/Map1.tmx");
                    break;
                case "arab":
                    map = new TmxMapLoader().load("Map/Map2/Map2.tmx");
                    break;
                case "rome":
                    map = new TmxMapLoader().load("Map/Map3/Map3.tmx");
                    break;
                case "mongol":
                    map = new TmxMapLoader().load("Map/Map4/Map4.tmx");
                    break;
            }
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

        // ایجاد Skin ساده برای UI
        createBasicSkin();
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
            if (name.equals("townHall")) {
                buildingTextures.put(name ,loadTextureSafe(name+playerColony.getBread()+".png"));
            }
            else
                buildingTextures.put(name, loadTextureSafe(name+".png"));
        }

        attackTexture = loadTextureSafe("attack.png");

        // ابتدا managerها را مقداردهی کنید
        inputHandler = new BuildingInputHandler(this);
        placementHandler = new BuildingPlacementHandler(this);
        uiManager = new BuildingUIManager(this);
        configManager = new BuildingConfigManager();

        // سپس toolbarها را ایجاد کنید
        createBuildingToolbar();
        initializeColonyResources();
        addMessage("Game started successfully!");

        debugBuildingConfig("market");
        debugBuildingConfig("hospital");

        // ایجاد پنل اطلاعات ساختمان (در ابتدا مخفی)
        buildingInfoPanel = new BuildingInfoPanel(skin, uiStage, this);
        buildingInfoPanel.createBuildingInfoPanel();

        if (hasTownHall) {
            townHallPlaced = true;
            createRightBottomToolbar();
            addMessage("Town Hall already exists in your colony.");
        } else {
            addMessage("Please place your Town Hall first! Click anywhere to place it for free.");
            selectedBuildingType = "townHall";
        }
    }

    public void createBasicSkin() {
        skin = new Skin();
        BitmapFont uiFont = new BitmapFont();
        skin.add("default-font", uiFont);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = uiFont;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = uiFont;

        Pixmap buttonPixmap = new Pixmap(100, 40, Pixmap.Format.RGBA8888);
        buttonPixmap.setColor(Color.DARK_GRAY);
        buttonPixmap.fill();
        buttonPixmap.setColor(Color.LIGHT_GRAY);
        buttonPixmap.drawRectangle(0, 0, buttonPixmap.getWidth(), buttonPixmap.getHeight());
        skin.add("button-bg", new Texture(buttonPixmap));
        buttonPixmap.dispose();

        buttonStyle.up = skin.newDrawable("button-bg", Color.DARK_GRAY);
        buttonStyle.down = skin.newDrawable("button-bg", Color.GRAY);
        buttonStyle.over = skin.newDrawable("button-bg", Color.LIGHT_GRAY);
        skin.add("default", buttonStyle);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();
    }

    public void createRightBottomToolbar() {
        if (rightBottomToolbarCreated || !townHallPlaced) return;

        rightBottomToolbar = new Table();
        rightBottomToolbar.setFillParent(true);
        rightBottomToolbar.bottom().right().pad(10);

        ImageButton attackButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(attackTexture)));
        attackButton.setSize(80, 80);

        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean isPanelVisible = buildingInfoPanel.isAttackInfoPanelVisible();
                buildingInfoPanel.setAttackInfoPanelVisible(!isPanelVisible); // استفاده از متد صحیح
                addMessage("Attack panel " + (!isPanelVisible ? "opened" : "closed") + " - Select a player to attack");
            }
        });

        rightBottomToolbar.add(attackButton).padBottom(20).padRight(20);
        uiStage.addActor(rightBottomToolbar);

        rightBottomToolbarCreated = true;
        addMessage("Right bottom toolbar with Attack button created");
    }

    public void debugBuildingConfig(String buildingType) {
        configManager.debugBuildingConfig(buildingType);
    }

    public void createInfoToolbar() {
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

    public void createBuildingToolbar() {
        uiManager.createBuildingToolbar();
    }

    public ImageButton createBuildingButton(Texture texture, String buildingType) {
        return uiManager.createBuildingButton(texture, buildingType);
    }

    public void addMessage(String message) {
        if (messagesLabel == null) return;

        messagesBuilder.append("• ").append(message).append("\n");
        messagesLabel.setText(messagesBuilder.toString());

        if (messagesScrollPane != null) messagesScrollPane.scrollTo(0, 0, 0, 0);
        if (messagesBuilder.length() > 1000) messagesBuilder.delete(0, 200);
    }

    public void updateResourcesDisplay() {
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

    public void handleGameInput(int screenX, int screenY, int button) {
        inputHandler.handleGameInput(screenX, screenY, button);
    }

    public Building getBuildingAt(float x, float y) {
        for (Building building : buildings) {
            Vector2 pos = building.getPosition();
            if (Math.abs(pos.x - x) < 64 && Math.abs(pos.y - y) < 64) {
                return building;
            }
        }
        return null;
    }

    public Texture loadTextureSafe(String path) {
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

    public void initializeColonyResources() {
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            togglePausePanel();
        }

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
    private void togglePausePanel() {
        boolean isPanelVisible = buildingInfoPanel.isPauseInfoPanelVisible();
        buildingInfoPanel.setPauseInfoPanelVisible(!isPanelVisible);

        if (!isPanelVisible) {
            addMessage("Pause menu opened");
        } else {
            addMessage("Pause menu closed");
        }
    }
    public OrthographicCamera getCamera() {
        return camera;
    }

    public boolean hasEnoughResourcesForUpgrade(Building building) {
        try {
            String configKey = getConfigKey(building.getType());
            Gdx.app.log("UpgradeDebug", "=== Checking upgrade for: " + building.getType() + " ===");
            Gdx.app.log("UpgradeDebug", "Config key: " + configKey);

            JSONObject config = getNestedConfig(configKey);

            if (config == null) {
                Gdx.app.error("UpgradeDebug", "Config is null for: " + building.getType());
                return false;
            }

            String levelKey = "lvl" + (building.getLevel() + 1);
            Gdx.app.log("UpgradeDebug", "Looking for level key: " + levelKey);
            Gdx.app.log("UpgradeDebug", "Available keys in config: " + config.keySet().toString());

            if (config.containsKey(levelKey)) {
                JSONObject levelConfig = (JSONObject) config.get(levelKey);
                Gdx.app.log("UpgradeDebug", "Level config keys: " + levelConfig.keySet().toString());

                JSONObject cost = getCostObject(levelConfig);

                if (cost != null) {
                    Gdx.app.log("UpgradeDebug", "Cost found: " + cost.toJSONString());

                    for (String material : Basics.MATERIALS_NAME) {
                        Object materialCostObj = cost.get(material);
                        if (materialCostObj != null) {
                            int costAmount = parseMaterialCost(materialCostObj);
                            int available = playerColony.getMaterial(material);
                            Gdx.app.log("UpgradeDebug", material + " - Cost: " + costAmount + ", Available: " + available);

                            if (costAmount > 0 && available < costAmount) {
                                Gdx.app.log("UpgradeDebug", "Not enough " + material);
                                return false;
                            }
                        }
                    }
                    Gdx.app.log("UpgradeDebug", "Enough resources for upgrade!");
                    return true;
                } else {
                    Gdx.app.error("UpgradeDebug", "Cost is null for level: " + levelKey);

                    // بررسی دستی برای upgradeCost (مخصوص Town Hall)
                    if (levelConfig.containsKey("upgradeCost")) {
                        Object upgradeCostObj = levelConfig.get("upgradeCost");
                        if (upgradeCostObj instanceof JSONObject) {
                            JSONObject upgradeCost = (JSONObject) upgradeCostObj;
                            Gdx.app.log("UpgradeDebug", "Manual upgradeCost found: " + upgradeCost.toJSONString());

                            boolean enoughResources = true;
                            for (String material : Basics.MATERIALS_NAME) {
                                Object materialCostObj = upgradeCost.get(material);
                                if (materialCostObj != null) {
                                    int costAmount = parseMaterialCost(materialCostObj);
                                    int available = playerColony.getMaterial(material);
                                    Gdx.app.log("UpgradeDebug", material + " - UpgradeCost: " + costAmount + ", Available: " + available);

                                    if (costAmount > 0 && available < costAmount) {
                                        enoughResources = false;
                                        Gdx.app.log("UpgradeDebug", "Not enough " + material + " for upgrade");
                                    }
                                }
                            }
                            Gdx.app.log("UpgradeDebug", "Enough resources for upgrade: " + enoughResources);
                            return enoughResources;
                        }
                    } else {
                        Gdx.app.error("UpgradeDebug", "No upgradeCost found either!");
                    }
                }
            } else {
                Gdx.app.error("UpgradeDebug", "Level key not found: " + levelKey);
            }
        } catch (Exception e) {
            Gdx.app.error("UpgradeDebug", "Error: " + e.getMessage(), e);
        }
        return false;
    }

    public String getUpgradeCostString(Building building) {
        StringBuilder costString = new StringBuilder();
        try {
            String configKey = getConfigKey(building.getType());
            JSONObject config = getNestedConfig(configKey);

            if (config != null) {
                String levelKey = "lvl" + (building.getLevel() + 1);
                if (config.containsKey(levelKey)) {
                    JSONObject levelConfig = (JSONObject) config.get(levelKey);
                    JSONObject cost = getCostObject(levelConfig);

                    if (cost != null) {
                        for (String material : Basics.MATERIALS_NAME) {
                            Object materialCostObj = cost.get(material);
                            if (materialCostObj != null) {
                                int costAmount = parseMaterialCost(materialCostObj);
                                if (costAmount > 0) {
                                    if (costString.length() > 0) costString.append("/");
                                    costString.append(material.substring(0, 1)).append(":").append(costAmount);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("UpgradeCost", "Error getting upgrade cost: " + e.getMessage(), e);
        }
        return costString.toString();
    }

    public void updateBuildingInfoPanel() {
        buildingInfoPanel.updateBuildingInfoPanel();
    }

    public void upgradeSelectedBuilding() {
        if (selectedBuilding != null && canUpgradeBuilding(selectedBuilding)) {
            try {
                String configKey = getConfigKey(selectedBuilding.getType());
                JSONObject config = getNestedConfig(configKey);

                if (config != null) {
                    String levelKey = "lvl" + (selectedBuilding.getLevel() + 1);
                    if (config.containsKey(levelKey)) {
                        JSONObject levelConfig = (JSONObject) config.get(levelKey);
                        JSONObject cost = getCostObject(levelConfig);

                        // اگر cost پیدا نشد، upgradeCost را بررسی کن (مخصوص Town Hall)
                        if (cost == null && levelConfig.containsKey("upgradeCost")) {
                            Object upgradeCostObj = levelConfig.get("upgradeCost");
                            if (upgradeCostObj instanceof JSONObject) {
                                cost = (JSONObject) upgradeCostObj;
                            }
                        }

                        if (cost != null) {
                            Gdx.app.log("UpgradeDebug", "Deducting costs: " + cost.toJSONString());

                            // کسر هزینه‌ها
                            for (String material : Basics.MATERIALS_NAME) {
                                Object materialCostObj = cost.get(material);
                                if (materialCostObj != null) {
                                    int costAmount = parseMaterialCost(materialCostObj);
                                    if (costAmount > 0) {
                                        playerColony.updateResourceAmount(material, -costAmount);
                                        addMessage("-" + costAmount + " " + material + " (upgrade)");
                                        Gdx.app.log("UpgradeDebug", "Deducted " + costAmount + " " + material);
                                    }
                                }
                            }

                            selectedBuilding.upgrade();
                            addMessage(selectedBuilding.getType() + " upgraded to level " + selectedBuilding.getLevel());
                            updateBuildingInfoPanel();
                        } else {
                            Gdx.app.error("Upgrade", "No cost found for upgrade");
                            addMessage("Upgrade failed: No cost configuration found");
                        }
                    }
                }
            } catch (Exception e) {
                Gdx.app.error("Upgrade", "Error upgrading building: " + e.getMessage(), e);
                addMessage("Upgrade failed: " + e.getMessage());
            }
        }
    }

    private boolean canUpgradeBuilding(Building building) {
        return building.getLevel() < building.getMaxLevel() &&
            hasEnoughResourcesForUpgrade(building);
    }

    public void placeBuilding(float x, float y) {
        placementHandler.placeBuilding(x, y);
    }

    public boolean checkAndDeductBuildingCost(String buildingType) {
        return placementHandler.checkAndDeductBuildingCost(buildingType);
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

        // برای backward compatibility
        configKeys.put("iron_mine", "farms.ironMine");
        configKeys.put("stone_mine", "farms.stoneMine");
        configKeys.put("gold_mine", "farms.goldMine");

        String key = configKeys.get(buildingType);
        Gdx.app.log("ConfigKeyDebug", "Building: " + buildingType + ", ConfigKey: " + key);
        return key;
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

                JSONObject current = (JSONObject) SimplerJson.getDataFromJson(Building.configFile, keys[0]);
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

    public int parseMaterialCost(Object costObj) {
        return configManager.parseMaterialCost(costObj);
    }

    public JSONObject getCostObject(JSONObject config) {
        return configManager.getCostObject(config);
    }

    public Building createBuilding(String type, int x, int y) throws Exception {
        return placementHandler.createBuilding(type, x, y);
    }

    public boolean isValidPosition(float x, float y) {
        return placementHandler.isValidPosition(x, y);
    }

    public boolean isBuildable(float worldX, float worldY) {
        return placementHandler.isBuildable(worldX, worldY);
    }

    public boolean isOccupied(float x, float y) {
        return placementHandler.isOccupied(x, y);
    }

    public void renderBuildings() {
        uiManager.renderBuildings(batch);
    }

    public void renderSelectionInfo() {
        uiManager.renderSelectionInfo(batch, font, camera);
    }

    public String getBuildingInfo(String buildingType) {
        return uiManager.getBuildingInfo(buildingType);
    }

    public boolean isClickOnUI(int screenX, int screenY) {
        return uiManager.isClickOnUI(screenX, screenY, buildingInfoPanel);
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

        buildingInfoPanel.positionBuildingInfoPanel();
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

        if (attackTexture != null) attackTexture.dispose();

        if (skin != null) {
            skin.dispose();
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

    // Getter methods for other classes to access private fields
    public List<Building> getBuildings() { return buildings; }
    public String getSelectedBuildingType() { return selectedBuildingType; }
    public void setSelectedBuildingType(String selectedBuildingType) { this.selectedBuildingType = selectedBuildingType; }
    public Building getSelectedBuilding() { return selectedBuilding; }
    public void setSelectedBuilding(Building selectedBuilding) { this.selectedBuilding = selectedBuilding; }
    public Map<String, Texture> getBuildingTextures() { return buildingTextures; }
    public Colony getPlayerColony() { return playerColony; }
    public boolean isTownHallPlaced() { return townHallPlaced; }
    public void setTownHallPlaced(boolean townHallPlaced) { this.townHallPlaced = townHallPlaced; }
    public boolean hasTownHall() { return hasTownHall; }
    public void setHasTownHall(boolean hasTownHall) { this.hasTownHall = hasTownHall; }
    public Stage getUiStage() { return uiStage; }
    public Skin getSkin() { return skin; }
    public TiledMap getMap() { return map; }
    public BuildingInfoPanel getBuildingInfoPanel() { return buildingInfoPanel; }

    public MyGame getGame() {
        return game;
    }
}
