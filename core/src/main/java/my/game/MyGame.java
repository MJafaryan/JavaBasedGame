package my.game;

import com.badlogic.gdx.Game;

public class MyGame extends Game {
    @Override
    public void create() {
        // فقط یک صفحه ساده با تصویر نمایش می‌دهد
        setScreen(new RegisterScreen(this));
    }
}
