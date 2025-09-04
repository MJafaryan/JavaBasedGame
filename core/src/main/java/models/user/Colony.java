package models.user;

import datastructures.HashMap;
import datastructures.LinkedList;
import datastructures.SimplerJson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import models.Basics;
import models.buildings.Building;
import models.buildings.House;

public class Colony extends Thread implements Serializable {
    private final static String SAVE_DIR = "data/saves/";

    private User leader;
    private String bread;
    private String colonyName;
    private int timeConfidence;
    private boolean isAlive;

    // The recourses
    private int balance;
    private int storageCapacity;
    private HashMap<Integer> recourses;

    // The citizens
    private int population;
    private HashMap<Integer> militaries;
    private int unemployedCitizens;
    private int workersAmount;

    private static int workersFoodUsage;
    private static double militariesFoodUsage;

    // The buildings
    private HashMap<Building> buildings;
    private HashMap<Building> importantBuildings;
    private LinkedList<House> emptyHouses;

    static {
        JSONObject config = SimplerJson.readJson("config/persons-config.json");
        workersFoodUsage = (int) (long) SimplerJson.getDataFromJson(config, "person_foodPer60Sec");
        militariesFoodUsage = (double) SimplerJson.getDataFromJson(config, "military_foodPer60Sec");
    }

    public Colony(User leader) throws Exception {
        Colony colony = load(leader.getUsername());
        if (colony == null) {
            throw new Exception("This user isn't exist!");
        } else if (!colony.getLeader().equals(leader)) {
            throw new Exception("Password was wrong!");
        }

        this.isAlive = true;

        this.leader = colony.getLeader();
        this.bread = colony.getBread();
        this.colonyName = colony.getColonyName();
        this.timeConfidence = 1;

        this.balance = colony.getBalance();
        this.storageCapacity = colony.getStorageCapacity();
        this.recourses = colony.getRecourses();

        this.population = colony.getPopulation();
        this.militaries = colony.getMilitaries();
        this.unemployedCitizens = colony.getUnemployedCitizens();

        this.buildings = colony.getBuildings();
        this.importantBuildings = colony.getImportantBuildings();
        this.emptyHouses = colony.getEmptyHouses();

        this.workersAmount = colony.getWorkersAmount();
    }

    public Colony(User leader, String bread, String colonyName) throws Exception {
        if (load(leader.getUsername()) != null) {
            throw new Exception("A player with this username already exists!");
        }

        this.isAlive = true;

        this.leader = leader;
        this.bread = bread;
        this.colonyName = colonyName;

        this.timeConfidence = 1;
        this.balance = 0;
        this.storageCapacity = 0;
        this.population = 0;
        this.unemployedCitizens = 0;

        this.recourses = new HashMap<>();
        this.militaries = new HashMap<>();
        this.buildings = new HashMap<>();
        this.importantBuildings = new HashMap<>();
        this.emptyHouses = new LinkedList<>();

        for (String material : Basics.WAREHOUSE) {
            this.recourses.put(material, 0);
        }

        for (String unitName : Basics.UNITS_NAME) {
            this.militaries.put(unitName, 0);
        }

        save();
    }

    public synchronized void save() {
        try (FileOutputStream fileOut = new FileOutputStream(SAVE_DIR + this.leader.getUsername() + ".bin");
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Colony load(String leaderName) {
        String path = SAVE_DIR + leaderName + ".bin";
        try (FileInputStream fileIn = new FileInputStream(path);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            return (Colony) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void defeat() {
        this.isAlive = false;
        File saveFile = new File(SAVE_DIR + this.leader.getUsername() + ".bin");
        saveFile.delete();
        try {
            ObjectOutputStream lootsFile = new ObjectOutputStream(new FileOutputStream("data/loots.bin"));
            HashMap<Integer> loots = this.recourses;
            loots.put("coin", this.balance);
            lootsFile.writeObject(loots);
            lootsFile.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public synchronized void updateRecourse(String recourseName, int amount) throws Exception {
        if (amount < 0 && (amount * -1) > getRecourse(recourseName)) {
            throw new Exception("No enough recourse " + recourseName);
        } else if (recourseName.equals("coin")) {
            this.balance += amount;
        } else if (amount > getRemainingStorageCapacity()) {
            this.recourses.put(recourseName, this.recourses.get(recourseName) + getRemainingStorageCapacity());
        } else {
            this.recourses.put(recourseName, this.recourses.get(recourseName) + amount);
        }
    }

    public void addBuilding(Building building) {
        this.buildings.put(building.getID().toString(), building);
    }

    public void addEmptyHouse(House house) {
        this.emptyHouses.addNode(house);
    }

    public void addImportantBuilding(String name, Building building) {
        this.importantBuildings.put(name, building);
    }

    @Override
    public void run() {
        try {
            while (this.isAlive) {
                updateRecourse("food",
                        (int) (getMilitariesAmount() * militariesFoodUsage + this.workersAmount * workersFoodUsage));
                save();

                Thread.sleep(Basics.BASE_TIME_PERIOD / this.getTimeConfidence());
            }
        } catch (Exception e) {
        }
    }

    // getters
    public User getLeader() {
        return this.leader;
    }

    public String getBread() {
        return this.bread;
    }

    public String getColonyName() {
        return this.colonyName;
    }

    public int getTimeConfidence() {
        return this.timeConfidence;
    }

    public int getBalance() {
        return this.balance;
    }

    public int getStorageCapacity() {
        return this.storageCapacity;
    }

    public HashMap<Integer> getRecourses() {
        return this.recourses;
    }

    public int getPopulation() {
        return this.population;
    }

    public int getRemainingStorageCapacity() {
        int amount = this.storageCapacity;
        for (String material : Basics.WAREHOUSE) {
            amount -= getRecourse(material);
        }

        return amount;
    }

    public HashMap<Integer> getMilitaries() {
        return this.militaries;
    }

    public int getUnemployedCitizens() {
        return this.unemployedCitizens;
    }

    public HashMap<Building> getBuildings() {
        return this.buildings;
    }

    public HashMap<Building> getImportantBuildings() {
        return this.importantBuildings;
    }

    public int getRecourse(String recourseName) {
        if (recourseName.equals("coin")) {
            return this.balance;
        }
        return this.recourses.get(recourseName);
    }

    public int getWorkersAmount() {
        return this.workersAmount;
    }

    public int getMilitariesAmount() {
        int amount = 0;
        for (String unit : Basics.UNITS_NAME) {
            amount += this.militaries.get(unit);
        }

        return amount;
    }

    public LinkedList<House> getEmptyHouses() {
        return this.emptyHouses;
    }

    public static List<String> getAllPlayers() {
        List<String> players = new ArrayList<>();
        File saveDir = new File(SAVE_DIR);

        // بررسی وجود پوشه saves
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            return players;
        }

        // خواندن تمام فایل‌های .bin در پوشه saves
        File[] files = saveDir.listFiles((dir, name) -> name.endsWith(".bin"));

        if (files != null) {
            for (File file : files) {
                // استخراج نام کاربری از نام فایل
                String filename = file.getName();
                String username = filename.substring(0, filename.length() - 4); // حذف .bin
                players.add(username);
            }
        }

        return players;
    }

    // Setters
    public void setEmptyHouses(LinkedList<House> newList) {
        this.emptyHouses = newList;
    }

    public void setBuildings(HashMap<Building> buildings) {
        this.buildings = buildings;
    }

    public void setPopulation(int amount) {
        this.population = amount;
    }

    public void setImportantBuildings(HashMap<Building> importantBuildings) {
        this.importantBuildings = importantBuildings;
    }

    public void setStorageCapacity(int newCapacity) {
        this.storageCapacity = newCapacity;
    }
}
