package models;

public interface Basics {
    public String BASE_DIR = "";
    public String DATA_DIR = String.format("%sdata/", BASE_DIR);
    public String MEDIA_DIR = String.format("%sassets/", BASE_DIR);

    public String[] MATERIALS_NAME = {"food", "gold", "iron", "stone", "wood"};

    public int BASE_TIME_PERIOD = 60000;
    public int[] TIME_COEFFICIENTS = {1, 2, 4};
}
