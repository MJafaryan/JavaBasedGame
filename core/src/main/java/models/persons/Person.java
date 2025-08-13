package models.persons;

import models.buildings.House;
import models.user.Colony;
import java.util.UUID;

public abstract class Person {
    private UUID id;
    private House house;
    private Colony colony;

    public Person(Colony colony, House house) {
        this.house = house;
        this.colony = colony;
        this.id = UUID.randomUUID();
    }

    public Colony getColony() {
        return colony;
    }

    public House getHouse() {
        return this.house;
    }

    public UUID getID() {
        return this.id;
    }
}
