package models.buildings;

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

    public Market(Colony colony) throws Exception {
        super(colony);
        JSONObject config = (JSONObject) SimplerJson.getDataFromJson(configFile, "house");
        HashMap<Integer> requiredMaterials = new HashMap<>();

        for (String material : Basics.MATERIALS_NAME) {
            if (SimplerJson.getDataFromJson(config, "cost_" + material) != null) {
                requiredMaterials.put(material,
                        (int) (long) SimplerJson.getDataFromJson(config, "cost_" + material));
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null
                    && requiredMaterials.get(material) > this.colony.getMaterial(material)) {
                System.out.println(material + ": " + this.colony.getMaterial(material));
                throw new Exception("No enough " + material);
            }
        }

        for (String material : Basics.MATERIALS_NAME) {
            if (requiredMaterials.get(material) != null) {
                this.colony.updateResourceAmount(material, requiredMaterials.get(material) * -1);
            }
        }

        this.health = (int) (long) SimplerJson.getDataFromJson(config, "health");
        this.tradingBox = (int) (long) SimplerJson.getDataFromJson(config, "tradingBox");
        this.products = new Product[4];

        for (int i = 0; i < this.products.length; i++) {
            String productName = Basics.WAREHOUSE[i];
            int sellingPrice = (int) (long) SimplerJson.getDataFromJson(config, "sellingPrice_" + productName);
            int buyingPrice = (int) (long) SimplerJson.getDataFromJson(config, "buyingPrice_" + productName);
            this.products[i] = new Product(productName, sellingPrice, buyingPrice);
        }
    }

    public void sellProduct(String productName, int amount) throws Exception {
        amount *= this.tradingBox;
        for (Product product : products) {
            if (product.getName().equals(productName)) {
                if (amount > this.colony.getMaterial(productName)) {
                    throw new Exception("Not enough " + productName + " in colony");
                }

                this.colony.updateResourceAmount(productName, amount * -1);
                this.colony.updateResourceAmount("coin", amount * product.getSellingPrice());
            }
        }
    }

    public void buyProduct(String productName, int amount) throws Exception {
        amount *= this.tradingBox;
        for (Product product : products) {
            if (product.getName().equals(productName)) {
                if (amount * product.getBuyingPrice() > this.colony.getBalance()) {
                    throw new Exception("Not enough coins in colony");
                }

                this.colony.updateResourceAmount(productName, amount);
                this.colony.updateResourceAmount("coin", amount * product.getBuyingPrice() * -1);
            }
        }
    }
}
