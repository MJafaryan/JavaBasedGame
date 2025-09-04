package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import models.user.Colony;
import models.user.User;

public class LoginScreen implements Screen {
    private final MyGame game;
    private final Texture background;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Stage stage;
    private final BitmapFont font;

    private boolean debugMode = true; // حالت دیباگ (true = نمایش مستطیل دکمه‌ها)

    private TextField usernameField;
    private TextField passwordField;

    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();

    private int registerButtonX = (int) (width * 0.423);
    private int registerButtonY = (int) (height * 0.038);
    private int registerButtonWidth = (int) (width * 0.084);
    private int registerButtonHeight = (int) (height * 0.056);

    private int exitButtonX = (int) (width * 0.9736);
    private int exitButtonY = (int) (height * 0.9627);
    private int exitButtonWidth = (int) (width * 0.0245);
    private int exitButtonHeight = (int) (width * 0.0245);

    private int usernameFieldX = (int) (width * 0.30625);
    private int usernameFieldY = (int) (height * 0.369);
    private int usernameFieldWidth = (int) (width * 0.314);
    private int usernameFieldHeight = (int) (height * 0.0722);

    private int passwordFieldX = (int) (width * 0.30625);
    private int passwordFieldY = (int) (height * 0.23);
    private int passwordFieldWidth = (int) (width * 0.314);
    private int passwordFieldHeight = (int) (height * 0.0722);

    private int loginToGameButtonX = (int) (width * 0.3058);
    private int loginToGameButtonY = (int) (height * 0.1136);
    private int loginToGameButtonWidth = (int) (width * 0.3145);
    private int loginToGameButtonHeight = (int) (height * 0.0911);

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
        usernameField.setPosition(usernameFieldX, usernameFieldY);
        usernameField.setSize(usernameFieldWidth, usernameFieldHeight);

        // فیلد رمز عبور
        passwordField = new TextField("", textFieldStyle);
        passwordField.setPosition(passwordFieldX, passwordFieldY);
        passwordField.setSize(passwordFieldWidth, passwordFieldHeight);
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
        batch.draw(background, 0, 0, width, height);
        batch.end();

        // به روزرسانی و رسم Stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // مدیریت کلیک روی دکمه خروج
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = height - Gdx.input.getY();
//            Gdx.app.log("DEBUG", "کلیک در: X=" + touchX + ", Y=" + touchY);
//
//            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT) &&
//                Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
//                debugMode = !debugMode;
//                Gdx.app.log("DEBUG", "حالت دیباگ: " + debugMode);
//            }

            if (touchX >= loginToGameButtonX && touchX <= loginToGameButtonX + loginToGameButtonWidth
                && touchY >= loginToGameButtonY && touchY <= loginToGameButtonY + loginToGameButtonHeight) {
                try {
                    Colony colony = new Colony(new User(this.usernameField.getText(), this.passwordField.getText()));
                    System.out.println(colony);
                    game.setScreen(new GameScreen(game, colony));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (touchX >= registerButtonX && touchY >= registerButtonY
                && touchX <= registerButtonX + registerButtonWidth
                && touchY <= registerButtonY + registerButtonHeight) {
                game.setScreen(new RegisterScreen(game));
                Gdx.app.log("Login", "click in register");
                dispose();

            }
            // مختصات دکمه خروج (تنظیم بر اساس نیاز شما)
            if (touchX >= exitButtonX && touchX <= exitButtonX + exitButtonWidth &&
                touchY >= exitButtonY && touchY <= exitButtonY + exitButtonHeight) {
                Gdx.app.exit();
            }
        }
    }

    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.D && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            debugMode = !debugMode;
            Gdx.app.log("DEBUG", "Debug mode: " + debugMode);
            return true;
        }
        return false;
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
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
