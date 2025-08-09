package models.user;

import datastructures.HashMap;
import models.Basics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class Colony extends Thread implements Serializable, Basics {
    private final static String SAVING_DIR = String.format("%ssaves/", DATA_DIR);

    private String name;
    private User owner;
    private String breed;
    private int balance;
    private int storageCapacity;
    private HashMap<Integer> resources;
    private HashMap<Integer> incomes;

    public Colony(String name, User owner, String breed, int balance, int storageCapacity)
            throws IllegalArgumentException {
        if (load(String.format("%s%s.bin", SAVING_DIR, name)) != null) {
            throw new IllegalArgumentException("Colony with name " + name + " already exists.");
        }

        this.name = name;
        this.owner = owner;
        this.breed = breed;
        this.balance = balance;
        this.storageCapacity = storageCapacity;

        this.resources = new HashMap<>();
        this.incomes = new HashMap<>();
        for (String resource : MATERIALS_NAME) {
            this.incomes.put(resource, 0);
            this.resources.put(resource, 0);
        }
        this.save();
    }

    public Colony(User owner) throws IllegalArgumentException {
        Colony colony = load(String.format("%s%s.bin", SAVING_DIR, owner.getUsername()));
        if (colony != null && colony.getOwner().equals(owner)) {
            this.name = colony.getName();
            this.owner = owner;
            this.breed = colony.getBreed();
            this.balance = colony.getBalance();
            this.storageCapacity = colony.getStorageCapacity();
            this.resources = colony.getResources();
            this.incomes = colony.getIncomes();
        } else {
            throw new IllegalArgumentException("Username or password is incorrect.");
        }
    }

    private synchronized void updateAllMaterialsCount() {
        for (String material : MATERIALS_NAME) {
            updateMaterialCount(material, this.incomes.get(material));
        }
    }

    public void updateMaterialCount(String material, int amount) {
        if (Arrays.asList(MATERIALS_NAME).contains(material)) {
            if (amount > 0 && amount > getRemainingCapacity()) {
                this.resources.put(material, this.resources.get(material) + getRemainingCapacity());
            } else if (amount < 0 && this.resources.get(material) < amount) {
                this.resources.put(material, 0);
            } else {
                this.resources.put(material, this.resources.get(material) + amount);
            }
        }
    }

    private static Colony load(String fileAddress) {
        Colony colony = null;
        try (FileInputStream fileIn = new FileInputStream(fileAddress);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            colony = (Colony) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return colony;
    }

    private synchronized void save() {
        try (FileOutputStream fileOut = new FileOutputStream(
                String.format(String.format("%s%s.bin", SAVING_DIR, name)));
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getUsedCapacity() {
        int usedCapacity = 0;
        for (String resource : MATERIALS_NAME) {
            usedCapacity += this.resources.get(resource);
        }
        return usedCapacity;
    }

    public int getRemainingCapacity() {
        return storageCapacity - getUsedCapacity();
    }

    public String getColonyName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public String getBreed() {
        return breed;
    }

    public int getBalance() {
        return balance;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public HashMap<Integer> getResources() {
        return resources;
    }

    public HashMap<Integer> getIncomes() {
        return incomes;
    }

    @Override
    public void run() {
        while (true) {
            updateAllMaterialsCount();
            save();
            try {
                Thread.sleep(BASE_TIME_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
