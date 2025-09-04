package models.buildings;

public interface Upgradable {
    abstract void upgrade() throws Exception;
    abstract int getLevel();
    abstract int getMaxLevel();
}
