package my.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

// import datastructures.HashMap;
import java.util.HashMap;
import datastructures.SimplerJson;
import models.Basics;
import models.buildings.Building;
import models.buildings.Market;
// import models.buildings.Upgradable;
import models.user.Colony;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import static models.user.Colony.getAllPlayers;

public class BuildingInfoPanel {
    private Table buildingInfoPanel;
    private Table pauseInfoPanel;
    private boolean buildingInfoPanelVisible = false;
    private boolean pauseInfoPanelVisible = false;
    private Skin skin;
    private Stage uiStage;
    private GameScreen gameScreen;
    private Market market;

    private Label nameValueLabel;
    private Label levelValueLabel;
    private Label typeValueLabel;
    private TextButton removeButton;
    private TextButton upgradeButton;

    private Table marketTable;
    private Table hospitalTable;
    private Table barrackTable;
    private Table attackInfoPanel;
    private boolean attackInfoPanelVisible;
    private Colony playerColony;

    ////////////////////
    private BuildingConfigManager configManager;

    public BuildingInfoPanel(Skin skin, Stage uiStage, GameScreen gameScreen) {
        this.skin = skin;
        this.uiStage = uiStage;
        this.gameScreen = gameScreen;
        this.playerColony = gameScreen.getPlayerColony(); // اضافه کردن این خط
        createBuildingInfoPanel();
        createPauseInfoPanel();
        createAttackInfoPanel();
    }

    public void createAttackInfoPanel() {
        attackInfoPanel = new Table();
        attackInfoPanel.setVisible(false);
        attackInfoPanel.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.9f)));
        attackInfoPanel.defaults().pad(5).fillX();

        // عنوان پنل
        Label titleLabel = new Label("Select Player to Attack", skin);
        titleLabel.setColor(Color.RED);
        titleLabel.setFontScale(1.5f);
        attackInfoPanel.add(titleLabel).colspan(2).row();

        // جداکننده
        Label separator = new Label("----------------------", skin);
        separator.setFontScale(1.2f);
        attackInfoPanel.add(separator).colspan(2).row();

        // دریافت لیست بازیکنان از کلاس Colony
        List<String> players = Colony.getAllPlayers(); // استفاده از متد کلاس Colony
        String currentPlayer = playerColony.getLeader().getUsername();

        // اگر بازیکنی وجود ندارد
        if (players.isEmpty()) {
            Label noPlayersLabel = new Label("No other players found", skin);
            noPlayersLabel.setFontScale(1.2f);
            attackInfoPanel.add(noPlayersLabel).colspan(2).pad(10).row();
        } else {
            // ایجاد دکمه برای هر بازیکن
            for (int i = 0; i < players.size(); i++) {
                String playerName = players.get(i);

                // نمایش بازیکنان به جز بازیکن فعلی
                if (!playerName.equals(currentPlayer)) {
                    TextButton playerButton = new TextButton(playerName, skin);
                    playerButton.getLabel().setFontScale(1.1f);

                    // اضافه کردن listener برای هر دکمه بازیکن
                    final String targetPlayer = playerName; // final برای استفاده در listener
                    playerButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            initiateAttack(targetPlayer);
                        }
                    });

                    attackInfoPanel.add(playerButton).width(280).height(35).pad(2).row();
                }
            }

            // اگر فقط بازیکن فعلی وجود دارد
            if (players.size() == 1 && players.get(0).equals(currentPlayer)) {
                Label onlyPlayerLabel = new Label("You are the only player", skin);
                onlyPlayerLabel.setFontScale(1.2f);
                attackInfoPanel.add(onlyPlayerLabel).colspan(2).pad(10).row();
            }
        }

        // جداکننده پایین
        Label separator2 = new Label("----------------------", skin);
        separator2.setFontScale(1.2f);
        attackInfoPanel.add(separator2).colspan(2).row();

        // دکمه بستن
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.getLabel().setFontScale(1.2f);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setAttackInfoPanelVisible(false);
            }
        });

        attackInfoPanel.add(closeButton).colspan(2).padTop(10);

        // اضافه کردن پنل به stage
        uiStage.addActor(attackInfoPanel);
        positionAttackInfoPanel();
    }

    private void positionAttackInfoPanel() {
        if (attackInfoPanel != null && attackInfoPanelVisible) {
            float panelWidth = 320;
            float panelHeight = 400;
            float stageWidth = uiStage.getWidth();
            float stageHeight = uiStage.getHeight();

            attackInfoPanel.setSize(panelWidth, panelHeight);
            attackInfoPanel.setPosition((stageWidth - panelWidth) / 2, (stageHeight - panelHeight) / 2);
        }
    }

    private void initiateAttack(String playerName) {
        gameScreen.addMessage("Preparing to attack: " + playerName);

        // در اینجا منطق حمله به بازیکن انتخاب شده پیاده‌سازی می‌شود
        // gameScreen.initiateCombat(playerName);

        setAttackInfoPanelVisible(false);
    }

    private List<String> getAllPlayers() {
        List<String> players = new ArrayList<>();
        File saveDir = new File("data/saves/"); // مطمئن شوید این مسیر درست است

        // بررسی وجود پوشه saves
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            System.out.println("Save directory not found: " + saveDir.getAbsolutePath());
            return players;
        }

        // خواندن تمام فایل‌های .bin در پوشه saves
        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".bin"));

        if (files != null) {
            for (File file : files) {
                // استخراج نام کاربری از نام فایل
                String filename = file.getName();
                String username = filename.substring(0, filename.length() - 4); // حذف .bin
                players.add(username);
            }
        }

        System.out.println("Found players: " + players);
        return players;
    }

    public void setAttackInfoPanelVisible(boolean visible) {
        attackInfoPanelVisible = visible;
        if (attackInfoPanel != null) {
            attackInfoPanel.setVisible(visible);
            if (visible) {
                positionAttackInfoPanel(); // موقعیت‌دهی هنگام نمایش
            }
        }
    }

    public void createPauseInfoPanel() {
        pauseInfoPanel = new Table();
        pauseInfoPanel.setVisible(false);
        pauseInfoPanel.setFillParent(true);
        pauseInfoPanel.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.9f)));
        pauseInfoPanel.center();

        VerticalGroup content = new VerticalGroup();
        content.space(20);
        content.center();

        Label titleLabel = new Label("GAME PAUSED", skin);
        titleLabel.setColor(Color.GOLD);
        titleLabel.setFontScale(2.5f);

        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.getLabel().setFontScale(1.5f);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setPauseInfoPanelVisible(false);
            }
        });

        TextButton exitButton = new TextButton("Exit to Menu", skin);
        exitButton.getLabel().setFontScale(1.5f);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameScreen.getGame().setScreen(new MenuScreen(gameScreen.getGame()));
            }
        });

        content.addActor(titleLabel);
        content.addActor(resumeButton);
        content.addActor(exitButton);

        pauseInfoPanel.add(content);
        uiStage.addActor(pauseInfoPanel);
    }

    public void setPauseInfoPanelVisible(boolean visible) {
        pauseInfoPanelVisible = visible;
        if (pauseInfoPanel != null) {
            pauseInfoPanel.setVisible(visible);
        }
    }

    public boolean isPauseInfoPanelVisible() {
        return pauseInfoPanelVisible;
    }

    public void createBuildingInfoPanel() {
        buildingInfoPanel = new Table();
        buildingInfoPanel.setVisible(false);
        buildingInfoPanel.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.9f)));
        buildingInfoPanel.defaults().pad(3).fillX();

        Label titleLabel = new Label("Building Info", skin);
        titleLabel.setColor(Color.GOLD);
        titleLabel.setFontScale(1.5f);
        buildingInfoPanel.add(titleLabel).colspan(2).row();

        Label separator = new Label("----------------------", skin);
        separator.setFontScale(1.5f);
        buildingInfoPanel.add(separator).colspan(2).row();

        Table infoTable = new Table();
        infoTable.defaults().pad(2).left();

        Label nameLabel = new Label("Name: ", skin);
        nameLabel.setFontScale(1.5f);
        nameValueLabel = new Label("", skin);
        nameValueLabel.setFontScale(1.5f);

        Label levelLabel = new Label("Level: ", skin);
        levelLabel.setFontScale(1.5f);
        levelValueLabel = new Label("", skin);
        levelValueLabel.setFontScale(1.5f);

        Label typeLabel = new Label("Type: ", skin);
        typeLabel.setFontScale(1.5f);
        typeValueLabel = new Label("", skin);
        typeValueLabel.setFontScale(1.5f);

        infoTable.add(nameLabel).left();
        infoTable.add(nameValueLabel).left().row();
        infoTable.add(levelLabel).left();
        infoTable.add(levelValueLabel).left().row();
        infoTable.add(typeLabel).left();
        infoTable.add(typeValueLabel).left();

        buildingInfoPanel.add(infoTable).colspan(2).row();

        Label separator2 = new Label("----------------------", skin);
        separator2.setFontScale(1.5f);
        buildingInfoPanel.add(separator2).colspan(2).row();

        removeButton = new TextButton("Remove", skin);
        upgradeButton = new TextButton("Upgrade", skin);

        removeButton.getLabel().setFontScale(1f);
        upgradeButton.getLabel().setFontScale(1f);

        removeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeSelectedBuilding();
            }
        });

        upgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                upgradeSelectedBuilding();
            }
        });

        Table buttonTable = new Table();
        buttonTable.defaults().pad(2).width(200).height(30);
        buttonTable.add(removeButton).row();
        buttonTable.add(upgradeButton);

        buildingInfoPanel.add(buttonTable).colspan(2).padTop(5);

        uiStage.addActor(buildingInfoPanel);
    }

    public void updateBuildingInfoPanel() {
        Building selectedBuilding = gameScreen.getSelectedBuilding();
        if (selectedBuilding == null) {
            setBuildingInfoPanelVisible(false);
            return;
        }

        setBuildingInfoPanelVisible(true);

        nameValueLabel.setText(selectedBuilding.getType());
        levelValueLabel.setText(String.valueOf(selectedBuilding.getLevel()));
        typeValueLabel.setText(selectedBuilding.getType());

        boolean canUpgrade = canUpgradeBuilding(selectedBuilding);
        upgradeButton.setDisabled(!canUpgrade);
        upgradeButton.setText(canUpgrade ? "Upgrade (" + getUpgradeCostString(selectedBuilding) + ")" : "Max Level");

        // حذف marketTable قبلی اگر وجود دارد
        if (marketTable != null) {
            buildingInfoPanel.removeActor(marketTable);
            marketTable = null;
        }
        // حذف hospitalTable قبلی اگر وجود دارد
        if (hospitalTable != null) {
            buildingInfoPanel.removeActor(hospitalTable);
            hospitalTable = null;
        }
        if (barrackTable != null) {
            buildingInfoPanel.removeActor(barrackTable);
            barrackTable = null;
        }

        if (selectedBuilding.getType().equals("hospital")) {
            hospitalTable = new Table();
            hospitalTable.defaults().pad(2).pad(2);

            TextButton heroTreatmentBtn = new TextButton("Hero Treatment", skin);
            heroTreatmentBtn.getLabel().setFontScale(1f);

            hospitalTable.add(heroTreatmentBtn).colspan(2).width(200).height(30).center().row();
            buildingInfoPanel.add(hospitalTable).colspan(2).row();
        }

        if (barrackTable != null) {
            buildingInfoPanel.removeActor(barrackTable);
            barrackTable = null;
        }

        if (selectedBuilding.getType().equals("barracks")) {
            barrackTable = new Table();
            barrackTable.defaults().pad(5).width(100).height(30);

            Label nameLabel = new Label("Soldier Training:", skin);
            nameLabel.setFontScale(1.2f);
            barrackTable.add(nameLabel).colspan(2).center().padBottom(10).row();

            String[] soldiers = { "Infantry", "Archer", "Cavalry", "Wizard", "spy" };
            for (int i = 0; i < soldiers.length; i++) {
                TextButton soldierBtn = new TextButton(soldiers[i], skin);
                soldierBtn.getLabel().setFontScale(1f);
                barrackTable.add(soldierBtn).width(100).height(30).pad(3);

                if (i % 2 == 1) {
                    barrackTable.row();
                }
            }

            buildingInfoPanel.add(barrackTable).colspan(2).padTop(10).row();
        }

        // اگر ساختمان بازار است، دکمه‌های خرید و فروش را اضافه کن
        if (selectedBuilding.getType().equals("market")) {
            marketTable = new Table();
            marketTable.defaults().pad(2).width(70).height(25);

            Label buyLabel = new Label("Buy:", skin);
            buyLabel.setFontScale(1.2f);
            marketTable.add(buyLabel).colspan(2).center().row();

            String[] resources = { "food", "wood", "stone", "iron" };
            TextButton[] buyBtns = new TextButton[resources.length];

            for (int i = 0; i < resources.length; i++) {
                buyBtns[i] = new TextButton(resources[i], skin);
                buyBtns[i].getLabel().setFontScale(1f);
                marketTable.add(buyBtns[i]); // اینجا تصحیح شد

                // اضافه کردن listener برای هر دکمه
                final int index = i; // برای استفاده در listener
                buyBtns[i].addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        try {
                            if (selectedBuilding instanceof Market) {
                                Market market = (Market) selectedBuilding;
                                market.buyProduct(resources[index]);
                                gameScreen.addMessage("Buy " + resources[index] + " successful");
                            }
                        } catch (Exception e) {
                            gameScreen.addMessage("Error buying " + resources[index]);
                            System.out.println("Error buying " + resources[index] + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

                if (i % 2 == 1)
                    marketTable.row();
            }

            marketTable.row();
            Label sellLabel = new Label("Sell:", skin);
            sellLabel.setFontScale(1.2f);
            marketTable.add(sellLabel).colspan(2).center().row();

            TextButton[] sellBtns = new TextButton[resources.length];
            for (int i = 0; i < resources.length; i++) {
                sellBtns[i] = new TextButton(resources[i], skin);
                sellBtns[i].getLabel().setFontScale(1f);
                marketTable.add(sellBtns[i]); // اینجا تصحیح شد

                // اضافه کردن listener برای هر دکمه
                final int index = i; // برای استفاده در listener
                sellBtns[i].addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        try {
                            if (selectedBuilding instanceof Market) {
                                Market market = (Market) selectedBuilding;
                                market.sellProduct(resources[index]);
                                gameScreen.addMessage("Sell " + resources[index] + " successful");
                            }
                        } catch (Exception e) {
                            gameScreen.addMessage("Error selling " + resources[index]);
                            System.out.println("Error selling " + resources[index] + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

                if (i % 2 == 1)
                    marketTable.row();
            }

            buildingInfoPanel.add(marketTable).colspan(2).pad(10).row();
        }

        positionBuildingInfoPanel();
    }

    public void positionBuildingInfoPanel() {
        if (buildingInfoPanel != null && buildingInfoPanelVisible) {
            float panelWidth = 250;
            float panelHeight = 600;
            float stageWidth = uiStage.getWidth();
            float stageHeight = uiStage.getHeight();

            buildingInfoPanel.setSize(panelWidth, panelHeight);
            buildingInfoPanel.setPosition(stageWidth - panelWidth - 20, (stageHeight - panelHeight) / 2);
        }
    }

    public void setBuildingInfoPanelVisible(boolean visible) {
        buildingInfoPanelVisible = visible;
        if (buildingInfoPanel != null) {
            buildingInfoPanel.setVisible(visible);
        }
    }

    public void setAttackInfoPanel(boolean visible) {
        attackInfoPanelVisible = visible;
        if (attackInfoPanel != null) {
            attackInfoPanel.setVisible(visible);
        }
    }

    /////////

    public int parseMaterialCost(Object costObj) {
        return configManager.parseMaterialCost(costObj);
    }

    public JSONObject getCostObject(JSONObject config) {
        return configManager.getCostObject(config);
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
                JSONObject buildingConfig = (JSONObject) SimplerJson.getDataFromJson(Building.getConfigFile(),
                        configKey);

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
                            int available = playerColony.getRecourse(material);
                            Gdx.app.log("UpgradeDebug",
                                    material + " - Cost: " + costAmount + ", Available: " + available);

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
                                    int available = playerColony.getRecourse(material);
                                    Gdx.app.log("UpgradeDebug",
                                            material + " - UpgradeCost: " + costAmount + ", Available: " + available);

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

    private boolean canUpgradeBuilding(Building building) {
        return building.getLevel() < building.getMaxLevel() &&
                hasEnoughResourcesForUpgrade(building);
    }
    // private boolean canUpgradeBuilding(Building building) {
    // if (!(building instanceof Upgradable))
    // return false;
    // Upgradable upgradableBuilding = (Upgradable) building;
    // if (upgradableBuilding.getLevel() < upgradableBuilding.getMaxLevel())
    // return true;
    // return false;
    // }

    private String getUpgradeCostString(Building building) {
        return gameScreen.getUpgradeCostString(building);
    }

    private void removeSelectedBuilding() {
        Building selectedBuilding = gameScreen.getSelectedBuilding();
        if (selectedBuilding != null) {
            gameScreen.getBuildings().remove(selectedBuilding);

            if (selectedBuilding.getType().equals("townHall")) {
                gameScreen.setTownHallPlaced(false);
                gameScreen.setHasTownHall(false);
                gameScreen.createBuildingToolbar();
                gameScreen.addMessage("Town Hall removed! You must place a new Town Hall.");
            }

            gameScreen.addMessage(selectedBuilding.getType() + " removed.");
            gameScreen.setSelectedBuilding(null);
            setBuildingInfoPanelVisible(false);
        }
    }

    private void upgradeSelectedBuilding() {
        Building selectedBuilding = gameScreen.getSelectedBuilding();
        if (selectedBuilding != null && canUpgradeBuilding(selectedBuilding)) {
            gameScreen.upgradeSelectedBuilding();
            updateBuildingInfoPanel();
        }
    }

    public boolean isAttackInfoPanelVisible() {
        return attackInfoPanelVisible;
    }

    public boolean isBuildingInfoPanelVisible() {
        return buildingInfoPanelVisible;
    }

    public Table getBuildingInfoPanel() {
        return buildingInfoPanel;
    }
}
