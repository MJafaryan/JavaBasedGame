package models.persons;

import models.buildings.Building;
import models.buildings.House;
import models.user.Colony;

public abstract class Military extends Person {
    private int health;
    private int attackPower;

    public Military(Colony colony, House house, int health, int attackPower) {
        super(colony, house);
        this.health = health;
        this.attackPower = attackPower;
    }

    public int getHealth() {
        return health;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void attack(Military target) {
        target.defend(this.attackPower);
    }

    public void attack(Building target) {
        target.takeDamage(this.attackPower);
    }

    public void defend(int attackPower) {
        this.health -= attackPower;
    }
}
