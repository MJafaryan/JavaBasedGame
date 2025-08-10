package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class GameScreen implements Screen {

    private MyGame game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private ExtendViewport viewport;
    private OrthographicCamera camera;

    private static final float WORLD_WIDTH = 1000;
    private static final float WORLD_HEIGHT = 1000;

    private InputManager inputManager;

    public GameScreen(MyGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

        map = new TmxMapLoader().load("Map/Map1/Map1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        inputManager = new InputManager(camera);
    }

    @Override
    public void render(float delta) {
        inputManager.update(); // بررسی ورودی‌ها

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void dispose() {
        if (map != null)
            map.dispose();
        if (renderer != null)
            renderer.dispose();
    }
}
