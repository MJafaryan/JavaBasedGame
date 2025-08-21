package models.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import models.persons.Worker;
import models.user.Colony;

public abstract class Mine extends Building {
    private Worker worker;

    public Mine(Texture textrue , int x , int y , int height , int weigth , String type , Colony colony) {
        super(textrue , x, y, height, weigth, type, colony);
    }

    public Worker getWorker() {
        return this.worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }
}
