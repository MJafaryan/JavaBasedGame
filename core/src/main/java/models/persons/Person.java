package models.persons;

import models.buildings.House;
import models.user.Colony;

public abstract class Person {
    private House house;
    private Colony colony;
    private boolean isAlive;

    public Person(Colony colony, House house) {
        this.house = house;
        this.colony = colony;
        this.isAlive = true;
    }

    public Colony getColony() {
        return colony;
    }

    public House getHouse() {
        return this.house;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public void dead() {
        this.isAlive = false;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
