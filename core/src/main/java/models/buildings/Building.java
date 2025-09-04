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
    protected int level = 1;
    protected int maxLevel = 1;
    public static JSONObject configFile;

    static {
        configFile = SimplerJson.readJson(String.format("building-config.json",
                Basics.DATA_DIR));
    }

    public Building(Texture texture,int x , int y , int width , int height, String type , Colony colony) { // TODO: deploy map logic
        this.id = UUID.randomUUID();
        this.texture = texture;
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

    public String getType() { return this.type; }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }

    public int getLevel() { return level; }

    public int getMaxLevel() {return  maxLevel; }

    public void upgrade() throws Exception {
        if (level >= maxLevel) {
            throw new Exception("Building is already at maximum level");
        }
        level++;
    }
}
