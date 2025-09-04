package models.buildings;

import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONObject;

import datastructures.HashMap;
import datastructures.SimplerJson;
import models.persons.Military;
import models.user.Colony;

public class Tower extends Building {
    private JSONObject attachments;
    private boolean hasFence = false;
    private boolean hasFirePeat = false;
    private Military[] archers;

    public Tower(Colony colony, Vector2 location, int height, int width) throws Exception {
        super(colony, location, height, width, "tower",1);
        JSONObject buildingInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, "tower");
        payCost((JSONObject) SimplerJson.getDataFromJson(buildingInfo, "cost"));
        this.health = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "health");
        this.attachments = (JSONObject) SimplerJson.getDataFromJson(buildingInfo, "attachments");
        this.archers = new Military[(int) (long) SimplerJson.getDataFromJson(buildingInfo, "capacity")];
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

    public void addArcher(Military archer) throws Exception {
        for (int i = 0; i < this.archers.length; i++) {
            if (this.archers[i] == null) {
                this.archers[i] = archer;
                return;
            }
        }
        throw new Exception("The capacity of tower is full.");
    }

    @Override
    public void destroy() {
        HashMap<Building> buildings = this.colony.getBuildings();
        buildings.delete(getID().toString());
        this.colony.setBuildings(buildings);

        for (Military archer : this.archers) {
            archer.dead();
        }
    }
}
