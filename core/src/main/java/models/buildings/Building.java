package models.buildings;

import com.badlogic.gdx.math.Vector2;
import models.Basics;
import models.user.Colony;
import org.json.simple.JSONObject;
import datastructures.HashMap;
import datastructures.SimplerJson;
import java.io.Serializable;
import java.util.UUID;

public abstract class Building implements Serializable {
    public final class Coordinates implements Serializable {
        private Vector2 location;
        private int height;
        private int width;

        public Coordinates(int xAssist, int yAssist, int height, int width) {
            this(new Vector2(xAssist, yAssist), height, width);
        }

        public Coordinates(Vector2 location, int height, int width) {
            this.location = location;
            this.height = height;
            this.width = width;
        }

        public Vector2 getLocation() {
            return this.location;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }
    }

    private UUID id;
    protected String type;
    protected Coordinates coordinates;
    protected int health;
    protected Colony colony;
    protected static JSONObject configFile;
    protected boolean isAlive;
    protected int level;
    private int maxLvl;

    static {
        configFile = SimplerJson.readJson("config/building-config.json");
    }

    public Building(Colony colony, Vector2 location, int height, int width, String buildingName, int maxLvl) {
        this.id = UUID.randomUUID();
        this.type = buildingName;
        this.coordinates = new Coordinates(location, height, width);
        this.colony = colony;
        this.isAlive = true;
        this.level = 1;
        this.maxLvl = maxLvl;

        colony.addBuilding(this);
    }

    public abstract void destroy();

    public void upgrade() throws Exception {

    }

    public int getHealth() {
        return health;
    }

    public Colony getColony() {
        return colony;
    }

    public UUID getID() {
        return this.id;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public String getType() {
        return this.type;
    }

    public static JSONObject getConfigFile() {
        return configFile;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    protected void deleteFromBuildings() {
        HashMap<Building> buildingsHashMap = this.colony.getBuildings();
        buildingsHashMap.delete(getID().toString());
        this.colony.setBuildings(buildingsHashMap);
    }

    public int getLevel() {
        return this.level;
    }

    public int getMaxLevel() {
        return this.maxLvl;
    }

    public void payCost(JSONObject costsJSON) throws Exception {
        HashMap<Integer> requiredMaterials = new HashMap<>();

        for (String material : Basics.MATERIALS_NAME) {
            if (SimplerJson.getDataFromJson(costsJSON, material) != null) {
                requiredMaterials.put(material,
                        (int) (long) SimplerJson.getDataFromJson(costsJSON, material));
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null
                    && requiredMaterials.get(material) > this.colony.getRecourse(material)) {
                throw new Exception("No enough " + material);
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null) {
                this.colony.updateRecourse(material, requiredMaterials.get(material) * -1);
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Building building = (Building) object;
            if (building.getID().equals(this.id)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
