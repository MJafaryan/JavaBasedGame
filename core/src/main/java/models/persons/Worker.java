package models.persons;

import models.buildings.*;
import models.user.Colony;

public class Worker extends Person {
    private Building workspaceBuilding;

    public Worker(Colony colony, Building workspaceBuilding, House house) {
        super(colony, house);
        this.workspaceBuilding = workspaceBuilding;
    }

    public Building getWorkingPlace() {
        return this.workspaceBuilding;
    }

    public void setWorkSpace(Building workingPlace) {
        this.workspaceBuilding = workingPlace;
    }
}
