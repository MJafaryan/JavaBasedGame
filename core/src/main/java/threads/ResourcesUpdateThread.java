package threads;

import models.Basics;
import models.user.Colony;

public class ResourcesUpdateThread extends Thread {
    private Colony colony;

    public ResourcesUpdateThread(Colony colony) {
        this.colony = colony;
    }

    private synchronized void updateAllResourceAmount() {
        for (String material : Basics.MATERIALS_NAME) {
            int amount = colony.getIncomes().get(material);
            try {
                colony.updateResourceAmount(material, amount);
            } catch (Exception e) {
                System.out.println("Something went wrong while updating resource: " + material);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            updateAllResourceAmount();
            this.colony.updateResourceAmount("food", colony.getUsingFoodByNPCs());
            try {
                Thread.sleep(Basics.BASE_TIME_PERIOD / colony.getTimeCoefficient());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
