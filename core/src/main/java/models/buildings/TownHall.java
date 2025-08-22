package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.user.Colony;
import models.user.User;

public class TownHall extends Building implements Upgradable {
    private int lvl;
    private static JSONObject config;

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "townHall");
    }

    public TownHall(Texture texture, int x, int y, int width, int height, String ironMine, Colony colony) {
        super(texture , x, y, width, height, ironMine, colony);
        this.health = (int) (long) SimplerJson.getDataFromJson(config, "lvl1_health");
        this.lvl = 1;
        colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(config, "lvl1_capacity"));
    }

    public void upgrade() throws Exception {
        JSONObject newlvl = null;

        if (lvl < 5) {
            newlvl = (JSONObject) SimplerJson.getDataFromJson(config, "lvl" + (this.lvl + 1));
        }

        payCost((JSONObject) SimplerJson.getDataFromJson(newlvl, "upgradeCost"));

        // Set Changes:
        this.lvl++;
        this.colony.setStorageCapacity((int) (long) SimplerJson.getDataFromJson(newlvl, "capacity"));
        this.health = (int) (long) SimplerJson.getDataFromJson(newlvl, "health");
    }

    public static void main(String[] args) {
        User user = new User("ali", "345");
        Colony colony = new Colony("ali", user, "pesian", 0, 0);

        TownHall townHall;
        try{
            townHall = new TownHall(null, 0, 0, 0, 0, null, colony);


            colony.setImportantBuilding("TownHall" , "townhall");
            System.out.println("set is successful");

            System.out.println("coin :" +colony.getBalance());
            System.out.println("wood :" + colony.getResources().get("wood"));
            System.out.println("iron :" + colony.getResources().get("iron"));
            System.out.println("stone :" + colony.getResources().get("stone"));
            System.out.println("food :" + colony.getResources().get("food"));
//            System.out.println("Town Hall created");
//            System.out.println("lvl th : " + townHall.lvl);
        }catch(Exception e){

        }
    }
}
