package models.user;

import datastructures.HashMap;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import models.Basics;
import models.buildings.Building;

public class Colony implements Serializable {
    private final static String SAVING_DIR = String.format("%s%s/", Basics.DATA_DIR, "saves");
    private int timeCoefficient;

    // The global info of a colony
    private String name;
    private String bread;
    private User leader;
    private int balance;

    // The storage and resources of the colony
    private int storageCapacity;
    private HashMap<Integer> resources;
    private HashMap<Integer> incomes;
    private int usingFoodByNPCs;

    // Buildings
    private HashMap<Building> buildings;
    private HashMap<String> importantBuildingsCode;

    // NPCs
    private int maximumPossiblePopulation;
    private int population;
    private int workersPopulation;
    private int militariesPopulation;
    private HashMap<Integer> militaries;

    // Constructors
    public Colony(String name, User leader, String breed, int storageCapacity, int balance)
        throws IllegalArgumentException {
        if (Colony.load(leader.getUsername()) != null) {
            throw new IllegalArgumentException("Colony already exists");
        }

        this.name = name;
        this.leader = leader;
        this.bread = breed;
        this.storageCapacity = storageCapacity;
        this.balance = balance;
        this.resources = new HashMap<>();
        this.incomes = new HashMap<>();
        this.usingFoodByNPCs = 0;
        this.buildings = new HashMap<>();
        this.importantBuildingsCode = new HashMap<>();
        this.maximumPossiblePopulation = 0;
        this.population = 0;
        this.workersPopulation = 0;
        this.militariesPopulation = 0;
        this.militaries = new HashMap<>();
        this.timeCoefficient = 1;

        // Initialize all resources safely
        initializeAllResources();

        for (String unit : Basics.UNITS_NAME) {
            this.militaries.put(unit, 0);
        }

        save();
    }

    public Colony(User leader) throws IllegalArgumentException {
        Colony colony = Colony.load(leader.getUsername());
        if (colony == null) {
            throw new IllegalArgumentException("Colony not found");
        } else if (!colony.getLeader().equals(leader)) {
            throw new IllegalArgumentException("Colony does not belong to this user");
        } else {
            this.name = colony.getName();
            this.bread = colony.getBread();
            this.leader = colony.getLeader();
            this.storageCapacity = colony.getStorageCapacity();
            this.balance = colony.getBalance();
            this.resources = colony.getResources();
            this.incomes = colony.getIncomes();
            this.usingFoodByNPCs = colony.getUsingFoodByNPCs();
            this.buildings = colony.getBuildings();
            this.importantBuildingsCode = colony.getImportantBuildingsCode();
            this.maximumPossiblePopulation = colony.getMaximumPossiblePopulation();
            this.population = colony.getPopulation();
            this.workersPopulation = colony.getWorkersPopulation();
            this.militariesPopulation = colony.getMilitariesPopulation();
            this.militaries = colony.getMilitaries();
            this.timeCoefficient = colony.getTimeCoefficient();
        }
    }

    // Initialize all possible resources with default value 0
    private void initializeAllResources() {
        // Initialize materials from Basics
        for (String material : Basics.MATERIALS_NAME) {
            this.resources.put(material, 0);
            this.incomes.put(material, 0);
        }

        // Initialize common resources that might be used
        String[] commonResources = {"wood", "stone", "gold", "iron", "food"};
        for (String resource : commonResources) {
            if (!this.resources.containsKey(resource)) {
                this.resources.put(resource, 0);
            }
            if (!this.incomes.containsKey(resource)) {
                this.incomes.put(resource, 0);
            }
        }
    }

    // Save and load colonies
    public synchronized void save() {
        try (FileOutputStream fileOut = new FileOutputStream(
            String.format("%s%s.bin", SAVING_DIR, this.leader.getUsername()));
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Colony load(String leaderName) {
        try (FileInputStream fileIn = new FileInputStream(
            String.format("%s%s.bin", SAVING_DIR, leaderName));
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            return (Colony) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    // Getters
    public User getLeader() {
        return this.leader;
    }

    public String getName() {
        return this.name;
    }

    public String getBread() {
        return this.bread;
    }

    public int getStorageCapacity() {
        return this.storageCapacity;
    }

    public int getBalance() {
        return this.balance;
    }

    public HashMap<Integer> getResources() {
        return this.resources;
    }

    public HashMap<Integer> getIncomes() {
        return this.incomes;
    }

    public int getUsingFoodByNPCs() {
        return this.usingFoodByNPCs;
    }

    public HashMap<Building> getBuildings() {
        return this.buildings;
    }

    public int getMaximumPossiblePopulation() {
        return this.maximumPossiblePopulation;
    }

    public int getPopulation() {
        return this.population;
    }

    public int getWorkersPopulation() {
        return this.workersPopulation;
    }

    public int getMilitariesPopulation() {
        return this.militariesPopulation;
    }

    public HashMap<Integer> getMilitaries() {
        return this.militaries;
    }

    public int getTimeCoefficient() {
        return this.timeCoefficient;
    }

    public HashMap<String> getImportantBuildingsCode() {
        return this.importantBuildingsCode;
    }

    // Special getters
    public int getUsedCapacity() {
        int usedCapacity = 0;
        for (String material : Basics.WAREHOUSE) {
            Integer amount = this.resources.get(material);
            if (amount != null) {
                usedCapacity += amount;
            }
        }
        return usedCapacity;
    }

    public int getMaterial(String materialName) {
        if (materialName.equals("coin")) {
            return this.balance;
        } else {
            Integer value = this.resources.get(materialName);
            if (value == null) {
                // If resource doesn't exist, create it and return 0
                this.resources.put(materialName, 0);
                return 0;
            }
            return value;
        }
    }

    public Building getBuilding(String id) {
        return this.buildings.get(id);
    }

    // Check if resource exists
    public boolean hasResource(String resourceName) {
        if (resourceName.equals("coin")) {
            return true;
        }
        return this.resources.containsKey(resourceName);
    }

    // Setters
    public void setStorageCapacity(int storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setUsingFoodByNPCs(int amount) {
        this.usingFoodByNPCs = amount;
    }

    public void setMaximumPossiblePopulation(int maximumPossiblePopulation) {
        this.maximumPossiblePopulation = maximumPossiblePopulation;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setWorkersPopulation(int workersPopulation) {
        this.workersPopulation = workersPopulation;
    }

    public void setMilitariesPopulation(int militariesPopulation) {
        this.militariesPopulation = militariesPopulation;
    }

    public void setImportantBuildingsCode(HashMap<String> newHashMap) {
        this.importantBuildingsCode = newHashMap;
    }

    // Special setters
    public void setResource(String material, int amount) throws IllegalArgumentException {
        if (!Basics.exists(Basics.WAREHOUSE, material)) {
            throw new IllegalArgumentException("Invalid material: " + material);
        }
        this.resources.put(material, amount);
    }

    public void setIncome(String material, int amount) throws IllegalArgumentException {
        if (!Basics.exists(Basics.MATERIALS_NAME, material)) {
            throw new IllegalArgumentException("Invalid material: " + material);
        }
        this.incomes.put(material, amount);
    }

    public void setMilitary(String type, int amount) throws IllegalArgumentException {
        if (!Basics.exists(Basics.UNITS_NAME, type)) {
            throw new IllegalArgumentException("Invalid military type: " + type);
        }
        this.militaries.put(type, amount);
    }

    public void setImportantBuilding(String name, String key) {
        this.importantBuildingsCode.put(name, key);
    }

    // Other functions
    public synchronized void updateResourceAmount(String resourceName, int amount)
        throws IllegalArgumentException {
        if (!resourceName.equals("coin")) {
            // Ensure resource exists
            if (!this.resources.containsKey(resourceName)) {
                this.resources.put(resourceName, 0);
            }

            int currentAmount = this.resources.get(resourceName);

            if (amount > 0 && amount > this.storageCapacity - getUsedCapacity()) {
                this.resources.put(resourceName, this.storageCapacity - getUsedCapacity());
            } else if (amount < 0 && Math.abs(amount) > currentAmount) {
                throw new IllegalArgumentException("Insufficient resources: " + resourceName);
            } else {
                this.resources.put(resourceName, currentAmount + amount);
            }
        } else if (amount < 0 && Math.abs(amount) > this.balance) {
            throw new IllegalArgumentException("Insufficient balance");
        } else {
            this.balance += amount;
        }
    }

    public synchronized void addBuilding(Building newBuilding) {
        this.buildings.put(newBuilding.getID().toString(), newBuilding);
    }

    // Safe method to get material with default value
    public int getMaterialSafe(String materialName, int defaultValue) {
        if (materialName.equals("coin")) {
            return this.balance;
        } else {
            Integer value = this.resources.get(materialName);
            return value != null ? value : defaultValue;
        }
    }

    // Safe method to update resource with null check
    public synchronized boolean safeUpdateResource(String resourceName, int amount) {
        try {
            updateResourceAmount(resourceName, amount);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (NullPointerException e) {
            // If resource doesn't exist, create it and try again
            if (!resourceName.equals("coin")) {
                this.resources.put(resourceName, 0);
                try {
                    updateResourceAmount(resourceName, amount);
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }
            return false;
        }
    }
}
