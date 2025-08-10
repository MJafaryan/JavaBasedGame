package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class InputManager {

    private OrthographicCamera camera;

    public InputManager(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void update() {
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom = Math.max(0.5f, camera.zoom - 0.01f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom = Math.min(2.0f, camera.zoom + 0.01f);
        }

        float moveSpeed = 1000 * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            camera.position.x -= moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            camera.position.x += moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            camera.position.y += moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            camera.position.y -= moveSpeed;





    }
}
