package my.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import datastructures.LinkedList;
import models.buildings.*;
import models.user.Colony;
import models.user.User;
import models.Basics;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;

public class GameScreen implements Screen {
    private final MyGame game;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private SpriteBatch batch;
    private BitmapFont font;

    // Scene2d UI
    private Stage uiStage;
    private Viewport uiViewport;

    private LinkedList buildingList = new LinkedList();
    private String selectedBuildingType = null;

    // Textures for buildings
    private Texture houseTexture, barracksTexture, farmTexture, hospitalTexture,
        stoneTexture, goldTexture, ironTexture, husbandryTexture;

    private Texture whitePixel; // cached 1x1 texture for health bars

    private InputManager inputManager;

    // Player colony
    private Colony playerColony;
    private User playerUser;

    private static final float WORLD_WIDTH = 1000;
    private static final float WORLD_HEIGHT = 1000;

    // UI elements for messages and resources
    private Label resourcesLabel;
    private Label messagesLabel;
    private ScrollPane messagesScrollPane;
    private StringBuilder messagesBuilder = new StringBuilder();
    private float messageTimer = 0;
    private static final float MESSAGE_DISPLAY_TIME = 5f;
    private InputMultiplexer inputMultiplexer;

    public GameScreen(MyGame game) {
        try {
            this.game = game;
            Gdx.app.log("GameScreen", "Initializing...");

            // Initialize player user and colony
            playerUser = new User("Player", "password");
            Gdx.app.log("GameScreen", "User created");

            try {
                playerColony = new Colony("Player Colony", playerUser, "default", 10000, 5000);
                Gdx.app.log("GameScreen", "New colony created");
            } catch (IllegalArgumentException e) {
                playerColony = new Colony(playerUser);
                Gdx.app.log("GameScreen", "Existing colony loaded");
            }

            // Initialize camera and viewport
            camera = new OrthographicCamera();
            viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
            camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
            Gdx.app.log("GameScreen", "Camera initialized");

            // Load map
            try {
                map = new TmxMapLoader().load("Map/Map1/Map1.tmx");
                renderer = new OrthogonalTiledMapRenderer(map);
                Gdx.app.log("GameScreen", "Map loaded successfully");
            } catch (Exception e) {
                Gdx.app.error("GameScreen", "Failed to load map: " + e.getMessage());
                map = new TiledMap();
                renderer = new OrthogonalTiledMapRenderer(map);
            }

            batch = new SpriteBatch();
            font = new BitmapFont();
            font.setColor(Color.WHITE);
            font.getData().setScale(1.2f);

            // Initialize UI stage
            uiViewport = new ScreenViewport();
            uiStage = new Stage(uiViewport, batch);

            // Create info toolbar first
            createInfoToolbar();

            // Initialize input multiplexer (UI first, then game)
            inputMultiplexer = new InputMultiplexer();
            inputMultiplexer.addProcessor(uiStage); // UI should consume first
            inputMultiplexer.addProcessor(new InputAdapter() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    handleGameInput(screenX, screenY, button);
                    return true;
                }
            });
            Gdx.input.setInputProcessor(inputMultiplexer);

            // Load textures with error handling
            try {
                houseTexture = loadTextureSafe("house.png");
                barracksTexture = loadTextureSafe("barracks.png");
                farmTexture = loadTextureSafe("farm.png");
                hospitalTexture = loadTextureSafe("hospital.png");
                stoneTexture = loadTextureSafe("stone.png");
                goldTexture = loadTextureSafe("gold.png");
                ironTexture = loadTextureSafe("iron.png");
                husbandryTexture = loadTextureSafe("husbandry.png");
                Gdx.app.log("GameScreen", "All textures loaded");
            } catch (Exception e) {
                Gdx.app.error("GameScreen", "Failed to load textures: " + e.getMessage());
            }

            // Initialize input manager
            try {
                int tileWidth = map.getProperties().get("tilewidth", Integer.class);
                int tileHeight = map.getProperties().get("tileheight", Integer.class);
                int mapWidthInTiles = map.getProperties().get("width", Integer.class);
                int mapHeightInTiles = map.getProperties().get("height", Integer.class);
                float mapWidth = mapWidthInTiles * tileWidth;
                float mapHeight = mapHeightInTiles * tileHeight;
                inputManager = new InputManager(camera, mapWidth, mapHeight, viewport.getWorldWidth(), viewport.getWorldHeight());
                Gdx.app.log("GameScreen", "Input manager initialized");
            } catch (Exception e) {
                Gdx.app.error("GameScreen", "Failed to initialize input manager: " + e.getMessage());
                inputManager = new InputManager(camera, WORLD_WIDTH, WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
            }

            // Create building toolbar
            createBuildingToolbar();

            // Initialize colony with some resources
            initializeColonyResources();

            // Add welcome message
            addMessage("Game started successfully!");
            addMessage("Resources initialized: Wood=1000, Stone=1000, Gold=500, Iron=500, Food=2000");

            Gdx.app.log("GameScreen", "Game screen initialized successfully");

        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Fatal error in constructor: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize GameScreen", e);
        }
    }

    private void createInfoToolbar() {
        // Resources toolbar at top
        Table resourcesTable = new Table();
        resourcesTable.setFillParent(true);
        resourcesTable.top().left();
        resourcesTable.pad(10);

        // Resources label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;

        resourcesLabel = new Label("", labelStyle);
        resourcesTable.add(resourcesLabel).expandX().left().pad(10);

        // Messages toolbar at top right
        Table messagesTable = new Table();
        messagesTable.setFillParent(true);
        messagesTable.top().right();
        messagesTable.pad(10);

        // Messages label with scroll pane
        messagesLabel = new Label("", labelStyle);
        messagesLabel.setWrap(true);
        messagesLabel.setAlignment(Align.right);

        messagesScrollPane = new ScrollPane(messagesLabel);
        messagesScrollPane.setScrollingDisabled(true, false);
        messagesScrollPane.setFadeScrollBars(false);
        messagesScrollPane.setScrollbarsVisible(true);
        messagesScrollPane.setScrollBarPositions(false, true);

        messagesTable.add(messagesScrollPane).width(300).height(100).pad(10);

        uiStage.addActor(resourcesTable);
        uiStage.addActor(messagesTable);
    }

    private void createBuildingToolbar() {
        Table toolbar = new Table();
        toolbar.setFillParent(true);
        toolbar.bottom();
        toolbar.setDebug(true);

        // Create image buttons
        ImageButton houseButton = createBuildingButton(houseTexture, "house");
        ImageButton barracksButton = createBuildingButton(barracksTexture, "barracks");
        ImageButton farmButton = createBuildingButton(farmTexture, "farm");
        ImageButton hospitalButton = createBuildingButton(hospitalTexture, "hospital");
        ImageButton stoneButton = createBuildingButton(stoneTexture, "stone");
        ImageButton goldButton = createBuildingButton(goldTexture, "gold");
        ImageButton ironButton = createBuildingButton(ironTexture, "iron");
        ImageButton husbandryButton = createBuildingButton(husbandryTexture, "husbandry");

        // Create button row
        Table buttonRow = new Table();
        buttonRow.add(houseButton).size(80, 80).pad(10);
        buttonRow.add(barracksButton).size(80, 80).pad(10);
        buttonRow.add(farmButton).size(80, 80).pad(10);
        buttonRow.add(hospitalButton).size(80, 80).pad(10);
        buttonRow.add(stoneButton).size(80, 80).pad(10);
        buttonRow.add(goldButton).size(80, 80).pad(10);
        buttonRow.add(ironButton).size(80, 80).pad(10);
        buttonRow.add(husbandryButton).size(80, 80).pad(10);

        // Add button row to toolbar
        toolbar.add(buttonRow).padBottom(20);
        uiStage.addActor(toolbar);

        Gdx.app.log("UI", "Building toolbar created");
    }

    private ImageButton createBuildingButton(Texture texture, String buildingType) {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.setColor(1.2f, 1.2f, 1.2f, 1f);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setColor(1f, 1f, 1f, 1f);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedBuildingType != null && selectedBuildingType.equals(buildingType)) {
                    selectedBuildingType = null;
                    addMessage("Building deselected: " + buildingType);
                } else {
                    selectedBuildingType = buildingType;
                    addMessage("Building selected: " + buildingType);
                    addMessage("Cost: " + getBuildingInfo(buildingType));
                }
                // event.stop();
            }
        });

        return button;
    }

    private void addMessage(String message) {
        if (messagesLabel == null) {
            Gdx.app.log("Messages", "messagesLabel is null! Message: " + message);
            return;
        }

        messagesBuilder.append("• ").append(message).append("\n");
        messagesLabel.setText(messagesBuilder.toString());

        if (messagesScrollPane != null) {
            messagesScrollPane.scrollTo(0, 0, 0, 0);
        }

        if (messagesBuilder.length() > 1000) {
            messagesBuilder.delete(0, 200);
        }
    }

    private void updateResourcesDisplay() {
        if (resourcesLabel == null) return;

        String resourcesText = String.format(
            "Wood: %d | Stone: %d | Gold: %d | Iron: %d | Food: %d | Coins: %d | Pop: %d/%d | Storage: %d/%d",
            playerColony.getMaterial("wood"),
            playerColony.getMaterial("stone"),
            playerColony.getMaterial("gold"),
            playerColony.getMaterial("iron"),
            playerColony.getMaterial("food"),
            playerColony.getBalance(),
            playerColony.getPopulation(),
            playerColony.getMaximumPossiblePopulation(),
            playerColony.getUsedCapacity(),
            playerColony.getStorageCapacity()
        );
        resourcesLabel.setText(resourcesText);
    }

    private void updateMessages(float delta) {
        messageTimer += delta;
        if (messageTimer > MESSAGE_DISPLAY_TIME) {
            messageTimer = 0;
        }
    }

    private void handleGameInput(int screenX, int screenY, int button) {
        try {
            // اگر کلیک روی UI بوده، پردازش نکن
            if (isClickOnUI(screenX, screenY)) {
                return;
            }

            Vector2 touchPos = new Vector2(screenX, screenY);
            Vector2 worldPos = viewport.unproject(touchPos.cpy());

            if (selectedBuildingType != null && button == Input.Buttons.LEFT) {
                placeBuilding(worldPos.x, worldPos.y);
            }

            if (button == Input.Buttons.RIGHT) {
                selectedBuildingType = null;
                addMessage("Selection cancelled");
            }

        } catch (Exception e) {
            addMessage("Input error: " + e.getMessage());
        }
    }

    private Texture loadTextureSafe(String path) {
        try {
            Texture texture = new Texture(Gdx.files.internal(path));
            Gdx.app.log("Texture", "Loaded: " + path);
            return texture;
        } catch (Exception e) {
            Gdx.app.error("Texture", "Failed to load texture: " + path + " - " + e.getMessage());
            Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.MAGENTA);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            return texture;
        }
    }

    private void initializeColonyResources() {
        String[] resourcesToUpdate = {"wood", "stone", "gold", "iron", "food"};
        int[] amounts = {1000, 1000, 500, 500, 2000};

        for (int i = 0; i < resourcesToUpdate.length; i++) {
            try {
                playerColony.updateResourceAmount(resourcesToUpdate[i], amounts[i]);
                Gdx.app.log("Resource", "Updated " + resourcesToUpdate[i] + " to " + amounts[i]);
            } catch (Exception e) {
                Gdx.app.error("Resource", "FAILED to update " + resourcesToUpdate[i] + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void render(float delta) {
        try {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            inputManager.update();

            camera.update();
            renderer.setView(camera);
            renderer.render();

            updateResourcesDisplay();
            updateMessages(delta);

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            renderBuildings();
            renderSelectionInfo();
            batch.end();

            uiStage.act(delta);
            uiStage.draw();

        } catch (Exception e) {
            addMessage("Render error: " + e.getMessage());
        }
    }

    private void placeBuilding(float x, float y) {
        addMessage("Attempting to place " + selectedBuildingType + " at " + (int)x + ", " + (int)y);

        if (!isValidPosition(x, y)) {
            addMessage("Cannot build here - invalid position!");
            return;
        }

        try {
            Building newBuilding = createBuilding(selectedBuildingType, (int)x, (int)y);
            buildingList.addNode(newBuilding);

            deductBuildingCost(selectedBuildingType);

            addMessage(selectedBuildingType + " built successfully!");
            addMessage("Resources deducted for construction");

        } catch (Exception e) {
            addMessage("Failed to build: " + e.getMessage());
        }

        selectedBuildingType = null;
    }

    private void deductBuildingCost(String buildingType) {
        try {
            String configKey = getConfigKey(buildingType);
            JSONObject config = (JSONObject) SimplerJson.getDataFromJson(Building.configFile, configKey);

            if (config != null) {
                JSONObject cost = (JSONObject) SimplerJson.getDataFromJson(config, "lvl1_cost");
                if (cost == null) {
                    cost = (JSONObject) SimplerJson.getDataFromJson(config, "cost");
                }

                if (cost != null && Basics.MATERIALS_NAME != null) {
                    for (String material : Basics.MATERIALS_NAME) {
                        Object materialCost = SimplerJson.getDataFromJson(cost, material);
                        if (materialCost != null && materialCost instanceof Long) {
                            int costAmount = ((Long) materialCost).intValue();
                            if (costAmount > 0) {
                                int currentAmount = playerColony.getMaterial(material);
                                playerColony.updateResourceAmount(material, currentAmount - costAmount);
                                addMessage("-" + costAmount + " " + material);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            addMessage("Error deducting resources: " + e.getMessage());
        }
    }

    private Building createBuilding(String type, int x, int y) throws Exception {
        Texture texture = getTextureForBuilding(type);
        int width = 64;
        int height = 64;

        switch (type) {
            case "house": return new House(texture, x, y, width, height, "house", playerColony);
            case "barracks": return new Barracks(texture, x, y, width, height, "barracks", playerColony);
            case "farm": return new Farm(texture, x, y, width, height, "farm", playerColony);
            case "hospital": return new Hospital(texture, x, y, width, height, "hospital", playerColony);
            case "stone": return new StoneMine(texture, x, y, width, height, "stone_mine", playerColony);
            case "gold": return new GoldMine(texture, x, y, width, height, "gold_mine", playerColony);
            case "iron": return new IronMine(texture, x, y, width, height, "iron_mine", playerColony);
            case "husbandry": return new AnimalHusbandry(texture, x, y, width, height, "animal_husbandry", playerColony);
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

            if (tileX < 0 || tileX >= mapWidthInTiles || tileY < 0 || tileY >= mapHeightInTiles) {
                return false;
            }

            TiledMapTileLayer obstacleLayer = (TiledMapTileLayer) map.getLayers().get("mountain and tree");
            if (obstacleLayer != null) {
                TiledMapTileLayer.Cell cell = obstacleLayer.getCell(tileX, tileY);
                // Buildable only if there's NO obstacle tile on this cell
                return cell == null || cell.getTile() == null;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean isOccupied(float x, float y) {
        for (int i = 0; i < buildingList.getLength(); i++) {
            Object obj = buildingList.getNode(i);
            if (obj instanceof Building) {
                Building building = (Building) obj;
                Vector2 pos = building.getPosition();
                if (Math.abs(pos.x - x) < 64 && Math.abs(pos.y - y) < 64) {
                    return true;
                }
            }
        }
        return false;
    }

    private void renderBuildings() {
        for (int i = 0; i < buildingList.getLength(); i++) {
            try {
                Object obj = buildingList.getNode(i);
                if (obj instanceof Building) {
                    Building building = (Building) obj;
                    Texture texture = building.getTexture();
                    Vector2 position = building.getPosition();
                    batch.draw(texture, position.x, position.y, texture.getWidth(), texture.getHeight());
                    renderHealthBar(building);
                }
            } catch (Exception e) {
                Gdx.app.error("RenderBuildings", "Error rendering building: " + e.getMessage());
            }
        }
    }

    private Texture getWhitePixel() {
        if (whitePixel == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            whitePixel = new Texture(pixmap);
            pixmap.dispose();
        }
        return whitePixel;
    }

    private void renderHealthBar(Building building) {
        try {
            float healthPercentage = (float) building.getHealth() / 100f;
            float barWidth = 50f;
            float barHeight = 5f;
            Vector2 position = building.getPosition();
            Texture texture = building.getTexture();

            float x = position.x + (texture.getWidth() - barWidth) / 2f;
            float y = position.y + texture.getHeight() + 5f;

            batch.setColor(Color.RED);
            batch.draw(getWhitePixel(), x, y, barWidth, barHeight);
            batch.setColor(Color.GREEN);
            batch.draw(getWhitePixel(), x, y, barWidth * healthPercentage, barHeight);
            batch.setColor(Color.WHITE);
        } catch (Exception e) {
            Gdx.app.error("RenderHealthBar", "Error rendering health bar: " + e.getMessage());
        }
    }

    private void renderSelectionInfo() {
        try {
            if (selectedBuildingType != null) {
                Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                Vector2 worldPos = viewport.unproject(mousePos.cpy());

                Texture texture = getTextureForBuilding(selectedBuildingType);
                if (texture != null) {
                    boolean canBuild = isValidPosition(worldPos.x, worldPos.y);
                    batch.setColor(1, 1, 1, canBuild ? 0.7f : 0.3f);
                    batch.draw(texture, worldPos.x - texture.getWidth() / 2f, worldPos.y - texture.getHeight() / 2f);
                    batch.setColor(1, 1, 1, 1);
                }
            }
        } catch (Exception e) {
            addMessage("Selection render error: " + e.getMessage());
        }
    }


    private String getBuildingInfo(String buildingType) {
        try {
            String configKey = getConfigKey(buildingType);
            JSONObject config = (JSONObject) SimplerJson.getDataFromJson(Building.configFile, configKey);


            if (config == null) return "Config not found";


            JSONObject cost = null;


// اگر ساختمان چند سطحی باشد
            if (config.containsKey("lvl1")) {
                JSONObject lvl1 = (JSONObject) SimplerJson.getDataFromJson(config, "lvl1");
                if (lvl1 != null) {
                    cost = (JSONObject) SimplerJson.getDataFromJson(lvl1, "cost");
                }
            } else if (config.containsKey("cost")) {
// اگر ساختمان ساده باشد
                cost = (JSONObject) SimplerJson.getDataFromJson(config, "cost");
            }


            if (cost == null) return "No cost information";


            StringBuilder info = new StringBuilder();
            if (Basics.MATERIALS_NAME != null) {
                for (String material : Basics.MATERIALS_NAME) {
                    Object materialCost = SimplerJson.getDataFromJson(cost, material);
                    if (materialCost != null) {
                        info.append(material).append(": ").append(materialCost).append(" ");
                    }
                }
            }
            return info.toString().isEmpty() ? "Cost info unavailable" : info.toString();
        } catch (Exception e) {
            return "Error loading cost";
        }
    }

    private boolean isClickOnUI(int screenX, int screenY) {
        // مختصات صفحه را به مختصات UI stage تبدیل کن
        Vector2 stageCoords = uiStage.screenToStageCoordinates(new Vector2(screenX, screenY));

        // بررسی کن که آیا کلیک روی یکی از actorهای UI بوده
        Actor hit = uiStage.hit(stageCoords.x, stageCoords.y, true);
        return hit != null;
    }

    private String getConfigKey(String buildingType) {
        switch (buildingType) {
            case "house": return "house";
            case "barracks": return "barracks";
            case "farm": return "farms.farm";
            case "hospital": return "hospital";
            case "stone": return "farms.stoneMine";
            case "gold": return "farms.goldMine";
            case "iron": return "farms.ironMine";
            case "husbandry": return "farms.animalHusbandry";
            default: return buildingType;
        }
    }

    private Texture getTextureForBuilding(String type) {
        switch (type) {
            case "house": return houseTexture;
            case "barracks": return barracksTexture;
            case "farm": return farmTexture;
            case "hospital": return hospitalTexture;
            case "stone": return stoneTexture;
            case "gold": return goldTexture;
            case "iron": return ironTexture;
            case "husbandry": return husbandryTexture;
            default: return null;
        }
    }

    @Override
    public void resize(int width, int height) {
        try {
            viewport.update(width, height, true);
            uiViewport.update(width, height, true);
            addMessage("Screen resized to: " + width + "x" + height);
        } catch (Exception e) {
            addMessage("Resize error: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        try {
            map.dispose();
            renderer.dispose();
            batch.dispose();
            font.dispose();
            uiStage.dispose();

            if (whitePixel != null) whitePixel.dispose();

            if (houseTexture != null) houseTexture.dispose();
            if (barracksTexture != null) barracksTexture.dispose();
            if (farmTexture != null) farmTexture.dispose();
            if (hospitalTexture != null) hospitalTexture.dispose();
            if (stoneTexture != null) stoneTexture.dispose();
            if (goldTexture != null) goldTexture.dispose();
            if (ironTexture != null) ironTexture.dispose();
            if (husbandryTexture != null) husbandryTexture.dispose();

            // Do NOT dispose building textures individually: they are shared

            playerColony.save();
            addMessage("Game saved on dispose");

        } catch (Exception e) {
            addMessage("Dispose error: " + e.getMessage());
        }
    }

    @Override
    public void pause() {
        try {
            playerColony.save();
            addMessage("Game saved on pause");
        } catch (Exception e) {
            addMessage("Pause save error: " + e.getMessage());
        }
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
        try {
            playerColony.save();
            addMessage("Game saved on hide");
        } catch (Exception e) {
            addMessage("Hide save error: " + e.getMessage());
        }
    }
}
