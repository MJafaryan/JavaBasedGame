package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import org.json.simple.JSONObject;

import datastructures.SimplerJson;
import models.user.Colony;
import models.user.User;

public class Tower extends Building {
    private static JSONObject config;
    private JSONObject attachments;
    private boolean hasFence = false;
    private boolean hasFirePeat = false;
    // TODO : adding Capacity of archers
    // TODO : add picture to map

    static {
        config = (JSONObject) SimplerJson.getDataFromJson(configFile, "tower");
    }

    public Tower(Texture textrue , int x , int y , int height , int weigth , String type , Colony colony) throws Exception {
        super(textrue , x , y ,height , weigth , "tower" , colony);
        this.attachments = (JSONObject) SimplerJson.getDataFromJson(config, "attachments");

        payCost((JSONObject) SimplerJson.getDataFromJson(config, "cost"));

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
    }

    public void addFirePeat() throws Exception {
        if (!hasFirePeat) {
            JSONObject firePeat = (JSONObject) SimplerJson.getDataFromJson(attachments, "firePeat");
            // pay cost
            payCost((JSONObject) SimplerJson.getDataFromJson(firePeat, "cost"));
            this.hasFirePeat = true;

            // increase power attack
            String influentialIn = (String) SimplerJson.getDataFromJson(firePeat, "influentialIn");
        }
    }

    public void addFence() throws Exception {
        if (!hasFence){
            JSONObject fence = (JSONObject) SimplerJson.getDataFromJson(attachments, "fence");
            boolean hasFence = false;
            // pay cost
            payCost((JSONObject) SimplerJson.getDataFromJson(fence, "cost"));
            this.hasFence = true;

            // increase health of build
            // String influentialIn = (String) SimplerJson.getDataFromJson(fence,
            // "influentialIn");
            int count = (int) (long) SimplerJson.getDataFromJson(fence, "count");
            this.health += count;
        }
    }

    public void addArcher() {
    }
}
