package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MenuScreen implements Screen {
    private final Texture background;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final MyGame game;

    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();

    // مختصات و ابعاد دکمه‌ها (با مقادیر اولیه)
    private int playButtonX = (int) (width*0.263);
    private int playButtonY = (int) (height*0.245);
    private int playButtonWidth = (int) (width*0.42);
    private int playButtonHeight = (int) (height*0.185);

    private int exitButtonX = (int) (width * 0.915);
    private int exitButtonY = (int) (height * 0.912);
    private int exitButtonWidth = (int) (width * 0.05625);
    private int exitButtonHeight = (int) (width * 0.05625);


    public MenuScreen(MyGame game) {
        this.game = game;
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        if (!Gdx.files.internal("vorod.png").exists()) {
            Gdx.app.error("MenuScreen", "فایل vorod.png پیدا نشد!");
            Gdx.app.exit();
        }

        background = new Texture(Gdx.files.internal("vorod.png"));
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // رسم تصویر پس‌زمینه
        batch.begin();
        batch.draw(background, 0, 0, width, height);
        batch.end();

        // نمایش مستطیل دکمه‌ها در حالت دیباگ

        // بررسی کلیک‌ها
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = height - Gdx.input.getY();


            // کلیک روی دکمه Play
            if (touchX >= playButtonX && touchX <= playButtonX + playButtonWidth &&
                touchY >= playButtonY && touchY <= playButtonY + playButtonHeight) {
                Gdx.app.log("MenuScreen", "Play button clicked");
                game.setScreen(new LoginScreen(game));
                dispose();
            }

            // کلیک روی دکمه Exit
            if (touchX >= exitButtonX && touchX <= exitButtonX + exitButtonWidth &&
                touchY >= exitButtonY && touchY <= exitButtonY + exitButtonHeight) {
                Gdx.app.log("MenuScreen", "Exit button clicked");
                Gdx.app.exit();
            }

            // چاپ مختصات کلیک (برای دیباگ)
            Gdx.app.log("DEBUG", "کلیک در: X=" + touchX + ", Y=" + touchY);
        }
    }
    @Override
    public void dispose() {
        background.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // تنظیمات تغییر اندازه
    }
    @Override
    public void show() {
        Gdx.graphics.requestRendering();// درخواست بروزرسانی فوری صفحه
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

}
