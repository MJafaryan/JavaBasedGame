package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import datastructures.HashMap;
import models.Basics;
import models.buildings.Building;
import org.json.simple.JSONObject;

public class BuildingUIManager {
    private GameScreen gameScreen;
    private static HashMap<Texture> textures;

    static {
        textures = new HashMap<>();
        for (String buildingName : Basics.BUILDINGS_NAME) {
            textures.put(buildingName, loadTexture(buildingName));
        }
    }

    public BuildingUIManager(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    private static Texture loadTexture(String buildingName) {
        String path = String.format("texture/%s-texture.png", buildingName.toLowerCase());
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

    public void createBuildingToolbar() {
        Table toolbar = new Table();
        toolbar.setFillParent(true);
        toolbar.bottom().left().pad(10);

        Table buttonRow = new Table();
        for (String buildingType : gameScreen.getBuildingTextures().keySet()) {
            if (!gameScreen.isTownHallPlaced()) {
                if (buildingType.equals("townHall")) {
                    ImageButton button = createBuildingButton(gameScreen.getBuildingTextures().get(buildingType),
                            buildingType);
                    buttonRow.add(button).size(80, 80).pad(5);
                }
                continue;
            }

            if (!buildingType.equals("townHall")) {
                ImageButton button = createBuildingButton(gameScreen.getBuildingTextures().get(buildingType),
                        buildingType);
                buttonRow.add(button).size(80, 80).pad(5);
            }
        }

        toolbar.add(buttonRow).padBottom(10);
        gameScreen.getUiStage().addActor(toolbar);
    }

    public ImageButton createBuildingButton(Texture texture, String buildingType) {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!gameScreen.isTownHallPlaced() && !buildingType.equals("townHall")) {
                    gameScreen.addMessage("You must place Town Hall first!");
                    return;
                }

                String selectedBuildingType = gameScreen.getSelectedBuildingType();
                gameScreen.setSelectedBuildingType(
                        selectedBuildingType != null && selectedBuildingType.equals(buildingType) ? null
                                : buildingType);
                gameScreen.addMessage(
                        gameScreen.getSelectedBuildingType() == null ? "Building deselected: " + buildingType
                                : "Building selected: " + buildingType + " | Cost: " + getBuildingInfo(buildingType));
            }
        });
        return button;
    }

    public void renderBuildings(SpriteBatch batch) {
        for (Building building : gameScreen.getBuildings()) {
            try {
                Texture texture = textures.get(building.getType());
                Vector2 position = building.getCoordinates().getLocation();
                if (texture != null) {
                    if (building == gameScreen.getSelectedBuilding()) {
                        batch.setColor(0.5f, 1, 0.5f, 1);
                    }

                    batch.draw(texture, position.x, position.y, texture.getWidth(), texture.getHeight());

                    if (building == gameScreen.getSelectedBuilding()) {
                        batch.setColor(1, 1, 1, 1);
                    }
                }
            } catch (Exception e) {
                Gdx.app.error("RenderBuildings", "Error rendering building: " + e.getMessage(), e);
            }
        }
    }

    public void renderSelectionInfo(SpriteBatch batch, BitmapFont font, Camera camera) {
        if (gameScreen.getSelectedBuilding() != null)
            return;
        if (gameScreen.getSelectedBuildingType() == null && gameScreen.isTownHallPlaced())
            return;
        if (!gameScreen.isTownHallPlaced())
            gameScreen.setSelectedBuildingType("townHall");

        try {
            Vector3 screenPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 worldPos = camera.unproject(screenPos.cpy());

            Texture texture = gameScreen.getBuildingTextures().get(gameScreen.getSelectedBuildingType());

            if (texture != null) {
                float drawX = (int) (worldPos.x / 64) * 64;
                float drawY = (int) (worldPos.y / 64) * 64;

                boolean canBuild = gameScreen.isValidPosition(drawX, drawY);
                batch.setColor(1, 1, 1, canBuild ? 0.7f : 0.3f);
                batch.draw(texture, drawX, drawY, texture.getWidth(), texture.getHeight());
                batch.setColor(1, 1, 1, 1);

                if (!gameScreen.isTownHallPlaced()) {
                    font.draw(batch, "FREE Town Hall Placement", drawX, drawY - 10);
                }
            }
        } catch (Exception e) {
            gameScreen.addMessage("Selection render error: " + e.getMessage());
        }
    }

    public String getBuildingInfo(String buildingType) {
        if (buildingType.equals("townHall") && !gameScreen.isTownHallPlaced()) {
            return "FREE (First building)";
        }

        try {
            JSONObject config = gameScreen.getNestedConfig(gameScreen.getConfigKey(buildingType));
            if (config == null)
                return "Config not found";

            JSONObject cost = gameScreen.getCostObject(config);
            if (cost == null) {
                if (config.containsKey("lvl1")) {
                    Object lvl1Obj = config.get("lvl1");
                    if (lvl1Obj instanceof JSONObject) {
                        JSONObject lvl1Config = (JSONObject) lvl1Obj;
                        cost = gameScreen.getCostObject(lvl1Config);
                    }
                }
                if (cost == null)
                    return "No cost information";
            }

            StringBuilder info = new StringBuilder();
            for (String material : Basics.MATERIALS_NAME) {
                Object materialCost = cost.get(material);
                if (materialCost != null) {
                    int costAmount = gameScreen.parseMaterialCost(materialCost);
                    if (costAmount > 0)
                        info.append(material).append(": ").append(costAmount).append(" ");
                }
            }
            return info.toString().isEmpty() ? "Cost info unavailable" : info.toString();
        } catch (Exception e) {
            return "Error loading cost";
        }
    }

    public boolean isClickOnUI(int screenX, int screenY, BuildingInfoPanel buildingInfoPanel) {
        Vector2 stageCoords = gameScreen.getUiStage().screenToStageCoordinates(new Vector2(screenX, screenY));

        if (buildingInfoPanel.isBuildingInfoPanelVisible()
                && buildingInfoPanel.getBuildingInfoPanel().getParent() != null) {
            if (buildingInfoPanel.getBuildingInfoPanel().hit(stageCoords.x, stageCoords.y, true) != null) {
                return true;
            }
        }

        return gameScreen.getUiStage().hit(stageCoords.x, stageCoords.y, true) != null;
    }
}
