package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;
import models.Basics;
import models.persons.*;

public class House extends Building implements Runnable {
    private int maxPopulation;
    private int population;
    private Person[] personsArray;

    public House(Texture texture, int x , int y , int wi, int height, String type, Colony colony) throws Exception {
        super(texture, x, y, wi, height, type, colony);
        JSONObject config = (JSONObject) SimplerJson.getDataFromJson(configFile, "house");

        payCost((JSONObject) SimplerJson.getDataFromJson(config, "cost"));

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.maxPopulation = (int) (long) SimplerJson.getDataFromJson(config, "capacity");
        this.colony.setMaximumPossiblePopulation(this.maxPopulation + this.colony.getMaximumPossiblePopulation());
        this.personsArray = new Person[this.maxPopulation];
        this.population = 0;
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

    @Override
    public void run() {
        while (this.population < this.maxPopulation) {
            this.population++;
            this.colony.setPopulation(this.colony.getPopulation() + 1);
            try {
                Thread.sleep(Basics.BASE_TIME_PERIOD / this.colony.getTimeCoefficient());
            } catch (Exception e) {
                // TODO: handle the exception
            }
        }
    }
}
