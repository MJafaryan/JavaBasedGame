package models.buildings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import models.Basics;
import models.user.Colony;
import org.json.simple.JSONObject;
import datastructures.HashMap;
import datastructures.SimplerJson;
import java.util.UUID;

public abstract class Building  {
    public final class Coordinates {
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
    protected Texture texture;
    protected Coordinates coordinates;
    protected int health;
    protected Colony colony;
    protected static JSONObject configFile;
    protected boolean isAlive;

    static {
        configFile = SimplerJson.readJson("config/building-config.json");
    }

    public Building(Colony colony, Vector2 location, int height, int width, String buildingName) {
        this.id = UUID.randomUUID();
        this.texture = loadTexture(buildingName);
        this.coordinates = new Coordinates(location, height, width);
        this.colony = colony;
        this.isAlive = true;

        colony.addBuilding(this);
    }

    public abstract void destroy();

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

    public void takeDamage(int damage) {
        health -= damage;
    }

    protected void deleteFromBuildings() {
        HashMap<Building> buildingsHashMap = this.colony.getBuildings();
        buildingsHashMap.delete(getID().toString());
        this.colony.setBuildings(buildingsHashMap);
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
