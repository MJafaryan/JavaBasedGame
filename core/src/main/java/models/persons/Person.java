package models.persons;

public class Person extends Thread {
    // private Object home; TODO: deploy home
    private int foodUsage;

    // public Person(int foodUsage, Object home) {
    //     this.foodUsage = foodUsage;
    //     this.home = home;
    // }

    protected void useFood() {
        // TODO: decrease town food by foodUsage
    }

    public void close() {
        interrupt();
    }

    @Override
    public void run() {
        while (true) {
            useFood();
            try {
                Thread.sleep(60);
            } catch (Exception e) {
                System.err.println("Something was wrong...");
            }
        }
    }
}
