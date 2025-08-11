package models.buildings;

import models.user.Colony;

public abstract class Building {
    protected int health;
    protected Colony colony;

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
