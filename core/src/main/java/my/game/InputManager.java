package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class InputManager {
    private final OrthographicCamera camera;
    private final float mapWidth;
    private final float mapHeight;
    private final float viewportWidth;
    private final float viewportHeight;

    public InputManager(OrthographicCamera camera, float mapWidth, float mapHeight,
                        float viewportWidth, float viewportHeight) {
        this.camera = camera;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    public void update() {
        // تغییر زوم
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom = Math.max(0.5f, camera.zoom - 0.01f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom = Math.min(2.0f, camera.zoom + 0.01f);
        }

        // حرکت دوربین
        float moveSpeed = 1000 * Gdx.graphics.getDeltaTime() * camera.zoom;

        float oldX = camera.position.x;
        float oldY = camera.position.y;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            camera.position.x -= moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            camera.position.x += moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            camera.position.y += moveSpeed;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            camera.position.y -= moveSpeed;

        // اعمال محدودیت‌ها
        applyCameraBounds();

        // اگر موقعیت تغییر کرد، دوربین را آپدیت کن
        if (camera.position.x != oldX || camera.position.y != oldY) {
            camera.update();
        }
    }

    public void handleMouseScroll(float amountY) {
        // کنترل زوم با اسکرول ماوس
        float zoomChange = amountY * 0.1f;
        camera.zoom = MathUtils.clamp(camera.zoom + zoomChange, 0.5f, 2.0f);
        applyCameraBounds();
        camera.update();
    }

    private void applyCameraBounds() {
        // محاسبه مرزهای مجاز با توجه به زوم و اندازه viewport
        float effectiveViewportWidth = viewportWidth * camera.zoom;
        float effectiveViewportHeight = viewportHeight * camera.zoom;

        float minX = effectiveViewportWidth / 2;
        float maxX = mapWidth - effectiveViewportWidth / 2;
        float minY = effectiveViewportHeight / 2;
        float maxY = mapHeight - effectiveViewportHeight / 2;

        // محدود کردن موقعیت دوربین
        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
    }
}
