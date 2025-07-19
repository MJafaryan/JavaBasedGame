package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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

    private TextField username;
    private TextField password;
    private TextField confirmPassword;
    private TextButton castelName;


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

    int width = Gdx.graphics.getWidth();
    int height = Gdx.graphics.getHeight();

    private void createTextFields() {
        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.BLACK;

        username = new TextField("", textFieldStyle);
        username.setPosition(570 ,525);
        username.setSize(405 , 50);

//        password = new TextField("", textFieldStyle);
//        password.setPosition();
//        password.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        password.setPasswordMode(true);
//        password.setPasswordCharacter('*');

//        confirmPassword = new TextField("", textFieldStyle);
//        confirmPassword.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight();
//        confirmPassword.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        confirmPassword.setPasswordMode(true);
//        confirmPassword.setPasswordCharacter('*');
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
        batch.draw(background, 0, 0 , width, height);
        batch.end();

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(570 , 800 , 405 , 50);
        shapeRenderer.end();

        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            Gdx.app.log("DEBUG", "کلیک در: X=" + touchX + ", Y=" + touchY);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

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
