package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;

public class House extends Building {
    private int populationCapacity;

    public House(Texture texture, int x, int y, int width, int height, String type, Colony colony) throws Exception {
        super(texture, x, y, width, height, type, colony);
        JSONObject config = (JSONObject) SimplerJson.getDataFromJson(configFile, "house");

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.populationCapacity = (int) (long) SimplerJson.getDataFromJson(config, "capacity");

        // مستقیماً ۶ نفر به جمعیت اضافه می‌کنیم
        synchronized (colony) {
            colony.setMaximumPossiblePopulation(colony.getMaximumPossiblePopulation() + this.populationCapacity);
            colony.setPopulation(colony.getPopulation() + this.populationCapacity);
            System.out.println("House built: +" + populationCapacity + " population. Total: " + colony.getPopulation());
        }
    }

    public int getPopulationCapacity() {
        return populationCapacity;
    }
}
