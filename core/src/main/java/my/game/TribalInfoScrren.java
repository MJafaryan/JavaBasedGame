package my.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class TribalInfoScrren implements Screen {
    private MyGame game;
    private SpriteBatch batch;
    private Texture background;
    private Stage stage;
    private String tribeName;

    public TribalInfoScrren(MyGame game, String tribeName) {
        this.game = game;
        this.tribeName = tribeName;
        switch(tribeName){
            case "IRAN" :
                this.background = new Texture(Gdx.files.internal("IRAN.png"));
                break;
            case "ARAB" :
                this.background = new Texture(Gdx.files.internal("ARAB.png"));
                break;
            case "ROME" :
                this.background = new Texture(Gdx.files.internal("ROME.png"));
                break;

            case "MONGOL" :
                this.background = new Texture(Gdx.files.internal("MONGOL.png"));
                break;
        }
        this.batch = new SpriteBatch();
        this.stage = new Stage();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.input.getY();
            Gdx.app.log("DEBUG", "کلیک در: X=" + touchX + ", Y=" + touchY);

            if (touchX >= 1745 && touchX <= 1841 && touchY >= 11 && touchY <= 52 ) {
                game.setScreen(new RegisterScreen(game));
            }

            if (touchX >=1862 && touchX <=1912 && touchY >= 11 && touchY <= 52 ) {
                Gdx.app.exit();
            }
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        background.dispose();


    }
}
