package models.buildings;

import models.persons.Worker;
import models.user.Colony;

public abstract class Mine extends Building {
    private Worker worker;

    public Mine(Colony colony) {
        super(colony);
    }

    public Worker getWorker() {
        return this.worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }
}
