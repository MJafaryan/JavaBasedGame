package models.buildings;

import models.Basics;
import models.user.Colony;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;

public abstract class Building {
    protected int health;
    protected Colony colony;
    protected static JSONObject configFile;

    static {
        configFile = SimplerJson.readJson(String.format("%sconfigs/building-config.json",
            Basics.DATA_DIR));
    }

    public Building(int health, Colony colony) { // TODO: deploy map logic
        this.health = health;
        this.colony = colony;
    }

    public Building(Colony colony) {
        this.colony = colony;
    }

    public int getHealth() {
        return health;
    }

    public Colony getColony() {
        return colony;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }
}
