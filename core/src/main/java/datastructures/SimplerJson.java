package datastructures;

import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SimplerJson {
    public static JSONObject readJson(String fileAddress) {
        StringBuilder fileContent = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileAddress));
            String line;

            while ((line = reader.readLine()) != null)
                fileContent.append(line);
            reader.close();
        } catch (Exception e) {
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(fileContent.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getDataFromJson(JSONObject object, String dataAddress) { // user enter a data address as
                                                                                  // "townHall_lvl1_health"
        String[] stringArray = dataAddress.split("_");
        int index = 0;

        for (String addressPart : stringArray) {
            if (index == stringArray.length - 1) {
                return object.get(addressPart);
            }
            object = (JSONObject) object.get(addressPart);
            index++;
        }

        return null;
    }
}
