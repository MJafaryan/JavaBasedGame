package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import models.Basics;
import models.user.Colony;
import org.json.simple.JSONObject;
import datastructures.HashMap;
import datastructures.SimplerJson;
import java.util.UUID;

public abstract class Building  {
    protected UUID id;
    protected Texture texture;
    protected Vector2 position;
    protected int width;
    protected int height;
    protected String type;
    protected int health;
    protected Colony colony;
    public static JSONObject configFile;

    static {
        configFile = SimplerJson.readJson(String.format("%sconfigs/building-config.json",
                Basics.DATA_DIR));
    }

    public Building(Texture texture,int x , int y , int width , int height, String type , Colony colony) { // TODO: deploy map logic
        this.id = UUID.randomUUID();
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.type = type;
        this.health = health;
        this.colony = colony;
        this.colony.addBuilding(this);
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

    public void takeDamage(int damage) {
        health -= damage;
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
                    && requiredMaterials.get(material) > this.colony.getMaterial(material)) {
                throw new Exception("No enough " + material);
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null) {
                this.colony.updateResourceAmount(material, requiredMaterials.get(material) * -1);
            }
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }
}
