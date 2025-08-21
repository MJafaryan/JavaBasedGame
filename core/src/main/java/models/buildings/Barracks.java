package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import org.json.simple.JSONArray;
import models.user.Colony;

public class Barracks extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;
    private JSONArray unavailableUnits;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "barracks");
    }

    public Barracks(Texture texture, int x, int y, int width, int height, String barracks, Colony colony) throws Exception {
        super(texture , x, y, width, height, barracks, colony);

        payCost((JSONObject) SimplerJson.getDataFromJson(config, "lvl1_cost"));

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.lvl = 1;
        this.unavailableUnits = (JSONArray) SimplerJson.getDataFromJson(config, "lvl1_unavailableUnits");
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl == 1) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + (this.lvl + 1));
        }

        payCost((JSONObject) SimplerJson.getDataFromJson(newlvl, "cost"));

        // Set Changes:
        this.lvl = 2;
        this.unavailableUnits = null;
    }

    public JSONArray getUnavailableUnits() {
        return this.unavailableUnits;
    }

    // public static void main(String[] args) {
    //     User user = new User("test", "123");
    //     Colony colony = new Colony("test", user, "tester", 300, 500);
    //     colony.updateResourceAmount("wood", 100);
    //     colony.updateResourceAmount("stone", 100);

    //     System.out.println("coin: " + colony.getBalance());
    //     System.out.println("wood: " + colony.getMaterial("wood"));
    //     System.out.println("stone: " + colony.getMaterial("stone"));
    //     Barracks barracks;
    //     try {
    //         barracks = new Barracks(colony);
    //         System.out.println("coin: " + colony.getBalance());
    //         System.out.println("wood: " + colony.getMaterial("wood"));
    //         System.out.println("stone: " + colony.getMaterial("stone"));

    //         barracks.upgrade();
    //         System.out.println("coin: " + colony.getBalance());
    //         System.out.println("wood: " + colony.getMaterial("wood"));
    //         System.out.println("stone: " + colony.getMaterial("stone"));
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}
