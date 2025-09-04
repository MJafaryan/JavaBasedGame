package models.buildings;

import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONObject;
import datastructures.HashMap;
import datastructures.SimplerJson;
import models.Basics;
import models.user.Colony;

public class Market extends Building {
    private class Product {
        private String name;
        private int sellingPrice;
        private int buyingPrice;

        public Product(String name, int sellingPrice, int buyingPrice) {
            this.name = name;
            this.sellingPrice = sellingPrice;
            this.buyingPrice = buyingPrice;
        }

        public String getName() {
            return name;
        }

        public int getSellingPrice() {
            return sellingPrice;
        }

        public int getBuyingPrice() {
            return buyingPrice;
        }
    }

    private int tradingBox;
    private Product[] products;

    public Market(Colony colony, Vector2 location, int height, int width) throws Exception {
        super(colony, location, height, width, "market");
        JSONObject buildingInfo = (JSONObject) SimplerJson.getDataFromJson(configFile, "market");
        payCost((JSONObject) SimplerJson.getDataFromJson(buildingInfo, "cost"));

        this.colony.addImportantBuilding("market", this);
        this.health = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "health");
        this.tradingBox = (int) (long) SimplerJson.getDataFromJson(buildingInfo, "tradingBox");
        this.products = new Product[Basics.WAREHOUSE.length];

        for (int i = 0; i < Basics.WAREHOUSE.length; i++) {
            this.products[i] = new Product(Basics.WAREHOUSE[i],
                    (int) (long) SimplerJson.getDataFromJson(buildingInfo,
                            String.format("sellingPrices_%s", Basics.WAREHOUSE[i])),
                    (int) (long) SimplerJson.getDataFromJson(buildingInfo,
                            String.format("buyingPrice_%s", Basics.WAREHOUSE[i])));
        }
    }

    @Override
    public void destroy() {
        HashMap<Building> importantBuildings = this.colony.getImportantBuildings();
        HashMap<Building> buildings = this.colony.getBuildings();
        importantBuildings.delete("hospital");
        buildings.delete(getID().toString());
        this.colony.setImportantBuildings(importantBuildings);
        this.colony.setBuildings(buildings);
    }

    public void buyProduct(String productName) throws Exception {
        for (Product product : products) {
            if (product.getName().equals(productName)) {
                if (this.tradingBox * product.getBuyingPrice() > this.colony.getBalance()) {
                    throw new Exception("Not enough coins in colony");
                }

                this.colony.updateRecourse(productName, this.tradingBox);
                this.colony.updateRecourse("coin", this.tradingBox * -1);
                return;
            }
        }
    }

    public void sellProduct(String productName) throws Exception {
        for (Product product : products) {
            if (product.getName().equals(productName)) {
                if (this.tradingBox < this.colony.getRecourse(productName)) {
                    throw new Exception("Not enough coins in colony");
                }

                this.colony.updateRecourse(productName, this.tradingBox * -1);
                this.colony.updateRecourse("coin", this.tradingBox * product.getSellingPrice());
                return;
            }
        }
    }
}
