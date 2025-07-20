package models.buildings;

public class Building {
    private int health;
    // TODO: deploy this by map logic
    
    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }
}
