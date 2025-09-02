package models.buildings;

import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONObject;
import datastructures.LinkedList;
import datastructures.SimplerJson;
import models.user.Colony;
import models.Basics;
import models.persons.*;

public class House extends Building implements Runnable {
    private int population;
    private int usedPopulation;
    private Person[] personsArray;

    public House(Colony colony, Vector2 location, int height, int width) throws Exception {
        super(colony, location, height, width, "house");
        JSONObject buildingInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, "house");
        payCost((JSONObject) SimplerJson.getDataFromJson(buildingInfo, "cost"));
        this.health = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "health");
        this.personsArray = new Person[(int) (long) SimplerJson.getDataFromJson(buildingInfo, "capacity")];
        this.population = 0;
        this.colony.addEmptyHouse(this);
    }

    public int getPopulation() {
        return population;
    }

    public Person[] getPersons() {
        return this.personsArray;
    }

    public void setPersons(Person[] personsArray) {
        this.personsArray = personsArray;
    }

    public void addPerson(Person person) {
        this.personsArray[this.population] = person;
    }

    public void destroy() {
        deleteFromBuildings();
        for (Person person : this.personsArray) {
            person.setHouse(null);
            person.dead();
        }
        this.colony.setPopulation(this.colony.getPopulation() - this.usedPopulation);
        this.isAlive = false;
    }

    @Override
    public void run() {
        while (this.isAlive) {
            while (this.population < this.personsArray.length) {
                synchronized (this) {
                    this.population++;
                    this.colony.setPopulation(this.colony.getPopulation() + 1);
                }
            }

            if (this.usedPopulation == 6) {
                LinkedList<House> emptyHouses = colony.getEmptyHouses();
                emptyHouses.deleteNode(this);
                colony.setEmptyHouses(emptyHouses);
            }

            try {
                Thread.sleep(Basics.BASE_TIME_PERIOD / this.colony.getTimeConfidence());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
