package models;

public interface Basics {
    public String BASE_DIR = "";
    public String DATA_DIR = String.format("%sdata/", BASE_DIR);
    public String MEDIA_DIR = String.format("%sassets/", BASE_DIR);

    public String[] MATERIALS_NAME = { "food", "coin", "iron", "stone", "wood" };
    public String[] UNITS_NAME = { "infantry", "archer", "cavalry", "wizard" };
    public String[] WAREHOUSE = {"food", "iron", "stone", "wood"};

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
