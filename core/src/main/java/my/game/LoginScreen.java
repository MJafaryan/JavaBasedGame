package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LoginScreen implements Screen {
    private final MyGame game;
    private final Texture background;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Stage stage;
    private final BitmapFont font;

    private TextField usernameField;
    private TextField passwordField;

    public LoginScreen(MyGame game) {
        this.game = game;
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        background = new Texture(Gdx.files.internal("login.png"));
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();
        font.getData().setScale(2f);
        createTextFields();
        Gdx.input.setInputProcessor(stage);
    }

    private void createTextFields() {
        // ایجاد استایل ساده برای TextField
        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.BLACK;

        // فیلد نام کاربری
        usernameField = new TextField("", textFieldStyle);
        usernameField.setPosition(580, 400);
        usernameField.setSize(620, 77);

        // فیلد رمز عبور
        passwordField = new TextField("", textFieldStyle);
        passwordField.setPosition(580, 250);
        passwordField.setSize(620, 77);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        stage.addActor(usernameField);
        stage.addActor(passwordField);
    }

    @Override
    public void render(float delta) {
        // پاک کردن صفحه
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // رسم پس‌زمینه
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // به روزرسانی و رسم Stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        stage.draw();

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(810 , 45 , 165 , 60);
        shapeRenderer.end();
        // مدیریت کلیک روی دکمه خروج
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (touchX >= 580 && touchX <= 580 + 600 && touchY >= 200 && touchY <= 200 + 77) {
                game.setScreen(new GameScreen(game));
            }

            if (touchX >= 810 && touchY >= 45 && touchX <= 165 + 810 && touchY <= 60 +45) {
                game.setScreen(new RegisterScreen(game));
                Gdx.app.log("Login" , "click in register");
                dispose();

            }
            // مختصات دکمه خروج (تنظیم بر اساس نیاز شما)
            if (touchX >= Gdx.graphics.getWidth() - 100 && touchX <= Gdx.graphics.getWidth() &&
                touchY >= Gdx.graphics.getHeight() - 100 && touchY <= Gdx.graphics.getHeight()) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        background.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        stage.dispose();
        font.dispose();
    }
}
