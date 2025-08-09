package models.buildings;

import java.io.File;

public abstract class Building {
    private int health;
    private File image;
    // TODO: deploy this by map logic
    
    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }
}
