package models;

public interface Basics {
    public String[] MATERIALS_NAME = { "food", "coin", "iron", "stone", "wood" };
    public String[] UNITS_NAME = { "infantry", "archer", "cavalry", "wizard" };
    public String[] WAREHOUSE = { "food", "iron", "stone", "wood" };
    public String[] BUILDINGS_NAME = { "animalhusbandry", "barracks", "farm", "goldmine", "hospital", "house",
            "ironmine", "lumbering", "market", "stonemine", "tower", "townHall" };

    public int BASE_TIME_PERIOD = 60000;
    public int[] TIME_COEFFICIENTS = { 1, 2, 4 };

    public static boolean exists(Object[] array, Object element) {
        for (Object obj : array) {
            if (obj.equals(element)) {
                return true;
            }
        }
        return false;
    }
}
