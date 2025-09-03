package models.persons;

import org.json.simple.JSONObject;
import datastructures.SimplerJson;
import models.Basics;
import models.user.Colony;

public class Hero {
    // private String name;

    // public Hero(Colony colony) {
    //     super(colony, null, getAttackPowerFromConfigFile(colony.getBread()), getHealthFromConfigFile(colony.getBread()));
    //     JSONObject jsonFile = SimplerJson.readJson(String.format("%sconfigs/person-config.json", Basics.DATA_DIR));
    //     this.name = (String) SimplerJson.getDataFromJson(jsonFile, String.format("military_%s_hero_name", colony.getBread()));
    // }

    // public static int getAttackPowerFromConfigFile(String bread) {
    //     JSONObject jsonFile = SimplerJson.readJson(String.format("%sconfigs/person-config.json", Basics.DATA_DIR));
    //     return (int) (long) SimplerJson.getDataFromJson(jsonFile, String.format("military_%s_hero_power", bread));
    // }

    // public static int getHealthFromConfigFile(String bread) {
    //     JSONObject jsonFile = SimplerJson.readJson(String.format("%sconfigs/person-config.json", Basics.DATA_DIR));
    //     return (int) (long) SimplerJson.getDataFromJson(jsonFile, String.format("military_%s_hero_health", bread));
    // }

    // public String getName() {
    //     return this.name;
    // }
}
