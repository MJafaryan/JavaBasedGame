package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import models.Basics;
import models.user.Colony;
import models.user.User;

public class RegisterScreen implements Screen {
    private final MyGame game;
    private final Texture background;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Stage stage;
    private final BitmapFont font;

    private boolean debugMode = true; // حالت دیباگ (true = نمایش مستطیل دکمه‌ها)

    private TextField username;
    private TextField password;
    private TextField confirmPassword;
    private TextField castleName;

    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();

    private CheckBox iran;
    private CheckBox rome;
    private CheckBox mongol;
    private CheckBox arab;

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
    private int exitButtonHeight = (int) (height * 0.055);

    public RegisterScreen(MyGame game) {
        this.game = game;
        this.background = new Texture(Gdx.files.internal("register.png"));
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.stage = new Stage(new ScreenViewport());
        this.font = new BitmapFont();

        initialize();
    }

    private void initialize() {
        font.getData().setScale(2f);
        createTextFields();
        createCheckBox();
        Gdx.input.setInputProcessor(stage);
    }

    private Drawable createColoredDrawable(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private void createCheckBox() {
        CheckBoxStyle checkBoxStyle = new CheckBoxStyle();
        checkBoxStyle.font = font;
        checkBoxStyle.fontColor = Color.BLACK;

        int boxSize = 20;
        checkBoxStyle.checkboxOff = createColoredDrawable(boxSize, 20, Color.WHITE);
        checkBoxStyle.checkboxOn = createColoredDrawable(boxSize, 20, Color.GREEN);

        iran = new CheckBox("Iran", checkBoxStyle);
        arab = new CheckBox("Arab", checkBoxStyle);
        rome = new CheckBox("Rome", checkBoxStyle);
        mongol = new CheckBox("Mongol", checkBoxStyle);

        iran.setPosition((float) (0.307 * width), (float) (0.157 * height));
        arab.setPosition((float) (0.4 * width), (float) (0.157 * height));
        rome.setPosition((float) (0.486 * width), (float) (0.157 * height));
        mongol.setPosition((float) (0.577 * width), (float) (0.157 * height));

        stage.addActor(iran);
        stage.addActor(arab);
        stage.addActor(rome);
        stage.addActor(mongol);

    }

    private void createTextFields() {
        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.BLACK;

        username = createTextField("", usernameFieldX, usernameFieldY, usernameFieldWidth, usernameFieldHeight,
                textFieldStyle);
        password = createTextField("", passwordFieldX, passwordFieldY, passwordFieldWidth, passwordFieldHeight,
                textFieldStyle);
        confirmPassword = createTextField("", confirmPasswordFieldX, confirmPasswordFieldY, confirmPasswordFieldWidth,
                confirmPasswordFieldHeight, textFieldStyle);
        castleName = createTextField("", castelNameFieldX, castelNameFieldY, castelNameFieldWidth,
                castelNameFieldHeight, textFieldStyle);

        password.setPasswordMode(true);
        password.setPasswordCharacter('*');
        confirmPassword.setPasswordMode(true);
        confirmPassword.setPasswordCharacter('*');
    }

    private TextField createTextField(String text, float x, float y, float width, float height, TextFieldStyle style) {
        TextField textField = new TextField(text, style);
        textField.setPosition(x, y);
        textField.setSize(width, height);
        stage.addActor(textField);
        return textField;
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(exitButtonX, exitButtonY, exitButtonWidth, exitButtonHeight);
        shapeRenderer.end();

        // Draw stage (text fields)
        stage.act(delta);
        stage.draw();

        int touchX = Gdx.input.getX();
        int touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Debug touch input
        if (Gdx.input.justTouched()) {
            Gdx.app.log("DEBUG", "کلیک در: X=" + touchX + ", Y=" + touchY);

            if ((touchX >= exitButtonX && touchX <= exitButtonX + exitButtonWidth && touchY >= exitButtonY
                    && touchY <= exitButtonY + exitButtonHeight)) {
                Gdx.app.exit();
            }

            if (touchX >= iranSymbolFieldX && touchX <= iranSymbolFieldX + iranSymbolFieldWidth
                    && touchY >= iranSymbolFieldY && touchY <= iranSymbolFieldY + iranSymbolFieldHeight)
                game.setScreen(new TribalInfoScrren(game, "IRAN"));

            if (touchX >= arabSymbolFieldX && touchX <= arabSymbolFieldX + arabSymbolFieldWidth
                    && touchY >= arabSymbolFieldY && touchY <= arabSymbolFieldY + arabSymbolFieldHeight)
                game.setScreen(new TribalInfoScrren(game, "ARAB"));

            if ((touchX >= romeSymbolFieldX && touchX <= romeSymbolFieldX + romeSymbolFieldWidth
                    && touchY >= romeSymbolFieldY && touchY <= romeSymbolFieldY + romeSymbolFieldHeight))
                game.setScreen(new TribalInfoScrren(game, "ROME"));

            if (touchX >= mughalSymbolFieldX && touchX <= mughalSymbolFieldX + mughalSymbolFieldWidth
                    && touchY >= mughalSymbolFieldY && touchY <= mughalSymbolFieldY + mughalSymbolFieldHeight)
                game.setScreen(new TribalInfoScrren(game, "MONGOL"));

            if (touchX >= registrationButtonX && touchX <= registrationButtonX + registrationButtonWidth &&
                    touchY >= registrationButtonY && touchY <= registrationButtonY + registrationButtonHeight) {
                String userName = this.username.getText();
                String password = this.password.getText();
                String confirmPassword = this.confirmPassword.getText();
                String ctlName = this.castleName.getText();
                String civilization = getSelectedCivilization();

                if (password.equals(confirmPassword)) {
                    try {
                        Colony colony = new Colony(new User(userName, password), civilization, ctlName);
                        colony = initializeColonyResources(colony);
                        game.setScreen(new GameScreen(game, colony));
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
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
        // Optional: Implement if needed
    }

    @Override
    public void resume() {
        // Optional: Implement if needed
    }

    @Override
    public void dispose() {
        background.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        stage.dispose();
    }

    public String getSelectedCivilization() {
        if (iran.isChecked()) {
            return "Iran";
        }
        if (arab.isChecked()) {
            return "arab";
        }
        if (rome.isChecked()) {
            return "rome";
        }
        if (mongol.isChecked()) {
            return "mongol";
        }
        return "";
    }

    private Colony initializeColonyResources(Colony colony) {
        try {
            for (String resource : Basics.WAREHOUSE) {
                colony.updateRecourse(resource, 50);
            }
            colony.updateRecourse("coin", 500);
        } catch (Exception e) {
        }
        return colony;
    }
}
