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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.*;

public class RegisterScreen implements Screen {
    private final MyGame game;
    private final Texture background;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private Stage stage;
    private BitmapFont font;

    private boolean debugMode = true; // حالت دیباگ (true = نمایش مستطیل دکمه‌ها)

    private TextField username;
    private TextField password;
    private TextField confirmPassword;
    private TextField castelName;

    // private TextButton iranDetails;
    // private TextButton arabDetails;
    // private TextButton romeDetails;
    // private TextButton mughalDetails;

    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();

    // username Field
    private int usernameFieldX = (int) (width * 0.30208);
    private int usernameFieldY = (int) (height * 0.4838);
    private int usernameFieldWidth = (int) (width * 0.2007);
    private int usernameFieldHeight = (int) (height * 0.0455);

    // password Field
    private int passwordFieldX = (int) (width * 0.30208);
    private int passwordFieldY = (int) (height * 0.42);
    private int passwordFieldWidth = (int) (width * 0.2007);
    private int passwordFieldHeight = (int) (height * 0.0455);

    // confirm password
    private int confirmPasswordFieldX = (int) (width * 0.30208);
    private int confirmPasswordFieldY = (int) (height * 0.3542);
    private int confirmPasswordFieldWidth = (int) (width * 0.2007);
    private int confirmPasswordFieldHeight = (int) (height * 0.0455);

    // castel name
    private int castelNameFieldX = (int) (width * 0.30208);
    private int castelNameFieldY = (int) (height * 0.29);
    private int castelNameFieldWidth = (int) (width * 0.2007);
    private int castelNameFieldHeight = (int) (height * 0.0455);

    // symbol iran
    private int iranSymbolFieldX = (int) (width * 0.2972);
    private int iranSymbolFieldY = (int) (height * 0.1966);
    private int iranSymbolFieldWidth = (int) (width * 0.0722);
    private int iranSymbolFieldHeight = (int) (height * 0.0744);

    // symbol arab
    private int arabSymbolFieldX = (int) (width * 0.38625);
    private int arabSymbolFieldY = (int) (height * 0.1966);
    private int arabSymbolFieldWidth = (int) (width * 0.0722);
    private int arabSymbolFieldHeight = (int) (height * 0.0744);

    // symbol rome
    private int romeSymbolFieldX = (int) (width * 0.4764);
    private int romeSymbolFieldY = (int) (height * 0.1966);
    private int romeSymbolFieldWidth = (int) (width * 0.0722);
    private int romeSymbolFieldHeight = (int) (height * 0.0744);

    // symblo mughal
    private int mughalSymbolFieldX = (int) (width * 0.56527);
    private int mughalSymbolFieldY = (int) (height * 0.1966);
    private int mughalSymbolFieldWidth = (int) (width * 0.0722);
    private int mughalSymbolFieldHeight = (int) (height * 0.0744);

    // registration button
    private int registrationButtonX = (int) (width * 0.337);
    private int registrationButtonY = (int) (height * 0.0422);
    private int registrationButtonWidth = (int) (width * 0.2604);
    private int registrationButtonHeight = (int) (height * 0.0711);

    // exit button
    private int exitButtonX = (int) (width * 0.955);
    private int exitButtonY = (int) (height * 0.944);
    private int exitButtonWidth = (int) (width * 0.0415);
    private int exitButtonHeight = (int) (width * 0.055);

    public RegisterScreen(MyGame game) {
        this.game = game;
        background = new Texture(Gdx.files.internal("register.png"));
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();
        font.getData().setScale(2f);
        createTextFields();
        Gdx.input.setInputProcessor(stage);
    }

    private void createTextFields() {
        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.BLACK;

        // فیلد نام کاربری
        username = new TextField("", textFieldStyle);
        username.setPosition(usernameFieldX, usernameFieldY);
        username.setSize(usernameFieldWidth, usernameFieldHeight);

        // فیلد رمز عبور
        password = new TextField("", textFieldStyle);
        password.setPosition(passwordFieldX, passwordFieldY);
        password.setSize(passwordFieldWidth, passwordFieldHeight);
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');

        // فیلد تاییدیه رمز عبور
        confirmPassword = new TextField("", textFieldStyle);
        confirmPassword.setPosition(confirmPasswordFieldX, confirmPasswordFieldY);
        confirmPassword.setSize(confirmPasswordFieldWidth, confirmPasswordFieldHeight);
        confirmPassword.setPasswordMode(true);
        confirmPassword.setPasswordCharacter('*');

        // نام قلعه
        castelName = new TextField("", textFieldStyle);
        castelName.setPosition(castelNameFieldX, castelNameFieldY);
        castelName.setSize(castelNameFieldWidth, castelNameFieldHeight);

        stage.addActor(username);
        stage.addActor(password);
        stage.addActor(confirmPassword);
        stage.addActor(castelName);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, width, height);
        batch.end();

        // به روزرسانی و رسم Stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        // shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // shapeRenderer.setColor(Color.RED);
        // shapeRenderer.rect(570, 800, 405, 50);
        // shapeRenderer.end();

        if (debugMode) {
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            // مستطیل دکمه Exit
            shapeRenderer.rect(
                    exitButtonX, exitButtonY,
                    exitButtonWidth, exitButtonHeight);

            // مستطیل دکمه Username
            shapeRenderer.rect(
                    usernameFieldX, usernameFieldY,
                    usernameFieldWidth, usernameFieldHeight);

            // مستطیل دکمه Password
            shapeRenderer.rect(
                    passwordFieldX, passwordFieldY,
                    passwordFieldWidth, passwordFieldHeight);

            // مستطیل دکمه confirmPassword
            shapeRenderer.rect(
                    confirmPasswordFieldX, confirmPasswordFieldY,
                    confirmPasswordFieldWidth, confirmPasswordFieldHeight);

            // مستطیل دکمه castleName
            shapeRenderer.rect(
                    castelNameFieldX, castelNameFieldY,
                    castelNameFieldWidth, castelNameFieldHeight);

            // مستطیل دکمه iranSymbol
            shapeRenderer.rect(
                    iranSymbolFieldX, iranSymbolFieldY,
                    iranSymbolFieldWidth, iranSymbolFieldHeight);

            // مستطیل دکمه arabSymbol
            shapeRenderer.rect(
                    arabSymbolFieldX, arabSymbolFieldY,
                    arabSymbolFieldWidth, arabSymbolFieldHeight);

            // مستطیل دکمه romeSymbol
            shapeRenderer.rect(
                    romeSymbolFieldX, romeSymbolFieldY,
                    romeSymbolFieldWidth, romeSymbolFieldHeight);

            // مستطیل دکمه mughalSymbol
            shapeRenderer.rect(
                    mughalSymbolFieldX, mughalSymbolFieldY,
                    mughalSymbolFieldWidth, mughalSymbolFieldHeight);

            // مستطیل دکمه registration
            shapeRenderer.rect(
                    registrationButtonX, registrationButtonY,
                    registrationButtonWidth, registrationButtonHeight);

            shapeRenderer.end();
        }

        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = height - Gdx.input.getY();
            Gdx.app.log("DEBUG", "کلیک در: X=" + touchX + ", Y=" + touchY);

            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.CONTROL_LEFT) &&
                    Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
                debugMode = !debugMode;
                Gdx.app.log("DEBUG", "حالت دیباگ: " + debugMode);
            }

            // اطلاعات ایران
            if (touchX >= iranSymbolFieldX && touchX <= iranSymbolFieldX + iranSymbolFieldWidth &&
                    touchY >= iranSymbolFieldY && touchY <= iranSymbolFieldY + iranSymbolFieldHeight) {
                Gdx.app.log("DEBUG", "clicked on Iran Symbol");
            }

            // اطلاعات عرب
            if (touchX >= arabSymbolFieldX && touchX <= arabSymbolFieldX + arabSymbolFieldWidth &&
                    touchY >= arabSymbolFieldY && touchY <= arabSymbolFieldY + arabSymbolFieldHeight) {
                Gdx.app.log("DEBUG", "clicked on Arab Symbol");
            }

            // اطلاعات روم
            if (touchX >= romeSymbolFieldX && touchX <= romeSymbolFieldX + romeSymbolFieldWidth &&
                    touchY >= romeSymbolFieldY && touchY <= romeSymbolFieldY + romeSymbolFieldHeight) {
                Gdx.app.log("DEBUG", "clicked on Rome Symbol");
            }

            // اطلاعات مغول
            if (touchX >= mughalSymbolFieldX && touchX <= mughalSymbolFieldX + mughalSymbolFieldWidth &&
                    touchY >= mughalSymbolFieldY && touchY <= mughalSymbolFieldY + mughalSymbolFieldHeight) {
                Gdx.app.log("DEBUG", "clicked on Mughal Symbol");
            }

            // ثبت نام
            if (touchX >= registrationButtonX && touchX <= registrationButtonX + registrationButtonWidth &&
                    touchY >= registrationButtonY && touchY <= registrationButtonY + registrationButtonHeight) {

            }

            // خروج از بازی
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
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);

    }

    @Override
    public void dispose() {
        background.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();

    }

}
